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
import com.zm.bean.ManagerUserBean;
import com.zm.bean.RegisterItemBean;
import com.zm.bean.SignItemBean;
import com.zm.bean.TrainScheduler;
import com.zm.bean.UserBean;
import com.zm.dao.ManagerReviewDao;
import com.zm.dao.SignDao;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class RegisterListService
 */
@WebServlet("/RegisterListService")
public class RegisterListService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterListService() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		ManagerUserBean user = new ManagerUserBean();

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
		ManagerReviewDao signDAO = sqlSession.getMapper(ManagerReviewDao.class);
		List<RegisterItemBean> outUser=signDAO.getRegisterList(user);
		if(outUser==null) {
			
			user.setChecked(true);
			user.setRegisterList(new ArrayList<RegisterItemBean>());
			response(user,response,gson);
			return;
		}
		sqlSession.commit();
		sqlSession.close();
		user.setChecked(true);
		user.setRegisterList(outUser);
		response(user,response,gson);
	}
	void response(ManagerUserBean user,HttpServletResponse response,Gson gson) throws ServletException, IOException {
		response.setContentType("application/x-javascript;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String outjson=gson.toJson(user,ManagerUserBean.class);
		System.out.println(outjson);
		out.println(outjson);
	}

}
