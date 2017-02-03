package com.niit.testbackend.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.testbackend.dao.FriendDAO;
import com.niit.testbackend.dao.UserDAO;
import com.niit.testbackend.model.Friend;

@RestController
public class FriendController {

	public static final Logger log = LoggerFactory.getLogger(FriendController.class);
	
	@Autowired
	private Friend friend;
	
	@Autowired
	private FriendDAO friendDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private HttpSession httpSession;
	
	private boolean checkFriendExist(String friendID){
		log.debug("-->-->Checking FriendID :"+friendID+"available as user in User Table.");
		if(userDAO.get(friendID) != null)
		return true;
		else
			return false;
	}
	
	@RequestMapping(value="/getMyFriend/", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> getMyFriend(){
		log.debug("-->-->starting of getMyFriend method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("403");
			friend.setErrorMessage("User not logged in. Please login first");
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
		
		List<Friend> myFriends = friendDAO.getMyFriends(loggedInUserID);
		
		if (myFriends.isEmpty()||myFriends.get(0)==null){
			log.debug("-->--> Friends list of the user : "+loggedInUserID +" seems to be empty."
					+ "sending appropriate error code and message with the friend object to list of friend");
			friend.setErrorCode("404");
			friend.setErrorMessage("You Dont have friends currrently.");
			myFriends.add(friend);
			
			return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);	
		}
		return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);
	}
	
