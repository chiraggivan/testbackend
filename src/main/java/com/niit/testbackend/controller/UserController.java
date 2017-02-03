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
import org.springframework.web.bind.annotation.RestController;

import com.niit.testbackend.dao.UserDAO;
import com.niit.testbackend.model.JobApplication;
import com.niit.testbackend.model.User;

//import com.niit.syzitobackend.model.Friend;
//import com.niit.syzitobackend.dao.FriendDAO;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserDAO userDAO;

	@Autowired
	User user;

	//@Autowired
	//FriendDAO friendDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@RequestMapping(value="/hello") // just to check if restClient working 
	public String hello(){
		return "hello";
	}
	//Admin should able to make one of the employee as admin
	@RequestMapping(value = "/makeAdmin/{id}", method = RequestMethod.PUT)
	public ResponseEntity<User> makeAdmin(@PathVariable("id") String empID) {
		logger.debug("------>>>>>  calling the method makeAdmin");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			logger.debug("-->-->user not logged in. Creating appropriate error code and message");
			user.setErrorCode("404");
			user.setErrorMessage("You have not logged in. Please login as admin to make the employee as Admin");	
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char a = 'A';
		if(loggedInUserRole!=a){
			logger.debug("-->-->user not logged in as admin. Cant make any user as Admin.");
			user.setErrorCode("404");
			user.setErrorMessage("Unauthorised Access. You have not logged in as admin. Cant make any user as Admin.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		user = userDAO.get(empID);
		if (user == null) {
			logger.debug("There is no user with the id : " + empID);
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist");
			return new ResponseEntity<User>(user, HttpStatus.OK); 
		}
		
		if(user.getRole()!='E')
		{
			logger.debug("We cannot make this user : " + empID+"as admin as he is not employee");
			user.setErrorCode("404");
			user.setErrorMessage("We cannot make this user : " + empID+" as admin as he is not employee");
			return new ResponseEntity<User>(user, HttpStatus.OK); // 200	
		}
		
		logger.debug("------> verified as logged in user as admin and the user to be made as admin is not other than employee");
		user.setRole('A');
		try {
			userDAO.update(user);
			user.setErrorCode("200");
			user.setErrorMessage("Successfully assign Admin role to the employee : " + user.getFirstName());
			logger.debug("Employee role updated as admin successfully for " + empID);
			return new ResponseEntity<User>(user, HttpStatus.OK); // 200
		} catch (Exception e) {
			user.setErrorCode("404");
			user.setErrorMessage("CANNOT assign Admin role to the employee : " + user.getFirstName());
			logger.debug("Employee role CANNOT be updated as admin successfully for " + empID);
			return new ResponseEntity<User>(user, HttpStatus.OK); // 200
		}
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		logger.debug("->->->->calling method listAllUsers");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID==null){
			logger.debug("->->->->no user logged in. Retrieving all users");
			List<User> users = userDAO.list();
			if (users.isEmpty()) {
				logger.debug("->->->->no user found. sending errorCode and message.");
				user.setErrorCode("404");
				user.setErrorMessage("No users are available");
				users.add(user);
				return new ResponseEntity<List<User>>(users, HttpStatus.OK);
			}
			logger.debug("->->->->Users found. sending all in list");
			return new ResponseEntity<List<User>>(users, HttpStatus.OK);	
		}
		else{
			logger.debug("->->->->User logged in. Retrieving all users except the logged in user.");
			List<User> users = userDAO.listOtherUser(loggedInUserID);
			if (users.isEmpty()) {
				logger.debug("->->->->no user found. sending errorCode and message.");
				user.setErrorCode("404");
				user.setErrorMessage("No users are available");
				users.add(user);
				return new ResponseEntity<List<User>>(users, HttpStatus.OK);
			}
			logger.debug("->->->->Users found. sending all in list");
			return new ResponseEntity<List<User>>(users, HttpStatus.OK);
		}
	}

	// http://localhost:8080/Collaboration/user/
	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		logger.debug("->->->->calling method createUser");
		if (userDAO.get(user.getId()) == null) {
			logger.debug("->->->->User is going to create with id:" + user.getId());
			user.setIsOnline('N');
			user.setStatus('N');
			  if (userDAO.save(user) ==true)
			  {
				  user.setErrorCode("200");
					user.setErrorMessage("Thank you  for registration. You have successfully registered as " + user.getRole());
			  }
			  else
			  {
				  user.setErrorCode("404");
				  user.setErrorMessage("Could not complete the operatin please contact Admin");	  
			  }
			
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		logger.debug("->->->->User already exist with id " + user.getId());
		user.setErrorCode("404");
		user.setErrorMessage("User already exist with id : " + user.getId());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// http://localhost:8080/Collaboration/user/
	@RequestMapping(value = "/user/", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		logger.debug("->->->->calling method updateUser");
		if (userDAO.get(user.getId()) == null) {
			logger.debug("->->->->User does not exist with id " + user.getId());
			user = new User(); // ?
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with id " + user.getId());
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		userDAO.update(user);
		logger.debug("->->->->User updated successfully");
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}


	// http://localhost:8081/CollaborationBackEnd/user/abbas
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("id") String id) {
		logger.debug("->->calling method getUser");
		logger.debug("->->id->->" + id);
		User user = userDAO.get(id);
		if (user == null) {
			logger.debug("->->->-> User does not exist wiht id" + id);
			user = new User(); //To avoid NLP - NullPointerException
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		logger.debug("->->->-> User exist wiht id" + id);
		logger.debug(user.getFirstName());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/accept/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> accept(@PathVariable("id") String id) {
		logger.debug("Starting of the method accept");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			logger.debug("-->-->user not logged in. Creating appropriate error code and message");
			user.setErrorCode("404");
			user.setErrorMessage("You have not logged in. Please login as admin to Accept the registration of user :" + id);	
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){
			logger.debug("-->-->user not logged in as admin. Cant allow to make changes to make user Active");
			user.setErrorCode("404");
			user.setErrorMessage("Unauthorised Access.  Cant allow to make changes to make user Active.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		user = userDAO.get(id);
		if (user == null) {
			logger.debug("There is no user with the id : " + id);
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist");
			return new ResponseEntity<User>(user, HttpStatus.OK); 
		}
		
		if(user.getStatus()=='A'){
			logger.debug("the user: " + id+ " has already been accepted as active user.");
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("the user: " + id+ " has already been accepted as active user.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
				
		user = updateStatus(id, 'A', "");
		logger.debug("Ending of the method accept");
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	@RequestMapping(value = "/reject/{id}/{reason}", method = RequestMethod.GET)
	public ResponseEntity<User> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		logger.debug("Starting of the method reject");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			logger.debug("-->-->user not logged in. Creating appropriate error code and message");
			user.setErrorCode("404");
			user.setErrorMessage("You have not logged in. Please login as admin to Accept the registration of user :" + id);	
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){
			logger.debug("-->-->user not logged in as admin. Cant allow to make changes to make user Active");
			user.setErrorCode("404");
			user.setErrorMessage("Unauthorised Access.  Cant allow to make changes to make user Active.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		
		user = userDAO.get(id);
		if (user == null) {
			logger.debug("There is no user with the id : " + id);
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist in database");
			return new ResponseEntity<User>(user, HttpStatus.OK); 
		}
		
		if(user.getStatus()=='R'){
			logger.debug("the user: " + id+ " has already been rejected as active user.");
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("the user: " + id+ " has already been rejected as active user.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		user = updateStatus(id, 'R', reason);
		logger.debug("Ending of the method reject");
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	private User updateStatus(String id, char status, String reason) {
		logger.debug("Starting of the method updateStatus");
		logger.debug("status: " + status);
		user = userDAO.get(id);
		user.setStatus(status);
		user.setReason(reason);
			
			try {
				logger.debug("-----------> trying to update status of user");
				userDAO.update(user);
				user.setErrorCode("200");
				user.setErrorMessage("Updated the status successfully");
				return user;
			} catch (Exception e) {
				logger.debug("Error occured while updating user information.");
				user.setErrorCode("200");
				user.setErrorMessage("Error occured while updating user information.");
				return user;
			}

	}

	@RequestMapping(value = "/myProfile", method = RequestMethod.GET)
	public ResponseEntity<User> myProfile() {
		logger.debug("->->calling method myProfile");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			logger.debug("-->-->user not logged in. Creating appropriate error code and message");
			user = new User(); 
			user.setErrorCode("404");
			user.setErrorMessage("Please login to check your profile.");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		try {
			user = userDAO.get(loggedInUserID);
			logger.debug("->->->-> User exist with id" + loggedInUserID);
			logger.debug(user.getFirstName());
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (Exception e) {
			user = new User(); // Do wee need to create new user?
			user.setErrorCode("404");
			user.setErrorMessage("Error occured while retrieving user from db");
			logger.debug("->->->->Error occured while retrieving user from db");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/user/authenticate/", method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestBody User user) {
		logger.debug("->->->->calling method authenticate");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID!=null){
			logger.debug("-->-->Previous user still logged in.  Cant login before the previous user logout");
			user = new User(); 
			user.setErrorCode("404");
			user.setErrorMessage("someone is logged in. Cant login unless previous user logout.");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		
		user = userDAO.authenticate(user.getId(), user.getPassword());
		if (user == null) {
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("Invalid Credentials.  Please enter valid credentials");
			logger.debug("->->->->In Valid Credentials");
		} else	{
			user.setErrorCode("200");
			user.setErrorMessage("You have successfully logged in.");
			user.setIsOnline('Y');
			logger.debug("->->->->Valid Credentials");
			httpSession.setAttribute("loggedInUser", user);
			httpSession.setAttribute("loggedInUserID", user.getId());
			httpSession.setAttribute("loggedInUserRole", user.getRole());

			//friendDAO.setOnline(user.getId());
			userDAO.setOnline(user.getId());
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public ResponseEntity<User> logout() {
		logger.debug("->->->->calling method logout");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		//friendDAO.setOffLine(loggedInUserID);
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			logger.debug("-->-->user not logged in.");
			user = new User(); 
			user.setErrorCode("404");
			user.setErrorMessage("No one was logged in. No need to logout.");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		logger.debug("-->-->logged in user found as :" + loggedInUserID);
		try {
			userDAO.setOffLine(loggedInUserID);
			httpSession.invalidate();
			user.setErrorCode("200");
			user.setErrorMessage("You have successfully logged out.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (Exception e) {
			user.setErrorCode("404");
			user.setErrorMessage("Error occured while logging out.");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}

}
