package com.niit.testbackend.dao;

import java.util.List;

import com.niit.testbackend.model.Job;
import com.niit.testbackend.model.JobApplication;

public interface JobDAO {

	public List<Job> getAllOpenedJobs();
	
	public Job getJobDetails(Long id);
	
	public boolean updateJob(Job job);
	
	public boolean updateJobApp(JobApplication jobApplication);
	
	public boolean save(JobApplication jobApplication);
	
	public boolean save(Job job);
	
	public List<Job> getMyAppliedJobs(String userID);
	
	public JobApplication getJobApplication(String userID, Long jobID);
	
	public JobApplication getJobApplication(Long jobID);
}
