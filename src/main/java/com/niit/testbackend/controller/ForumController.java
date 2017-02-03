package com.niit.testbackend.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.niit.testbackend.dao.ForumDAO;
import com.niit.testbackend.model.Forum;
import com.niit.testbackend.model.ForumComment;

@RestController
public class ForumController {
	
	private static final Logger log = LoggerFactory.getLogger(ForumController.class);

	@Autowired
	Forum forum;
	
	@Autowired
	ForumComment forumComment;
	
	@Autowired
	ForumDAO forumDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@RequestMapping(value="/getAllForums", method = RequestMethod.GET)
	public ResponseEntity<List<Forum>> getAllForums(){
		log.debug("-->-->starting of getAllforums method in ForumController.java");
		List<Forum> forum = forumDAO.getAllForum();
		return new ResponseEntity<List<Forum>>(forum, HttpStatus.OK);
	}

	@RequestMapping(value="/getMyForums", method = RequestMethod.GET)
	public ResponseEntity<List<Forum>> getMyForum(){
		log.debug("-->-->starting of getMyForum method in ForumController.java");
		String loggedInUserID = (String) httpSession.getAttribute("LoggedInUserID");
		List<Forum> myForums = forumDAO.getMyForum(loggedInUserID);
		return new ResponseEntity<List<Forum>>(myForums, HttpStatus.OK);
	}
	
	@RequestMapping(value="/forumDetails/{forumID}",method = RequestMethod.GET)
	public ResponseEntity<Forum> getForumDetails(@PathVariable("forumID") Long forumID){
		log.debug("-->-->starting of getforumDetails(forumID) method in ForumController.java");
		Forum forum = forumDAO.getForumDetails(forumID);
		if (forum==null){
			forum.setErrorCode("404");
			forum.setErrorMessage("There is no such forum with id: "+forumID);
		}
		return new ResponseEntity<Forum>(forum, HttpStatus.OK);
	}
	
	@RequestMapping(value="/postForum", method= RequestMethod.POST)
	public ResponseEntity<Forum> postForum(@RequestBody Forum forum){
		log.debug("-->-->starting of postForum(POST forum Object) method in ForumController.java");
		String loggedInUserID = (String) httpSession.getAttribute("LoggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			forum.setErrorCode("404");
			forum.setErrorMessage("You have not logged in. Please login to post this forum");
			log.debug("You have not logged in. Please login to post this forum");
			
		}else{
			forum.setUserID(loggedInUserID);
			if (forumDAO.saveForum(forum)){
				forum.setErrorCode("200");
				forum.setErrorMessage("Forum successfully applied by userID: "+loggedInUserID+" for the forumID: "+forum.getId());
				log.debug("New forum successfully applied in db with forumID: " + forum.getId()+ " and userID : "+ loggedInUserID);
				}else{
				forum.setErrorCode("404");
				forum.setErrorMessage("Not a able to apply forum in db at the current moment");
				log.debug("Not a able to apply forum in db at the current moment");		
				}
			
		}
		return new ResponseEntity<Forum>(forum, HttpStatus.OK);
	}

	@RequestMapping(value="/forumCommented", method = RequestMethod.POST)
	public ResponseEntity<ForumComment> forumCommented(@RequestBody ForumComment forumComment, @RequestParam Long forumID){
		log.debug("-->-->starting of forumCommented(POST forumComment Object, forumID) method in ForumController.java");
		String loggedInUserID = (String) httpSession.getAttribute("LoggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			
			forumComment.setErrorCode("404");
			forumComment.setErrorMessage("You have not logged in. Please login to comment on this forum");
			log.debug("You have not logged in. Please login to comment on this forum");
			
		}else{
			forumComment.setForumID(forumID);
			forumComment.setUserID(loggedInUserID);
			if (forumDAO.saveForumComment(forumComment)){
				forumComment.setErrorCode("200");
				forumComment.setErrorMessage("Forum Commented successfully applied by userID: "+loggedInUserID+" for the forumID: "+forum.getId());
				log.debug("New forum commented successfully applied in db with forumID: " + forum.getId()+ " and userID : "+ loggedInUserID);
				}else{
				forumComment.setErrorCode("404");
				forumComment.setErrorMessage("Not a able to comment forum in db at the current moment");
				log.debug("Not a able to comment forum in db at the current moment");		
				}
		}
		return new ResponseEntity<ForumComment>(forumComment, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/deleteComment", method = RequestMethod.POST)
	public ResponseEntity<ForumComment> deleteComment(@RequestBody ForumComment forumComment){
		log.debug("-->-->starting of deleteCommented(POST forumComment Object) method in ForumController.java");
		
		if (forumDAO.deleteForumComment(forumComment)){
			forumComment.setErrorCode("200");
			forumComment.setErrorMessage("Forum Commented successfully deleted.");
			log.debug("Forum commented successfully deleted");
			}else{
			forumComment.setErrorCode("404");
			forumComment.setErrorMessage("Not a able to delete the comment of forum in db at the current moment");
			log.debug("Not a able to delete comment forum in db at the current moment");		
		}
		
		return new ResponseEntity<ForumComment>(forumComment, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deleteForum", method = RequestMethod.POST)
	public ResponseEntity<Forum> deleteForum(@RequestBody Forum forum){
		log.debug("-->-->starting of deletforum(POST forum Object) method in ForumController.java");
		
		if(forumDAO.deleteAllCommentOfForum(forum.getId())){
			log.debug("-->-->All comments related to forum : "+forum.getName()+" deleted and deleting now deleting forum");
			
			if (forumDAO.deleteForum(forum)){
				forum.setErrorCode("200");
				forum.setErrorMessage("Forum successfully deleted.");
				log.debug("Forum successfully deleted");
				}else{
				forum.setErrorCode("404");
				forum.setErrorMessage("Not a able to delete the forum in db at the current moment");
				log.debug("Not a able to delete forum in db at the current moment");		
			}			
		} else{
			forum.setErrorCode("404");
			forum.setErrorMessage("Not a able to delete the forumComments of forum: "+forum.getName()+" in db at the current moment");
			log.debug("Not a able to delete forum: "+forum.getName()+" in db at the current moment");	
		}
	
		return new ResponseEntity<Forum>(forum, HttpStatus.OK);
	}	
}