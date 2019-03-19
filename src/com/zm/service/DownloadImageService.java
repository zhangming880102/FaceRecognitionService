package com.zm.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

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
import com.zm.dao.ManagerReviewDao;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class DownloadImageService
 */
@WebServlet("/DownloadImageService")
public class DownloadImageService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadImageService() {
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
		RegisterItemBean user = new RegisterItemBean();

		InputStreamReader reader = new InputStreamReader(request.getInputStream(), "utf-8");
		BufferedReader bf = new BufferedReader(reader);
		String line;
		String o = "";
		while ((line = bf.readLine()) != null) {
			o += line;
		}
		user = gson.fromJson(o, RegisterItemBean.class);

		SqlSessionFactory sqlSessionFactory = MybatisUtils.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		ManagerReviewDao signDAO = sqlSession.getMapper(ManagerReviewDao.class);
		if (user != null && user.getReview_image_path() != null) {
			try {
				response.setContentType("image/jpeg;charset=UTF-8");
				OutputStream out = response.getOutputStream();

				FileInputStream fis = new FileInputStream(user.getReview_image_path());
				int len = -1;
				byte[] data = new byte[1024];

				while ((len = fis.read(data)) != -1) {
					out.write(data, 0, len);
				}
				fis.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
