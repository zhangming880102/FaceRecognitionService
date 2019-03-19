package com.zm.dao;

import java.util.List;

import com.zm.bean.TrainImgPathBean;
import com.zm.bean.UserBean;

public interface LoginDao {
	public List<UserBean> login(UserBean user) ;
	public List<UserBean> queryUser(UserBean user);
	public List<UserBean> queryDepartment(UserBean user);
	public List<UserBean> queryCompany(UserBean user);
	public int register(UserBean user);
	public List<UserBean> getUserId(UserBean user);
	public int addTrainImgPath(TrainImgPathBean imgPath);
	public int updateReviewPath(UserBean user);
	public int updateDepartment(UserBean user);
}
