package com.niit.testbackend.dao;

import java.util.List;

import com.niit.testbackend.model.Blog;
import com.niit.testbackend.model.BlogComment;

public interface BlogDAO {

	public List<Blog> getAllBlogs();
	
	public Blog blogDetails(Long id);
	
	public boolean save(Blog blog);
	
	public boolean update(Blog blog);
	
	public boolean delete(Blog blog);
	
	public List<Blog> getMyBlogs(String userID);
	
	public List<BlogComment> getBlogComment(Long blogID);
	
	public boolean checkBlogComment(Long blogId);
	
	public boolean saveBlogComment(BlogComment blogComment);
	
	public boolean deleteBlogComment(BlogComment blogComment);
	
	public boolean deleteAllCommentOfBlog(Long blogID);
}

