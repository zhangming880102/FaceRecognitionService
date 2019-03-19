package com.zm.dao;

import java.util.List;

import com.zm.bean.ManagerUserBean;
import com.zm.bean.RegisterItemBean;
import com.zm.bean.UserBean;

public interface ManagerLoginDao {
	public List<ManagerUserBean> login(ManagerUserBean user) ;
	public List<ManagerUserBean> queryUser(ManagerUserBean user);
	public List<ManagerUserBean> queryDepartment(ManagerUserBean user);
	public List<ManagerUserBean> queryCompany(ManagerUserBean user);
	public int register(ManagerUserBean user);
	public List<ManagerUserBean> getUserId(ManagerUserBean user);
	public int registerCompany(ManagerUserBean user);
	public int registerDepartment(ManagerUserBean user);
	public int updateDepartment(ManagerUserBean user);
	public int updateCompany(ManagerUserBean user);
}
