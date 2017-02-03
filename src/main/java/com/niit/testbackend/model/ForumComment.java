package com.niit.testbackend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Table(name = "S_FORUM_COMMENT")
@Component
public class ForumComment extends BaseDomain {

	@Id
	private Long id;
	private Long forumID;
	@Column(name = "USER_ID")
	private String userID;
	private String comments;
	private Long likes;
	private Long disLikes;
	@Column(name = "REPORT_ABUSE")
	private Long reportAbuse;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getForumID() {
		return forumID;
	}
	public void setForumID(Long forumID) {
		this.forumID = forumID;
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
	public Long getLikes() {
		return likes;
	}
	public void setLikes(Long likes) {
		this.likes = likes;
	}
	public Long getDisLikes() {
		return disLikes;
	}
	public void setDisLikes(Long disLikes) {
		this.disLikes = disLikes;
	}
	public Long getReportAbuse() {
		return reportAbuse;
	}
	public void setReportAbuse(Long reportAbuse) {
		this.reportAbuse = reportAbuse;
	}
	
}
