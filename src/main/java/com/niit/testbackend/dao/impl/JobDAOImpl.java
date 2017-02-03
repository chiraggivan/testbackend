package com.niit.testbackend.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.niit.testbackend.dao.JobDAO;
import com.niit.testbackend.model.Job;
import com.niit.testbackend.model.JobApplication;

@Repository("JobDAO")
public class JobDAOImpl implements JobDAO{

	private static final Logger log = LoggerFactory.getLogger(JobDAOImpl.class);
	
	@Autowired(required=true)
	private SessionFactory sessionFactory;
	
	public JobDAOImpl(SessionFactory sessionFactory)
	{
		try{
			this.sessionFactory = sessionFactory;
		}catch (Exception e){
			log.error("Unable to connect to db");
			e.printStackTrace();
		}
	}

	@Transactional
	public List<Job> getAllOpenedJobs() {
		log.debug("-->--> starting method getAllOpenedJobs inJobDAOImpl");
		String hql = "from Job where status = 'V'";
		log.debug("-->--> String hql :"+ hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("-->--> SQL query created as : select * "+ hql);
		return query.list();		
	}

	@Transactional
	public Job getJobDetails(Long id) {
		log.debug("-->--> starting method getJobDetails(jobID) in JobDAOImpl.java");
		Job job = (Job) sessionFactory.getCurrentSession().get(Job.class, id);
		log.debug("-->--> Got job object, trying to check if its null or empty");
		if (job==null){
			log.debug("-->-->No such JobID: "+id);
			log.debug("-------------->--> returning null object");
			return null;
		}
		return job;		
	}

	@Transactional
	public boolean updateJob(Job job) {
		log.debug("-->--> starting method updateJob(job) in JobDAOImpl.java");
		try {
			sessionFactory.getCurrentSession().update(job);
			log.debug("-->--> updated job details. Returning 'true'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("-->--> error occured while updating Job object. Returning 'false'");
			return false;
		}
	}

	@Transactional
	public boolean updateJobApp(JobApplication jobApplication) {
		log.debug("-->--> starting method updateJob(jobApplication) in JobDAOImpl.java");
		try {
			sessionFactory.getCurrentSession().update(jobApplication);
			log.debug("-->--> updated jobApplication details. Returning 'true'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("-->--> error occured while updating Job object. Returning 'false'");
			return false;
		}
	}

	private Long getAppMaxId(){
		
		Long maxID = 100L;
		try {
			String hql = "select max(id) from JobApplication";
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			maxID = (Long) query.uniqueResult();
			// or other method is 
			//maxID = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			maxID = 100L;
			return maxID;
		}
		return maxID+1;
	}
	
private Long getJobMaxId(){
		
		Long maxID = 100L;
		try {
			String hql = "select max(id) from Job";
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			maxID = (Long) query.uniqueResult();
			// or other method is 
			//maxID = (Long) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			maxID = 100L;
			return maxID;
		}
		return maxID+1;
	}
	
	@Transactional
	public boolean save(Job job){
		log.debug("-->-->starting of save(job object) method in JobDAOImpl.java");
		try {
			log.debug("-->-->starting of private getJobMaxId() method and setting it "
					+ "to job.id in JobDAOImpl.java");
			job.setId(getJobMaxId());
			log.debug("-->-->got maxId for job.id as : "+ job.getId());
			sessionFactory.getCurrentSession().save(job);
			log.debug("-->-->saved job in JobApplication table");
			return true;
		} catch (Exception e) {
			log.debug("-->-->error occured in try-catch block of save(job)");
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean  save(JobApplication jobApplication)  {
		log.debug("-->-->starting of save(jobApplication object) method in JobDAOImpl.java");
			try {
				log.debug("-->-->setting jobApplication.ID by calling private getAppMaxID() method");
				jobApplication.setId(getAppMaxId());
				log.debug("-->-->got maxId for jobApplication.id as : "+ jobApplication.getId());
				log.debug("-->-->saving jobApplication in db");
				sessionFactory.getCurrentSession().save(jobApplication);
				log.debug("-->-->saved jobApplication in db and returning true in applyForJob method injobController.java");
				return true;
			} catch (Exception e) {
				log.debug("-->-->got error while setting jobApplication.ID or while saving jobApplication in db."
						+ "Returning 'false' in applyForJob method injobController.java");
				e.printStackTrace();
				return false;
			}	
	}

	@Transactional
	public List<Job> getMyAppliedJobs(String userID) {
		log.debug("-->-->starting of getMyAppliedJobs(userID) method in JobDAOImpl.java");
		String hql = "from JobApplication where userID ='"+userID+"'";
		log.debug("-->--> String hql :"+ hql);		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("-->--> SQL Query Created as : select * "+hql);
		log.debug("-->--> Checking if the above query returned null or empty list");
		if(query.list().isEmpty()){
			log.debug("-->--> No record found for the query : select * "+hql );
			log.debug("-->--> sending appropriate message in errorCode and message for no record found." );
			Job job = new Job();
			job.setErrorCode("404");
			job.setErrorMessage("You have not applied for any job.");
			List<Job> jobs= new ArrayList<Job>();
			jobs.add(job);
			return jobs;
		}
		log.debug("-->--> records found and returning list of your jobs to myAppliedJobs method in JobController.java");
		return query.list();
	}

	@Transactional
	public JobApplication getJobApplication(String userID, Long jobID) {
		log.debug("-->-->calling getJobApplication(userID, jobID) method of JobDAOImpl.java");
		String hql = "from JobApplication where userID = '"+userID+"' and jobID ='"+jobID+"'";
		log.debug("-->--> String hql :"+ hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("-->-->Query query created as : select * "+hql);
		log.debug("-->-->returning result of query back in private updateJobApplication method");
		JobApplication j = (JobApplication) query.uniqueResult();
		if(j!=null){
			log.debug("-->--> If query --- select * "+hql+" result is unique. returing jobApplication object ");
			return j;
		}
		
		else{
			log.debug("-->--> If query --- select * "+hql+" result is Null. returning null");
			return null;
		}
		/*log.debug("-->--> If query --- select * "+hql+" result is more than one row.");
		JobApplication jobApplication = new JobApplication();
		jobApplication.setErrorCode("401");
		jobApplication.setErrorMessage("There are more than one records in db for same jobID :"
										+jobID+ " and userID : "+userID );
		log.debug("-->-->Query returned more than more row. Error in db. check db table JobApplication "
				+ "with userID :"+userID+" and jobID"+jobID );
		return jobApplication;*/
	}

	@Transactional
	public JobApplication getJobApplication(Long jobID) {
		log.debug("-->-->calling getJobApplication(jobID) method of JobDAOImpl.java");
		JobApplication jobApplication =  (JobApplication) sessionFactory.getCurrentSession().get(JobApplication.class, jobID);
		log.debug("-->--> Got jobApplication object, trying to check if its null or empty");
		if (jobApplication==null||jobApplication.getId().equals("")){
			log.debug("-->-->No such JobID: "+jobID+". Creating appropriate error code and message");
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("There is no such jobID as : "+ jobID );
			return jobApplication;
		}
		return jobApplication;
	}
	
	
	
}
