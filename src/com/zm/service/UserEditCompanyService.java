package com.zm.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import com.zm.bean.UserBean;
import com.zm.dao.LoginDao;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class UserEditCompanyService
 */
@WebServlet("/UserEditCompanyService")
public class UserEditCompanyService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserEditCompanyService() {
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

		outUser = loginDAO.queryCompany(user);
		if (outUser == null || outUser.size() == 0) {
			ou = new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("学校不存在");
			response(ou, response, gson);
			return;
		}
		user.setCompany_id(outUser.get(0).getCompany_id());
		outUser = loginDAO.queryDepartment(user);
		if (outUser == null || outUser.size() == 0) {
			ou = new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("班级不存在");
			response(ou, response, gson);
			return;
		}
		user.setDepartment_id(outUser.get(0).getDepartment_id());
		user.setReview_status(0);
		loginDAO.updateDepartment(user);
		user.setChecked(true);

		outUser = loginDAO.login(user);
		if (outUser == null || outUser.size() == 0) {
			ou = new UserBean();
			ou.setRegistered(false);
			ou.setErr_str("注册失败！请稍后重试");
			response(ou, response, gson);
			return;
		}
		ou = outUser.get(0);
		ou.setChecked(true);
		sqlSession.commit();
		sqlSession.close();
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
