package com.niit.testbackend.dao;

import java.util.List;

import com.niit.testbackend.model.User;

public interface UserDAO {

	public User get(String id);
	
	public List<User>  list();
	
	public List<User>  listOtherUser(String userID);
	
	public boolean save(User user);
	
	public boolean update(User user);
	
	public User get(String id, String password);
	
	public void delete(String id);
	
	public User authenticate(String id, String name);
	
	public void setOnline(String userID);
	
	public void setOffLine(String userID);
	
}
