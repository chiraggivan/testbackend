package com.niit.testbackend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.testbackend.dao.JobDAO;
import com.niit.testbackend.dao.UserDAO;
//import com.niit.testbackend.model.Friend;
import com.niit.testbackend.model.Job;
import com.niit.testbackend.model.JobApplication;
import com.niit.testbackend.model.User;

@RestController
public class JobController {

	private static final Logger log = LoggerFactory.getLogger(JobController.class);
	
	@Autowired
	JobDAO jobDAO;
	
	@Autowired
	Job job;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	JobApplication jobApplication;
	
	@Autowired
	HttpSession httpSession;
	
	//@CrossOrigin(origins="http://localhost:8088") if the front end is on different port number.
	//for eg . bank can use it to define which vendors can use this method by specifying url. like flipkart,amazon,etc
	
	private boolean checkUserExist(String userID){
		log.debug("-->-->Checking FriendID :"+userID+"available as user in User Table.");
		if(userDAO.get(userID) != null)
		return true;
		else
			return false;
	}
	
	@RequestMapping(value = "/getAllJobs/", method = RequestMethod.GET)
	public ResponseEntity<List<Job>> getAllJobs(){
		log.debug("-->-->starting of getAllJobs method in JobController.java");
		List<Job> jobs = jobDAO.getAllOpenedJobs();
		if(jobs.isEmpty()||jobs.get(0)==null){
			Job job = new Job();
			job.setErrorCode("404");
			job.setErrorMessage("Currently there are no jobs available to apply.");
			jobs.add(job);
		}
		return new ResponseEntity<List<Job>>(jobs,HttpStatus.OK);
	}
	
