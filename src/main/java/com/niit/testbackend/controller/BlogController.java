package com.niit.testbackend.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.niit.testbackend.dao.BlogDAO;
import com.niit.testbackend.model.Blog;
import com.niit.testbackend.model.BlogComment;
import com.niit.testbackend.model.Job;

@RestController
public class BlogController {

	private static final Logger log = LoggerFactory.getLogger(BlogController.class);
	
	//@Autowired
	//SessionFactory sessionFactory;
	
	@Autowired
	Blog blog;
	
	@Autowired
	BlogComment blogComment;
	
	@Autowired
	BlogDAO blogDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@RequestMapping(value="/getAllBlogs/", method = RequestMethod.GET)
	public ResponseEntity<List<Blog>> getAllBlogs(){
		log.debug("-->-->starting of getAllBlogs method in BlogController.java");
		List<Blog> blogs = blogDAO.getAllBlogs();
		log.debug("-->--> should have got all the blogs even if  there are none. will have null");
		if(blogs.isEmpty()||blogs.get(0)==null){
			log.debug("-->--> There are none blogs in the blogs list. creating errorCode and errorMessage");
			Blog blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Currently there are no Blogs available to view.");
			blogs.add(blog);
		}
		return new ResponseEntity<List<Blog>>(blogs, HttpStatus.OK);
	}

	@RequestMapping(value="/getMyBlogs", method = RequestMethod.GET)
	public ResponseEntity<List<Blog>> getMyBlogs(){
		log.debug("-->-->starting of getMyBlogs method in BlogController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			blog.setErrorCode("404");
			blog.setErrorMessage("User not logged in. Please login first");
			List<Blog> blogs = new ArrayList<Blog>();
			blogs.add(blog);
			return new ResponseEntity<List<Blog>>(blogs, HttpStatus.OK);
		}
		log.debug("-->-->loggedUserID not null and calling getMyBlogs(userID) method in BlogDAOImpl.java");
		List<Blog> myBlogs = blogDAO.getMyBlogs(loggedInUserID);
		return new ResponseEntity<List<Blog>>(myBlogs, HttpStatus.OK);
	}
	
