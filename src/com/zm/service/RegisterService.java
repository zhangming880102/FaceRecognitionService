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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class RegisterService
 */
@WebServlet("/RegisterService")
public class RegisterService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static {
		System.loadLibrary("opencv_java401");
	}  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		UserBean user = new UserBean();
		List<UserBean> outUser;
		UserBean ou;

		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "utf-8");
		BufferedReader bf = new BufferedReader(reader);
		String line;
		String o = "";
		while ((line = bf.readLine()) != null) {
			o += line;
		}
		user = gson.fromJson(o, UserBean.class);
		SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		LoginDao loginDAO = sqlSession.getMapper(LoginDao.class);
		
		ImgBean img=user.getImg();
		if(img==null ||img.getBytes()==null ||img.getBytes().size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("服务器获取用户照片失败");
			response(ou,response,gson);
			return;
		}
		
		ImgBean rimg=user.getReviewImg();
		if(rimg==null ||rimg.getBytes()==null ||rimg.getBytes().size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("服务器获取用户照片失败");
			response(ou,response,gson);
			return;
		}
		
		outUser=loginDAO.queryUser(user);
		if(outUser!=null && outUser.size()!=0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("账号已存在");
			response(ou,response,gson);
			return;
		}
		outUser=loginDAO.queryCompany(user);
		if(outUser==null ||outUser.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("学校不存在");
			response(ou,response,gson);
			return;
		}
		user.setCompany_id(outUser.get(0).getCompany_id());
		outUser=loginDAO.queryDepartment(user);
		if(outUser==null ||outUser.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("班级不存在");
			response(ou,response,gson);
			return;
		}
		user.setDepartment_id(outUser.get(0).getDepartment_id());
		
		int inst=loginDAO.register(user);
		
		outUser=loginDAO.login(user);
		if(outUser==null ||outUser.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("注册失败！请稍后重试");
			response(ou,response,gson);
			return;
		}
		
		
		List<UserBean> user_id_list=loginDAO.getUserId(user);
		if(user_id_list==null || user_id_list.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("服务器获取用户失败");
			response(ou,response,gson);
			return;
		}
		
		int user_id=user_id_list.get(0).getUser_id();
		
		ArrayList<String> paths=MyImageUtils.writeImg(img, MyProperty.TRAIN_IMG_DIR,user_id);
		
		if(paths.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("服务器存储照片失败");
			response(ou,response,gson);
			return;
		}
		
		for(int i=0;i<paths.size();i++) {
			TrainImgPathBean imgPath=new TrainImgPathBean();
			imgPath.setImg_path(paths.get(i));
			imgPath.setUser_id(user_id);
			loginDAO.addTrainImgPath(imgPath);
		}
		
		ArrayList<String> paths2=MyImageUtils.writeColorImg(rimg, MyProperty.REVIEW_IMG_DIR,user_id);
		
		if(paths2.size()==0) {
			ou=new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("服务器存储照片失败");
			response(ou,response,gson);
			return;
		}
		
		
		user.setReview_image_path(paths2.get(0));
		loginDAO.updateReviewPath(user);
		
		
		SignDao sDAO=sqlSession.getMapper(SignDao.class);
		TrainScheduler t=new TrainScheduler();
		t.setDepartment_id(outUser.get(0).getDepartment_id());
		sDAO.addScheduler(t);
		
		sqlSession.commit();
		sqlSession.close();
		ou=outUser.get(0);
		ou.setRegistered(true);
		ou.setChecked(true);
		response(ou,response,gson);
	}
	
	void response(UserBean user,HttpServletResponse response,Gson gson) throws ServletException, IOException {
		response.setContentType("application/x-javascript;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String outjson=gson.toJson(user,UserBean.class);
		System.out.println(outjson);
		out.println(outjson);
	}

}