	@RequestMapping(value="/myAppliedJobs/", method = RequestMethod.GET)
	public ResponseEntity<List<Job>> myAppliedJobs(){
		log.debug("-->-->starting of myAppliedJobs method in JobController.java");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if (loggedInUserID==null||loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			job.setErrorCode("403");
			job.setErrorMessage("User not logged in. Please login first");
			List<Job> jobs = new ArrayList<Job>();
			jobs.add(job);
			return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
		}
		log.debug("-->-->loggedUserID not null and calling getMyAppliedJobs(userID) method in JobDAOImpl.java");
		List<Job> jobs = jobDAO.getMyAppliedJobs(loggedInUserID);
		
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getJobDetails/{jobID}", method = RequestMethod.GET)
	public ResponseEntity<Job> getJobDetails(@PathVariable("jobID") Long jobID){
		log.debug("-->-->starting of getJobDetails(jobID) method in JobController.java");
		log.debug("-->-->sending it to getJobDetails(jobID) method in JobDAOImpl.java");
		Job job = jobDAO.getJobDetails(jobID);
		if(job==null){
			Job j = new Job();
			j.setErrorCode("404");
			j.setErrorMessage("No such job exist with job id : "+ jobID);
			return new ResponseEntity<Job>(j,HttpStatus.OK);
			
		}
		log.debug("-->-->sending Job Oject from JobController to Frontend.");
		return new ResponseEntity<Job>(job,HttpStatus.OK);
	}
	
	@RequestMapping(value="/postAJob", method = RequestMethod.POST)
	public ResponseEntity<Job> postAJob(@RequestBody Job job){
		log.debug("-->-->starting of postAJob(job object) method in JobController.java");
		job.setStatus('V');
		log.debug("-->-->calling of save(job object) method in JobDAOImpl.java");
		if (jobDAO.save(job)== false)
		{
			job.setErrorCode("404");
			job.setErrorMessage("Not a able to post the job in db at the current moment");
			log.debug("Not a able to post the job in db at the current moment");
		}else {
			job.setErrorCode("200");
			job.setErrorMessage("New job successfully posted and saved in db with job name :" + job.getName());
			log.debug("New job successfully posted and saved in db with job name :" + job.getName());
		}
		return new ResponseEntity<Job> (job, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/applyForJob/{jobID}", method = RequestMethod.POST)
	public ResponseEntity<JobApplication> applyForJob(@PathVariable("jobID") Long jobID){
		log.debug("-->-->starting of applyForJob(jobID) method in JobController.java");
		
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(loggedInUserID==null || loggedInUserID.isEmpty()){
			log.debug("-->-->user not logged in. Creating appropriate error code and message");
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("You have not logged in. Please login to apply for this job");
			
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
		}
		
		log.debug("-->-->Trying to find if job exist with jobID"+jobID);
		if(jobDAO.getJobDetails(jobID)==null){
			log.debug("-->-->No job with jobID :"+
					jobID+" Creating appropriate error code and message");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("No such Job exist with jobID :"+jobID);
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);
			}
		log.debug("Both loggedInUserID and jobID exist. "
				+ "Checking if user has applied for same job before");
		if(jobDAO.getJobApplication(loggedInUserID, jobID)!=null){
			log.debug("-->-- userID : "+loggedInUserID+" has already applied for the job "+jobID);
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("you have already applied for the job : "+jobID);
			log.debug("You have not logged in. Please login to apply for this job");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
		}
		
		log.debug("-->--> user not applied this job before "
				+ "setting userID, jobID and status ='N' for jobApplication object");
		jobApplication.setUserID(loggedInUserID);
		jobApplication.setJobID(jobID);
		jobApplication.setDateApplied(new Date());
		jobApplication.setStatus('N');//N-Newly Applied; C-Call for Interview; S-Selected, R-Rejected
		log.debug("-->-->calling save(jobApplication object) methog in JobDAOImpl");			
		if (jobDAO.save(jobApplication)){
			jobApplication.setErrorCode("200");
			jobApplication.setErrorMessage("Job successfully applied by userID: "+loggedInUserID+" for the jobID: "+jobID);
			log.debug("New job successfully applied in db with jobID: " + jobID+ " and userID : "+ loggedInUserID);
		}else{
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("Not a able to apply job in db at the current moment");
			log.debug("Not a able to apply job in db at the current moment");		
		}
		
		return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
	}
	
	@RequestMapping(value="/callForInterview/{userID}/{jobID}", method = RequestMethod.PUT)
	public ResponseEntity<JobApplication> callForInterview(@PathVariable("userID") String userID, @PathVariable("jobID") Long jobID){
		log.debug("-->-->starting of callForInterview(userID, jobID) method in JobController.java");
		log.debug("-->-->checking if the user is logged In and is he an Employee or Admin. ");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID==null){			
			log.debug("-->-->User not logged in to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("Please login as Employee/Admin to make the changes.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){			
			log.debug("-->-->User not logged in as employee/admin to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("You are not authorised to do this process. Please login in as admin/employee.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		log.debug("-->-->verifying if there is a job application with userID :"+userID+" and jobID : "+jobID);
		JobApplication jobApplication = jobDAO.getJobApplication(userID, jobID);
		if(jobApplication==null){
			log.debug("-->--> No such Job application not found in db.");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("There is no job application with userID :"+userID+" and jobID :"+jobID);
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);
		}
		
		log.debug("-->-->verified. Job application with userID : "+userID+" and jobID :"+jobID+" exist");
		if(jobApplication.getStatus()=='C'){
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("The current job application is already at 'call for interview' stage.");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);			
		}
		log.debug("-->-->calling private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'C') method");
		jobApplication = updateJobApplication(jobApplication,userID, jobID, 'C');		
		log.debug("-->-->received updated  job application from private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'C') method");
		return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
	}
	
	@RequestMapping(value="/rejectedJobApplication/{userID}/{jobID}", method = RequestMethod.PUT)
	public ResponseEntity<JobApplication> rejectedJobApplication(@PathVariable("userID") String userID, @PathVariable("jobID") Long jobID){
		log.debug("-->-->starting of rejected(userID, jobID) method in JobController.java");
		log.debug("-->-->checking if the user is logged In and is he an Employee or Admin. ");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID==null){			
			log.debug("-->-->User not logged in to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("Please login as Employee/Admin to make the changes.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){			
			log.debug("-->-->User not logged in as employee/admin to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("You are not authorised to do this process. Please login in as admin/employee.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		log.debug("-->-->verifying if there is a job application with userID :"+userID+" and jobID : "+jobID);
		JobApplication jobApplication = jobDAO.getJobApplication(userID, jobID);
		if(jobApplication==null){
			log.debug("-->--> No such Job application not found in db.");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("There is no job application with userID :"+userID+" and jobID :"+jobID);
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);
		}
		
		log.debug("-->-->verified. Job application with userID : "+userID+" and jobID :"+jobID+" exist");
		if(jobApplication.getStatus()=='R'){
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("The current job application is already being REJECTED.");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);			
		}
		log.debug("-->-->calling private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'R') method");
		jobApplication = updateJobApplication(jobApplication,userID, jobID, 'R');		
		log.debug("-->-->received updated  job application from private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'R') method");
		return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
	}
	
	@RequestMapping(value="/selectUser/{userID}/{jobID}", method = RequestMethod.PUT)
	public ResponseEntity<JobApplication> selectUser(@PathVariable("userID") String userID, @PathVariable("jobID") Long jobID){
		log.debug("-->-->starting of selectUser(userID, jobID) method in JobController.java");
		log.debug("-->-->checking if the user is logged In and is he an Employee or Admin. ");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		if(loggedInUserID==null){			
			log.debug("-->-->User not logged in to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("Please login as Employee/Admin to make the changes.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		char loggedInUserRole = (Character) httpSession.getAttribute("loggedInUserRole");
		char s = 'S';
		if(loggedInUserRole==s){			
			log.debug("-->-->User not logged in as employee/admin to make update");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("You are not authorised to do this process. Please login in as admin/employee.");
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);	
		}
		log.debug("-->-->verifying if there is a job application with userID :"+userID+" and jobID : "+jobID);
		JobApplication jobApplication = jobDAO.getJobApplication(userID, jobID);
		if(jobApplication==null){
			log.debug("-->--> No such Job application not found in db.");
			JobApplication jobApp = new JobApplication();
			jobApp.setErrorCode("404");
			jobApp.setErrorMessage("There is no job application with userID :"+userID+" and jobID :"+jobID);
			return new ResponseEntity<JobApplication>(jobApp, HttpStatus.OK);
		}
		
		log.debug("-->-->verified. Job application with userID : "+userID+" and jobID :"+jobID+" exist");
		if(jobApplication.getStatus()=='S'){
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("The user : "+userID+" already selected for the job : "+jobID);
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);			
		}
		log.debug("-->-->calling private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'S') method");
		jobApplication = updateJobApplication(jobApplication,userID, jobID, 'S');		
		log.debug("-->-->received updated  job application from private updateJobApplication(jobApplication,"+userID+", "+jobID+", 'S') method");
		return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
	}
	
	private JobApplication updateJobApplication(JobApplication jobApplication, String userID, Long jobID, char status){
		log.debug("-->-->starting of PRIVATE updateJobApplication method in JobController.java");
		log.debug("-->-->setting status of job application to '"+status+"'");
									
			jobApplication.setStatus(status);
			if(jobDAO.updateJobApp(jobApplication)){
				jobApplication.setErrorCode("200");
				jobApplication.setErrorMessage("Job Application for UserID : "+ userID+
						" successfully modified as : " + jobApplication.getStatus());
				log.debug("Job Application for UserID : "+ userID+" successfully modified as : " + jobApplication.getStatus());
				return jobApplication;
			}else{
				return null;
			}

	}
}
