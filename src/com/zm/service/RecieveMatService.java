package com.zm.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zm.bean.ClassifierBean;
import com.zm.bean.ClassifierResult;
import com.zm.bean.ImgBean;
import com.zm.bean.ImgPathBean;
import com.zm.bean.SignBean;
import com.zm.bean.TrainImgPathBean;
import com.zm.bean.TrainScheduler;
import com.zm.bean.UserBean;
import com.zm.classifier.MyClassifier;
import com.zm.dao.LoginDao;
import com.zm.dao.SignDao;
import com.zm.tools.MyImageUtils;
import com.zm.tools.MyProperty;
import com.zm.tools.MyUtils;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class RecieveMatService
 */
@WebServlet("/RecieveMatService")
public class RecieveMatService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static {
		System.loadLibrary("opencv_java401");
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecieveMatService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "utf-8");
		BufferedReader bf = new BufferedReader(reader);
		String line;
		String o = "";
		while ((line = bf.readLine()) != null) {
			o += line;
		}
		// System.out.println(o);
		UserBean user = gson.fromJson(o, UserBean.class);
		SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		SignDao signDAO = sqlSession.getMapper(SignDao.class);
		LoginDao loginDAO = sqlSession.getMapper(LoginDao.class);
		UserBean ou;

		if (user == null) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器获取信息失败");
			response(ou, response, gson);
			return;
		}

		List<UserBean> outUser = signDAO.getAddress(user);

		if (outUser == null || outUser.size() == 0) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器获取用户信息失败");
			response(ou, response, gson);
			return;
		}
		double reg_lat = outUser.get(0).getDepartment_latitude();
		double reg_lon = outUser.get(0).getDepartment_longtitude();
		boolean address_signed = false;
		double distance = MyUtils.computeDistance(user.getSign_latitude(), user.getSign_longtitude(), reg_lat, reg_lon);
		System.out.println(distance);
		if (distance < MyProperty.MAX_DIS) {
			address_signed = true;
		}

		ImgBean img = user.getImg();
		if (img == null || img.getBytes() == null || img.getBytes().size() == 0) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器获取用户照片失败");
			response(ou, response, gson);
			return;
		}
		List<UserBean> user_id_list = signDAO.getUserId(user);
		if (user_id_list == null || user_id_list.size() == 0) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器获取用户失败");
			response(ou, response, gson);
			return;
		}

		int user_id = user_id_list.get(0).getUser_id();
		String department_id = user_id_list.get(0).getDepartment_id();

		List<String> paths = MyImageUtils.writeImg(img, MyProperty.TEST_IMG_DIR, user_id);

		if (paths.size() == 0) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器存储照片失败");
			response(ou, response, gson);
			return;
		}

		MyClassifier mcf = new MyClassifier();
		TrainScheduler t = new TrainScheduler();
		t.setDepartment_id(department_id);
		List<TrainImgPathBean> imglist = signDAO.queryTrainSet(t);
		List<String> trainset = new ArrayList<String>();
		List<String> train_user_ids = new ArrayList<String>();
		for (int i = 0; i < imglist.size(); i++) {
			trainset.add(imglist.get(i).getImg_path());
			train_user_ids.add("" + imglist.get(i).getUser_id());
		}
		ClassifierBean c = new ClassifierBean();
		c.setDepartment_id(department_id);
		List<ClassifierBean> clist = signDAO.queryClassifier(c);
		if (clist == null || clist.size() == 0) {
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器获取分类器失败");
			response(ou, response, gson);
			return;
		}
		ClassifierResult sign_result=null;
		try {
			sign_result = mcf.check_test_imgs(paths, user_id + "", trainset, train_user_ids,
					clist.get(0).getPca_model_path(), clist.get(0).getLda_model_path());
		} catch (Exception e) {
			e.printStackTrace();
			ou = new UserBean();
			ou.setChecked(false);
			ou.setErr_str("服务器分类失败");
			response(ou, response, gson);
			return;
		}
		// ClassifierResult sign_result=new ClassifierResult();
		boolean img_signed = sign_result.isResult();
		boolean signed = (img_signed && address_signed);
		int img_sign = img_signed ? 1 : 0;
		int address_sign = address_signed ? 1 : 0;
		int user_sign = signed ? 1 : 0;
		SignBean signBean = new SignBean();
		signBean.setAddress(user.getSign_addrees());
		signBean.setUser_id(user_id);
		signBean.setLatitude(user.getSign_latitude());
		signBean.setLongtitude(user.getSign_longtitude());
		signBean.setImg_sign(img_sign);
		signBean.setAddress_sign(address_sign);
		signBean.setSign_status(user_sign);

		signDAO.sign(signBean);

		for (int i = 0; i < paths.size(); i++) {
			ImgPathBean imgPath = new ImgPathBean();
			imgPath.setPath(paths.get(i));
			imgPath.setSign_id(signBean.getId());
			boolean ri = sign_result.getResult_list().get(i).booleanValue();
			int st = ri ? 1 : 0;
			imgPath.setSign_status(st);
			signDAO.addImgPath(imgPath);
		}

		sqlSession.commit();
		sqlSession.close();

		ou = new UserBean();
		ou.setFinal_check(true);
		ou.setImg_signed(img_signed);
		ou.setAddress_signed(address_signed);
		ou.setSigned(signed);
		response(ou, response, gson);
	}

	void response(UserBean user, HttpServletResponse response, Gson gson) throws ServletException, IOException {
		response.setContentType("application/x-javascript;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String outjson = gson.toJson(user, UserBean.class);
		System.out.println(outjson);
		out.println(outjson);
	}
}
