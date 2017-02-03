package com.niit.testbackend.dao;

import java.util.List;

import com.niit.testbackend.model.Forum;
import com.niit.testbackend.model.ForumComment;

public interface ForumDAO {

	public List<Forum> getAllForum();
	
	public Forum getForumDetails(Long forumID);
	
	public List<Forum> getMyForum(String userID);
	
	public boolean saveForum(Forum forum);
	
	public boolean updateForum(Forum forum);
	
	public boolean deleteForum(Forum forum);
	
	public List<ForumComment> getForumComment(Long forumID);
	
	public boolean saveForumComment(ForumComment forumComment);
	
	public boolean deleteForumComment(ForumComment forumComment);
	
	public boolean deleteAllCommentOfForum(Long forumID);
}
