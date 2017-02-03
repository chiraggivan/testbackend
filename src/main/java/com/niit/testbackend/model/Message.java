package com.niit.testbackend.model;

public class Message {

	private String message;
	
	private String friendID;
	
	private int id;

	public Message(int id, String message){
		this.message=message;	
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFriendID() {
		return friendID;
	}

	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
