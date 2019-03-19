package com.zm.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.zm.bean.ManagerSignItemBean;
import com.zm.bean.ManagerUserBean;
import com.zm.bean.RegisterItemBean;
import com.zm.bean.UserBean;
import com.zm.dao.ManagerReviewDao;
import com.zm.dao.SignDao;
import com.zm.tools.MyUtils;
import com.zm.tools.MybatisUtils;

/**
 * Servlet implementation class ManagerSignListService
 */
@WebServlet("/ManagerSignListService")
public class ManagerSignListService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ManagerSignListService() {
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
		SignDao signDAO = sqlSession.getMapper(SignDao.class);
		UserBean u = new UserBean();
		u.setDepartment_id(user.getDepartment_id());
		List<UserBean> user_list = signDAO.queryUserByDepartmentId(u);
		int start_time_num = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(user.getStart_time()));
		List<ManagerSignItemBean> mslist = new ArrayList<ManagerSignItemBean>();
		if (user_list != null) {
			Date d = new Date();
			Calendar cl = Calendar.getInstance();
			cl.setTime(d);
			int day_of_month = cl.get(Calendar.DAY_OF_MONTH);
			String month = new SimpleDateFormat("yyyyMM").format(d);
			for (int j = day_of_month - 1; j >= 1; j--) {
				cl.set(Calendar.DAY_OF_MONTH, j);
				int day_of_week = cl.get(Calendar.DAY_OF_WEEK);
				if (day_of_week < user.getWeek_from() || day_of_week > user.getWeek_to()) {
					continue;
				}
				String qd = month + MyUtils.toDay(j);
				if (Integer.parseInt(qd) < start_time_num) {
					break;
				}
				for (int i = 0; i < user_list.size(); i++) {
					ManagerSignItemBean qitem = new ManagerSignItemBean();

					qitem.setQuery_date(qd);
					qitem.setDepartment_id(user.getDepartment_id());
					qitem.setUser(user_list.get(i).getUser());
					List<ManagerSignItemBean> slist = signDAO.queryManagerSign(qitem);
					String status = MyUtils.check_slist(slist, user);
					if (!status.equals("成功")) {
						ManagerSignItemBean m = new ManagerSignItemBean();
						m.setDate(qd);
						m.setUser(user_list.get(i).getUser());
						m.setName(user_list.get(i).getName());
						m.setStatus(status);
						mslist.add(m);
					}
				}
			}
		}

		user.setSignList(mslist);

		sqlSession.commit();
		sqlSession.close();
		user.setChecked(true);

		response(user, response, gson);
	}

	void response(ManagerUserBean user, HttpServletResponse response, Gson gson) throws ServletException, IOException {
		response.setContentType("application/x-javascript;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String outjson = gson.toJson(user, ManagerUserBean.class);
		System.out.println(outjson);
		out.println(outjson);
	}

}
