package com.zm.dao;

import java.util.List;

import com.zm.bean.ClassifierBean;
import com.zm.bean.ImgPathBean;
import com.zm.bean.ManagerSignItemBean;
import com.zm.bean.SignBean;
import com.zm.bean.SignItemBean;
import com.zm.bean.TrainImgPathBean;
import com.zm.bean.TrainScheduler;
import com.zm.bean.UserBean;

public interface SignDao {
	public List<UserBean> getAddress(UserBean user) ;
	public List<UserBean> getUserId(UserBean user);
	public List<UserBean> getUserInfoByUser(UserBean user);
	public int sign(SignBean signBean);
	public int addImgPath(ImgPathBean imgPath);
	public List<SignItemBean> getSignList(UserBean user);
	public List<ManagerSignItemBean> queryManagerSign(ManagerSignItemBean item);
	public List<UserBean> queryUserByDepartmentId(UserBean user);
	public List<TrainScheduler> queryScheduler();
	public List<TrainScheduler> querySchedulerByDepartmentId(TrainScheduler t);
	public List<TrainScheduler> queryRunningScheduler(TrainScheduler t);
	public int addScheduler(TrainScheduler s);
	public int updateSchedulerById(TrainScheduler s);
	public List<TrainImgPathBean> queryTrainSet(TrainScheduler s);
	public int addClassifier(ClassifierBean c);
	public List<ClassifierBean> queryClassifier(ClassifierBean c);
}
