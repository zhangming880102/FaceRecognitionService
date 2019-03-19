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
import com.zm.bean.ImgBean;
import com.zm.bean.TrainImgPathBean;
import com.zm.bean.TrainScheduler;
import com.zm.bean.ManagerUserBean;
import com.zm.dao.LoginDao;
import com.zm.dao.ManagerLoginDao;
import com.zm.dao.SignDao;
import com.zm.quartz.TrainJob;
import com.zm.tools.MyImageUtils;
import com.zm.tools.MyProperty;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class ManagerRegisterService
 */
@WebServlet("/ManagerRegisterService")
public class ManagerRegisterService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManagerRegisterService() {
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
		ManagerUserBean user = new ManagerUserBean();
		List<ManagerUserBean> outUser;
		ManagerUserBean ou;

		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "utf-8");
		BufferedReader bf = new BufferedReader(reader);
		String line;
		String o = "";
		while ((line = bf.readLine()) != null) {
			o += line;
		}
		user = gson.fromJson(o, ManagerUserBean.class);
		SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ManagerLoginDao loginDAO = sqlSession.getMapper(ManagerLoginDao.class);
		
		
		outUser=loginDAO.queryUser(user);
		if(outUser!=null && outUser.size()!=0) {
			ou=new ManagerUserBean();
			ou.setRegistered(false);
			ou.setErr_str("账号已存在");
			response(ou,response,gson);
			return;
		}
		outUser=loginDAO.queryCompany(user);
		if(outUser==null || outUser.size()==0) {
			loginDAO.registerCompany(user);
		}
		String company_id=loginDAO.queryCompany(user).get(0).getCompany_id();
		
		user.setCompany_id(company_id);
		outUser=loginDAO.queryDepartment(user);
		if(outUser==null ||outUser.size()==0) {
			loginDAO.registerDepartment(user);
		}
		user.setDepartment_id(loginDAO.queryCompany(user).get(0).getCompany_id());
		
		int inst=loginDAO.register(user);
		
		outUser=loginDAO.login(user);
		if(outUser==null ||outUser.size()==0) {
			ou=new ManagerUserBean();
			ou.setRegistered(false);
			ou.setErr_str("注册失败！请稍后重试");
			response(ou,response,gson);
			return;
		}
		
		SignDao sDAO=sqlSession.getMapper(SignDao.class);
		TrainScheduler t=new TrainScheduler();
		t.setDepartment_id(outUser.get(0).getDepartment_id());
		sDAO.addScheduler(t);
		sqlSession.commit();
		TrainJob.onetrainForRegister(outUser.get(0).getDepartment_id(), sDAO, sqlSession);
		
		
		sqlSession.commit();
		sqlSession.close();
		ou=outUser.get(0);
		ou.setRegistered(true);
		ou.setChecked(true);
		response(ou,response,gson);
	}
	
	void response(ManagerUserBean user,HttpServletResponse response,Gson gson) throws ServletException, IOException {
		response.setContentType("application/x-javascript;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String outjson=gson.toJson(user,ManagerUserBean.class);
		System.out.println(outjson);
		out.println(outjson);
	}


}
