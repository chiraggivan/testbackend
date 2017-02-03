package com.niit.testbackend.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.niit.testbackend.dao.BlogDAO;
import com.niit.testbackend.model.Blog;
import com.niit.testbackend.model.BlogComment;
import com.niit.testbackend.model.Job;

@Repository("BlogDAO")
public class BlogDAOImpl implements BlogDAO {

	private static final Logger log = LoggerFactory.getLogger(BlogDAOImpl.class);

	@Autowired(required = true)
	private SessionFactory sessionFactory;

	public BlogDAOImpl(SessionFactory sessionFactory) {

		try {
			this.sessionFactory = sessionFactory;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public List<Blog> getAllBlogs() {
		log.debug("->->Starting of the getAllBlog method in BlogDAOImpl");
		String hql = "from Blog";
		log.debug("-->--> String hql :"+ hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("-->--> SQL query created as : select * "+ hql);
		return query.list();
	}

	@Transactional
	public Blog blogDetails(Long blogId) {
		log.debug("->->Starting of the blogDetails(blogID) method in BlogDAOImpl");
		return (Blog) sessionFactory.getCurrentSession().get(Blog.class, blogId);
	}

	@Transactional
	public boolean save(Blog blog) {
		log.debug("->->Starting of the save-blog method in BlogDAOImpl");
		try {
			blog.setId(getMaxBlogId());
			sessionFactory.getCurrentSession().save(blog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Long getMaxBlogId(){
		
		Long maxID = 1L; // try with --- Long maxID;
		try {
			String hql = "select max(id) from Blog";
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			maxID = (Long) query.uniqueResult();
			// or other method is 
			//maxID = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			maxID = 1L;
			return maxID;
		}
		return maxID+1;
	}

	@Transactional
	public boolean update(Blog blog) {
		log.debug("->->Starting of the update-blog method in BlogDAOImpl");
		try {
			sessionFactory.getCurrentSession().update(blog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean delete(Blog blog) {
		log.debug("->->Starting of the delete-blog method in BlogDAOImpl");
		try {
			sessionFactory.getCurrentSession().delete(blog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public List<Blog> getMyBlogs(String userID) {
		log.debug("->->Starting of the getMyBlogs(userID) method in BlogDAOImpl");
		String hql = "from Blog where userID = '" + userID + "'";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("-->--> SQL Query Created as : select * "+hql);
		log.debug("-->--> Checking if the above query returned null or empty list");
		if(query.list().isEmpty()){
			log.debug("-->--> No record found for the query : select * "+hql );
			log.debug("-->--> sending appropriate message in errorCode and message for no record found." );
			Blog blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("You have not published any Blog.");
			List<Blog> blogs= new ArrayList<Blog>();
			blogs.add(blog);
			return blogs;
		}
		log.debug("-->--> records found and returning list of your jobs to myAppliedJobs method in JobController.java");
		return query.list();
	}

	@Transactional
	public List<BlogComment> getBlogComment(Long blogID) {
		log.debug("->->Starting of the getBlogComment(blogID) method in BlogDAOImpl");
		String hql = "from BlogComment where blogID = '" + blogID + "'";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Transactional
	public boolean saveBlogComment(BlogComment blogComment) {
		log.debug("->->Starting of the saveBlogComment(blogComment) method in BlogDAOImpl");
		try {
			blogComment.setId(getMaxBlogCommentId());
			sessionFactory.getCurrentSession().save(blogComment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

private Long getMaxBlogCommentId(){
		
		Long maxID = 1L; // try with --- Long maxID;
		try {
			String hql = "select max(id) from BlogComment";
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			maxID = (Long) query.uniqueResult();
			// or other method is 
			//maxID = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			maxID = 1L;
			return maxID;
		}
		return maxID+1;
	}

	@Transactional
	public boolean deleteBlogComment(BlogComment blogComment) {
		log.debug("->->Starting of the deleteBlogComment(blogComment) method in BlogDAOImpl");
		try {
			sessionFactory.getCurrentSession().delete(blogComment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean deleteAllCommentOfBlog(Long blogID) {
		log.debug("->->Starting of the deleteAllCommentOfBlog(blogID) method in BlogDAOImpl");
		String hql = "delete from BlogComment where blogID ="+blogID;
		
		try {
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			query.executeUpdate(); 
			// OR System.out.println(query.executeUpdate()); // for checking purpose only(NOT YET TESTED)
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean checkBlogComment(Long id) {
		log.debug("->->Starting of the checkBlogComment(ID) method in BlogDAOImpl");
		String hql = "from BlogComment where id ="+id;
		log.debug("->->String hql = "+ hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("->-> sql Query created : select * " + hql);
		BlogComment b = (BlogComment) query.uniqueResult();
		if(b!=null){
			return true;
		}
		return false;
	}	
	
}
