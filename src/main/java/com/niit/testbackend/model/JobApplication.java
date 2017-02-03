package com.niit.testbackend.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Table(name = "S_JOB_APPLIED")
@Component
public class JobApplication extends BaseDomain {

	@Id
	private Long id;
	
	@Column(name="USER_ID")
	private String userID;
	
	@Column(name="JOB_ID")
	private Long jobID;
	
	@Column(name="DATE_APPLIED")
	private Date dateApplied;
	
	private char status;
	
	private String remarks;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public Long getJobID() {
		return jobID;
	}
	public void setJobID(Long jobID) {
		this.jobID = jobID;
	}
	public Date getDateApplied() {
		return dateApplied;
	}
	public void setDateApplied(Date dateApplied) {
		this.dateApplied = dateApplied;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