	@RequestMapping(value="/blogDetails/{blogID}",method = RequestMethod.GET)
	public ResponseEntity<Blog> getBlogDetails(@PathVariable("blogID") Long blogID){
		log.debug("-->-->starting of getblogDetails(blogID) method in BlogController.java");
		Blog blog = blogDAO.blogDetails(blogID);
		if (blog==null){
			Blog b = new Blog();
			b.setErrorCode("404");
			b.setErrorMessage("There is no such blog with id: "+blogID);
			return new ResponseEntity<Blog>(b, HttpStatus.OK);
		}
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);
	}
	
	@RequestMapping(value="/postBlog", method= RequestMethod.POST)
	public ResponseEntity<Blog> postBlog(@RequestBody Blog blog){
		log.debug("-->-->starting of postBlog(POST blog Object) method in BlogController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			blog.setErrorCode("404");
			blog.setErrorMessage("You have not logged in. Please login to post this blog");
			log.debug("You have not logged in. Please login to post this blog");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}else{
			blog.setUserID(loggedInUserID);
			if (blogDAO.save(blog)){
				blog.setErrorCode("200");
				blog.setErrorMessage("Blog successfully applied by userID: "+loggedInUserID+" for the blogID: "+blog.getId());
				log.debug("New blog successfully applied in db with blogID: " + blog.getId()+ " and userID : "+ loggedInUserID);
				}else{
				blog.setErrorCode("404");
				blog.setErrorMessage("Not a able to apply blog in db at the current moment");
				log.debug("Not a able to apply blog in db at the current moment");		
				}
			
		}
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);
	}

	@RequestMapping(value="/blogCommented", method = RequestMethod.POST)
	public ResponseEntity<BlogComment> blogCommented(@RequestBody BlogComment blogComment){
		log.debug("-->-->starting of blogCommented(POST blogComment Object, blogID) method in BlogController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("You have not logged in. Please login to comment on this blog");
			log.debug("You have not logged in. Please login to comment on this blog");
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		}
		Blog blog = blogDAO.blogDetails(blogComment.getBlogID());
		if (blog==null){
			BlogComment b = new BlogComment();
			b.setErrorCode("404");
			b.setErrorMessage("There is no such blog with id: "+blogComment.getBlogID()+". Cant add comment.");
			return new ResponseEntity<BlogComment>(b, HttpStatus.OK);			
		}
		else{
			blogComment.setUserID(loggedInUserID);
			if (blogDAO.saveBlogComment(blogComment)){
				blogComment.setErrorCode("200");
				blogComment.setErrorMessage("Blog Commented successfully applied by userID: "+loggedInUserID+" for the blogID: "+blog.getId());
				log.debug("New blog commented successfully applied in db with blogID: " + blog.getId()+ " and userID : "+ loggedInUserID);
				}else{
				blogComment.setErrorCode("404");
				blogComment.setErrorMessage("Not a able to comment blog in db at the current moment");
				log.debug("Not a able to comment blog in db at the current moment");		
				}
		}
		return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/deleteComment", method = RequestMethod.DELETE)
	public ResponseEntity<BlogComment> deleteComment(@RequestBody BlogComment blogComment){
		log.debug("-->-->starting of deleteCommented(POST blogComment Object) method in BlogController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("You have not logged in. Please login as employee/Admin to delete comment on this blog");
			log.debug("You have not logged in. Please login as employee/Admin to delete comment on this blog");
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		}
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){
			
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("Unauthorised access. You have not logged in as employee/Admin to delete comment on this blog");
			log.debug("Unauthorised access. You have not logged in as employee/Admin to delete comment on this blog");
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		}
		
		if(!blogDAO.checkBlogComment(blogComment.getId())){
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("No such comment id exists.");
			log.debug("No such comment id exists.");
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		}
		
		if (blogDAO.deleteBlogComment(blogComment)){
			blogComment.setErrorCode("200");
			blogComment.setErrorMessage("Blog Commented successfully deleted.");
			log.debug("Blog commented successfully deleted");
			}else{
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("Not a able to delete the comment of blog in db at the current moment");
			log.debug("Not a able to delete comment blog in db at the current moment");		
		}
		
		return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deleteBlog", method = RequestMethod.DELETE)
	public ResponseEntity<Blog> deleteBlog(@RequestBody Blog blog){
		log.debug("-->-->starting of deletblog(POST blog Object) method in BlogController.java");
String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			blog.setErrorCode("404");
			blog.setErrorMessage("You have not logged in. Please login as employee/Admin to delete this blog");
			log.debug("You have not logged in. Please login as employee/Admin to delete this blog");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){
			
			blog.setErrorCode("404");
			blog.setErrorMessage("Unauthorised access. You have not logged in as employee/Admin to delete this blog");
			log.debug("Unauthorised access. You have not logged in as employee/Admin to delete this blog");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		
		if(blogDAO.blogDetails(blog.getId())==null){
			blog.setErrorCode("404");
			blog.setErrorMessage("No such blog id exists.");
			log.debug("No such blog id exists.");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		
		
		if(blogDAO.deleteAllCommentOfBlog(blog.getId())){
			log.debug("-->-->All comments related to blog : "+blog.getName()+" deleted and deleting now deleting blog");
			
			if (blogDAO.delete(blog)){
				blog.setErrorCode("200");
				blog.setErrorMessage("Blog successfully deleted.");
				log.debug("Blog successfully deleted");
				}else{
				blog.setErrorCode("404");
				blog.setErrorMessage("Not a able to delete the blog in db at the current moment");
				log.debug("Not a able to delete blog in db at the current moment");		
			}			
		} else{
			blog.setErrorCode("404");
			blog.setErrorMessage("Not a able to delete the blogComments of blog: "+blog.getName()+" in db at the current moment");
			log.debug("Not a able to delete blog: "+blog.getName()+" in db at the current moment");	
		}
	
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);
	}

}
