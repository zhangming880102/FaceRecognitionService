package com.zm.bean;

import java.util.Date;

public class ClassifierBean {
	private String pca_model_path;
	private String lda_model_path;
	private String department_id;
	private Date create_time;
	private int use_status;
	public String getPca_model_path() {
		return pca_model_path;
	}
	public void setPca_model_path(String pca_model_path) {
		this.pca_model_path = pca_model_path;
	}
	public String getLda_model_path() {
		return lda_model_path;
	}
	public void setLda_model_path(String lda_model_path) {
		this.lda_model_path = lda_model_path;
	}
	
	public String getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(String department_id) {
		this.department_id = department_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public int getUse_status() {
		return use_status;
	}
	public void setUse_status(int use_status) {
		this.use_status = use_status;
	}
}
