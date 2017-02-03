package com.niit.testbackend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Table(name = "S_BLOG_COMMENT")
@Component
public class BlogComment extends BaseDomain{
	
	@Id
	private Long id;
	
	@Column(name = "BLOG_ID")
	private Long blogID;
	
	@Column(name = "USER_ID")
	private String userID;
	
	private String comments;
	
	private String likes;
	
	private String dislikes;
	
	@Column(name = "REPORT_ABUSE")
	private String reportAbuse;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBlogID() {
		return blogID;
	}
	public void setBlogID(Long blogID) {
		this.blogID = blogID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLikes() {
		return likes;
	}
	public void setLikes(String likes) {
		this.likes = likes;
	}
	public String getDislikes() {
		return dislikes;
	}
	public void setDislikes(String dislikes) {
		this.dislikes = dislikes;
	}
	public String getReportAbuse() {
		return reportAbuse;
	}
	public void setReportAbuse(String reportAbuse) {
		this.reportAbuse = reportAbuse;
	}
	
}
