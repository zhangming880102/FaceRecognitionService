package com.zm.dao;

import java.util.List;

import com.zm.bean.ImgPathBean;
import com.zm.bean.RegisterItemBean;
import com.zm.bean.SignBean;
import com.zm.bean.ManagerUserBean;

public interface ManagerReviewDao {
	public List<RegisterItemBean> getRegisterList(ManagerUserBean user);
	public int updateReviewStatus(RegisterItemBean item);
}
