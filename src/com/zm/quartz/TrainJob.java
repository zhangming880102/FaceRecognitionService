package com.zm.quartz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zm.bean.ClassifierBean;
import com.zm.bean.TrainImgPathBean;
import com.zm.bean.TrainScheduler;
import com.zm.classifier.MyClassifier;
import com.zm.dao.ManagerReviewDao;
import com.zm.dao.SignDao;
import com.zm.tools.MyProperty;
import com.zm.tools.MyUtils;
import com.zm.tools.MybatisUtils;

public class TrainJob implements Job {
	static {
		System.loadLibrary("opencv_java401");
	}

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 接受参数
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		// 通过这种方式，传递参数
		String taskId = jobDataMap.getString("taskId");
		onetrain();
		// 此任务仅打印日志便于调试、观察
		this.logger.debug(this.getClass().getName() + " trigger..." + taskId);
	}

	public static void onetrain() {

		SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		SignDao signDAO = sqlSession.getMapper(SignDao.class);

		List<TrainScheduler> slist = signDAO.queryScheduler();
		HashMap<String, List<TrainScheduler>> dids = new HashMap<String, List<TrainScheduler>>();
		for (int i = 0; i < slist.size(); i++) {
			String id = slist.get(i).getDepartment_id();
			if (dids.containsKey(id)) {
				List<TrainScheduler> dsl = dids.get(id);
				dsl.add(slist.get(i));
				dids.put(id, dsl);
			} else {
				List<TrainScheduler> dsl = new ArrayList<TrainScheduler>();
				dsl.add(slist.get(i));
				dids.put(id, dsl);
			}
		}
		for (String id : dids.keySet()) {
			TrainScheduler t = new TrainScheduler();
			t.setDepartment_id(id);
			List<TrainScheduler> runningd = signDAO.queryRunningScheduler(t);
			if (runningd != null && runningd.size() != 0) {
				continue;
			}
			List<TrainScheduler> dsl = dids.get(id);
			boolean suc = true;
			for (int i = 0; i < dsl.size(); i++) {
				TrainScheduler item = dsl.get(i);
				item.setStatus(1);
				item.setStart_time(new Date());
				signDAO.updateSchedulerById(item);
			}
			sqlSession.commit();
			MyClassifier cf = new MyClassifier();
			List<TrainImgPathBean> imglist = signDAO.queryTrainSet(t);
			try {
				if (imglist == null || imglist.size() == 0) {
					imglist = new ArrayList<TrainImgPathBean>();
					imglist.addAll(addTrainDefault());
				} else if (imglist.size() < 2*5) {
					imglist.addAll(addTrainDefault());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<String> trainset = new ArrayList<String>();
			List<String> train_user_ids = new ArrayList<String>();
			for (int i = 0; i < imglist.size(); i++) {
				trainset.add(imglist.get(i).getImg_path());
				train_user_ids.add("" + imglist.get(i).getUser_id());
			}

			String pca_path = MyProperty.TRAIN_MODEL_DIR + "/pca/" + id + MyUtils.dateFormatDir() + "/"
					+ System.currentTimeMillis() + ".txt";
			String lda_path = MyProperty.TRAIN_MODEL_DIR + "/lda/" + id + MyUtils.dateFormatDir() + "/"
					+ System.currentTimeMillis() + ".txt";
			;
			new File(pca_path).getParentFile().mkdirs();
			new File(lda_path).getParentFile().mkdirs();
			String message = "";
			try {
				System.out.println("train start.");
				cf.train(trainset, train_user_ids, pca_path, lda_path);
				System.out.println("train end.");
			} catch (Exception e) {
				suc = false;
				message = e.getMessage();
				e.printStackTrace();
			}
			for (int i = 0; i < dsl.size(); i++) {
				TrainScheduler item = dsl.get(i);
				if (suc) {
					item.setStatus(2);
					item.setEnd_time(new Date());
				} else {
					item.setStatus(3);
					item.setEnd_time(new Date());
					item.setMessage(message);
				}
				signDAO.updateSchedulerById(item);
			}
			if (suc) {
				ClassifierBean c = new ClassifierBean();
				c.setDepartment_id(id);
				c.setPca_model_path(pca_path);
				c.setLda_model_path(lda_path);
				c.setUse_status(1);
				signDAO.addClassifier(c);
			}
			sqlSession.commit();
		}

		sqlSession.commit();
		sqlSession.close();
	}

	public static void onetrainForRegister(String department_id, SignDao signDAO, SqlSession sqlSession) {
		String id =department_id;
		TrainScheduler tq=new TrainScheduler();
		boolean suc = true;
		String message = "";
		tq.setDepartment_id(id);
		List<TrainScheduler> tlist=signDAO.querySchedulerByDepartmentId(tq);
		if(tlist==null ||tlist.size()==0) {
			suc = false;
			return;
		}
		TrainScheduler t=tlist.get(0);
		MyClassifier cf = new MyClassifier();
		List<TrainImgPathBean> imglist = new ArrayList<TrainImgPathBean>();
		try {
			imglist.addAll(addTrainDefault());
		} catch (Exception e) {
			suc = false;
			message = e.getMessage();
			e.printStackTrace();
		}
		List<String> trainset = new ArrayList<String>();
		List<String> train_user_ids = new ArrayList<String>();
		for (int i = 0; i < imglist.size(); i++) {
			trainset.add(imglist.get(i).getImg_path());
			train_user_ids.add("" + imglist.get(i).getUser_id());
		}

		String pca_path = MyProperty.TRAIN_MODEL_DIR + "/pca/" + id + MyUtils.dateFormatDir() + "/"
				+ System.currentTimeMillis() + ".txt";
		String lda_path = MyProperty.TRAIN_MODEL_DIR + "/lda/" + id + MyUtils.dateFormatDir() + "/"
				+ System.currentTimeMillis() + ".txt";
		;
		new File(pca_path).getParentFile().mkdirs();
		new File(lda_path).getParentFile().mkdirs();
		t.setStart_time(new Date());
		try {
			System.out.println("train start.");
			cf.train(trainset, train_user_ids, pca_path, lda_path);
			System.out.println("train end.");
		} catch (Exception e) {
			suc = false;
			message = e.getMessage();
			e.printStackTrace();
		}
		if (suc) {
			t.setStatus(2);
			t.setEnd_time(new Date());
		} else {
			t.setStatus(3);
			t.setEnd_time(new Date());
			t.setMessage(message);
		}
		signDAO.updateSchedulerById(t);

		if (suc) {
			ClassifierBean c = new ClassifierBean();
			c.setDepartment_id(id);
			c.setPca_model_path(pca_path);
			c.setLda_model_path(lda_path);
			c.setUse_status(1);
			signDAO.addClassifier(c);
		}
		sqlSession.commit();
	}

	public static List<TrainImgPathBean> addTrainDefault() throws Exception {
		List<TrainImgPathBean> imglist = new ArrayList<TrainImgPathBean>();
		BufferedReader br = new BufferedReader(new FileReader(MyProperty.DEFAULT_TRAIN_SET));
		String line;
		while ((line = br.readLine()) != null) {
			String[] ar = line.split("\t");
			TrainImgPathBean t = new TrainImgPathBean();
			t.setImg_path(new File(MyProperty.DEFAULT_TRAIN_SET).getParent() + "/" + ar[1]);
			t.setUser_id(Integer.parseInt(ar[0]));
			imglist.add(t);
		}
		return imglist;
	}
}