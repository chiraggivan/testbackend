package com.niit.testbackend.dao;

import java.util.List;

import com.niit.testbackend.model.Friend;

public interface FriendDAO {
	
	public List<Friend> getMyFriends(String userID);
	
	public Friend get(String userID, String friendID);
	
	public Friend getUnfriend(String userID, String friendID);
	
	public boolean save(Friend friend);
	
	public boolean update(Friend friend);
	
	public void delete(String userID, String friendID);
	
	public List<Friend> getNewFriendRequests(String userID);
	
	public boolean friendRequestExist(String friendID, String userID);
	
	public void setOnLine(String userID);
	
	public void setOffLine(String userID);
	
}