	@RequestMapping(value="/addFriend/{friendID}", method = RequestMethod.GET)
	public ResponseEntity<Friend> sendFriendRequest(@PathVariable("friendID") String friendID){
		log.debug("-->-->starting of sendFriendRequest(friendID) method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("403");
			friend.setErrorMessage("User not logged in. Please login first");
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		log.debug("-->--> logged in userID : "+loggedInUserID+". Now check if friendID exist.");
		if(checkFriendExist(friendID)==false){
			log.debug("-->-->calling private checkFriendExist(friendID) method to check friendID exist in user Table.");
			friend.setErrorCode("404");
			friend.setErrorMessage("You can not add a friend as there is no user with the ID :"+friendID);
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		if(friendDAO.get(loggedInUserID, friendID)!=null||friendDAO.get(friendID, loggedInUserID)!=null){
			log.debug("-->-->Both userID and friendID already in there in friend table. Cannot create new row.");
			friend.setErrorCode("404");
			friend.setErrorMessage("You already sent friend request Or please check friend's request option");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		friend.setUserID(loggedInUserID);
		friend.setFriendID(friendID);
		friend.setStatus('P');
		log.debug("-->-->trying to save userID and friendID in friend table with status as P -- pending request");
		friendDAO.save(friend);
		friend.setErrorCode("200");
		friend.setErrorMessage("Friend request successfully registered in db.");
		log.debug("-->-->successfully saved userID and friendID in friend table");
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);	
	}
	
	@RequestMapping(value="/unFriend/{friendID}", method = RequestMethod.GET)
	public ResponseEntity<Friend> unFriend(@PathVariable("friendID") String friendID){
		log.debug("-->-->starting of unFriend(friendID) method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("404");
			friend.setErrorMessage("User not logged in. Please login first");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		if(checkFriendExist(friendID)==false){
			log.debug("-->-->calling private checkFriendExist(friendID) method to check friendID exist in user Table.");
			friend.setErrorCode("404");
			friend.setErrorMessage("You cannot unfriend as there is no user with the ID :"+friendID);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		log.debug("-->-->got userID :"+ loggedInUserID+" and friend ID :"+friendID);
		
		Friend friend = friendDAO.getUnfriend(loggedInUserID, friendID);
		if (friend==null){
			log.debug("-->-->friend row not found. Swapping userID and friendID. ");
			friend = friendDAO.getUnfriend(friendID, loggedInUserID);
			if (friend==null){
				Friend f = new Friend();
				f.setErrorCode("404");
				f.setErrorMessage("You both are not friend in the first place.");
				log.debug("-->-->As no row found as friend for both ID, sending appropriate error code and message "+friend);
				return new ResponseEntity<Friend>(f, HttpStatus.OK);
			}
			friend.setStatus('U');
			try {
				friendDAO.update(friend);
				friend.setErrorCode("200");
				friend.setErrorMessage("friend successfully updated in friend table.");
				log.debug("-->-->updated friend info ");
				return new ResponseEntity<Friend>(friend, HttpStatus.OK);
			} catch (Exception e) {
				friend.setErrorCode("404");
				friend.setErrorMessage("friend could not be updated in friend table.");
				e.printStackTrace();
				log.debug("-->-->could not update thru friendDAO.update(friend). returning back null ");
				return new ResponseEntity<Friend>(friend, HttpStatus.OK);
			}			
		}

		friend.setStatus('U');
		if(friendDAO.update(friend)){
			friend.setErrorCode("200");
			friend.setErrorMessage("friend successfully updated in friend table.");
			log.debug("-->-->updated friend info ");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		} else {
			friend.setErrorCode("404");
			friend.setErrorMessage("friend could not be updated in friend table.");
			log.debug("-->-->could not update thru friendDAO.update(friend). returning back appropriate errorCode and message ");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		/*log.debug("-->-->calling private method(updateFriendStatus) to check and update friend Table. with status as U -- unfriend");
		Friend friend = updateFriendStatus(loggedInUserID, friendID, 'U');
		log.debug("-->-->updated friend info and received friend object as :"+friend);
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);*/
	}
	
	@RequestMapping(value="/rejectFriend/{friendID}", method = RequestMethod.GET)
	public ResponseEntity<Friend> rejectFriend(@PathVariable("friendID") String friendID){
		log.debug("-->-->starting of rejectFriend(friendID) method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("403");
			friend.setErrorMessage("User not logged in. Please login first");
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		if(checkFriendExist(friendID)==false){
			log.debug("-->-->calling private checkFriendExist(friendID) method to check friendID exist in user Table.");
			friend.setErrorCode("404");
			friend.setErrorMessage("You can not reject as there is no user with the ID :"+friendID);
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		log.debug("-->--> Fetching row in Friend table for which friendID :"+loggedInUserID+" and userID :"+friendID
				+ " and status is 'P' pending ");
		log.debug("-->-->calling friendRequestExist(loggedInID, friendID) method in FriendDAOImpl");
		if(!friendDAO.friendRequestExist(loggedInUserID, friendID)){
			log.debug("-->-->could'nt find a friend's request for "+loggedInUserID+" from friend "+ friendID);
			friend.setErrorCode("404");
			friend.setErrorMessage("You "+loggedInUserID+", dont have any friend's request from the user with the ID :"+friendID);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		log.debug("-->-->got userID :"+ loggedInUserID+" and friend ID :"+friendID);
		log.debug("-->-->calling private method(updateFriendStatus) to check and update friend Table. with status as R -- reject");
		Friend friend = updateFriendStatus(loggedInUserID, friendID, 'R');
		log.debug("-->-->updated friend info and recieved friend object as :"+friend);
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);
	}
	
	@RequestMapping(value="/acceptFriend/{friendID}", method = RequestMethod.GET)
	public ResponseEntity<Friend> acceptFriend(@PathVariable("friendID") String friendID){
		log.debug("-->-->starting of acceptFriend(friendID) method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("404");
			friend.setErrorMessage("User not logged in. Please login first!!!!!!!!!!");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		if(checkFriendExist(friendID)==false){
			log.debug("-->-->calling private checkFriendExist(friendID) method to check friendID exist in user Table.");
			friend.setErrorCode("404");
			friend.setErrorMessage("You can not accept as there is no user with the ID :"+friendID);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		log.debug("-->--> Fetching row in Friend table for which friendID :"+loggedInUserID+" and userID :"+friendID
				+ " and status is 'P' pending ");
		log.debug("-->-->calling friendRequestExist(loggedInID, friendID) method in FriendDAOImpl");
		if(!friendDAO.friendRequestExist(loggedInUserID, friendID)){
			log.debug("-->-->could'nt find a friend's request for "+loggedInUserID+" from friend "+ friendID);
			friend.setErrorCode("404");
			friend.setErrorMessage("You "+loggedInUserID+", dont have any friend's request from the user with the ID :"+friendID);
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		
		log.debug("-->-->got userID :"+ loggedInUserID+" and friend ID :"+friendID +" with request in Friend Table");
		log.debug("-->-->calling private method(updateFriendStatus) to check and update friend Table. with status as A -- accept");
		Friend friend = updateFriendStatus(loggedInUserID, friendID, 'A');
		log.debug("-->-->updated friend info and recieved friend object as :"+friend);
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);
	}
	
	private Friend updateFriendStatus(String userID, String friendID, char status){
		log.debug("-->-->private method(updateFriendStatus) called with parameter userID : "+userID+" friendID : "+friendID+" status : "+status );
		
		Friend friend = friendDAO.get(friendID, userID);
		friend.setStatus(status);
		if(friendDAO.update(friend)){
			friend.setErrorCode("200");
			friend.setErrorMessage("friend succefully updated in friend table.");
			log.debug("-->-->updated friend info ");
			return friend;
		} else {
			friend.setErrorCode("404");
			friend.setErrorMessage("friend could not be updated in friend table.");
			log.debug("-->-->could not update thru friendDAO.update(friend). returning back appropriate errorCode and message ");
			return friend;
		}				
	}
	
	@RequestMapping(value="/getMyFriendsRequest", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> getMyFriendsRequest(){
		log.debug("-->-->starting of getMyFriendsRequest method in FriendController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			friend.setErrorCode("403");
			friend.setErrorMessage("User not logged in. Please login first");
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
		log.debug("->->calling getNewFriendRequests(userID) method in FriendDAOImpl");
		List<Friend> friends = friendDAO.getNewFriendRequests(loggedInUserID);
		
		return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
	}
}
