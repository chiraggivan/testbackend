package com.niit.testbackend.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.niit.testbackend.dao.FriendDAO;
import com.niit.testbackend.model.Friend;

@Repository("FriendDAO")
public class FriendDAOImpl implements FriendDAO {
	
	public static final Logger log = LoggerFactory.getLogger(FriendDAOImpl.class);
	
	@Autowired(required=true)
	private SessionFactory sessionFactory;
	
	public FriendDAOImpl(SessionFactory sessionFactory){
		try {
			this.sessionFactory = sessionFactory;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Integer getMaxId(){
		
		int maxID = 100;
		String hql = "select max(id) from Friend";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		try {
			maxID =  (Integer) query.uniqueResult();
			return maxID+1;
		} catch (Exception e) {
			e.printStackTrace();
			maxID = 100;
			return maxID;
		}
	}

	@Transactional
	public List<Friend> getMyFriends(String userID) {
		log.debug("->->Starting of the getMyFriends(userID) method in FriendDAOImpl");
		String hql1 = "select friendID from Friend where userID = '" + userID + "' and status = 'A'";
		log.debug("->->Query for hql1 :" +hql1);
		String hql2 = "select userID from Friend where friendID = '"+userID+"' and status = 'A'";
		log.debug("->->Query for hql2 :" +hql2);
		Query query1 = sessionFactory.getCurrentSession().createQuery(hql1);
		log.debug("->->createQuery query1");
		Query query2 = sessionFactory.getCurrentSession().createQuery(hql2);
		log.debug("->->createQuery query2");
		List<Friend> list1 = query1.list();
		log.debug("->->Created list1 for hql1");
		List<Friend> list2 = query2.list();
		log.debug("->->Created list2 for hql2");
		log.debug("->->adding list2 data in list1");
		list1.addAll(list2);
		log.debug("->->Now List1 contains data from list1 and list2");
		return query1.list();
	}

	@Transactional
	public Friend get(String userID, String friendID) {
		log.debug("->->Starting of the get(userID,FriendID) method in FriendDAOImpl");
		String hql = "from Friend where userID = '"+userID+"' and friendID ='"+friendID+"'";
		log.debug("-->-->Sql Query created : select * "+hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("->->Trying to get unique result for the above query");
		Friend friend = (Friend) query.uniqueResult();
		return friend;
	}
	
	@Transactional
	public Friend getUnfriend(String userID, String friendID) {
		log.debug("->->Starting of the getUnfriend(userID,FriendID) method in FriendDAOImpl");
		String hql = "from Friend where userID = '"+userID+"' and friendID ='"+friendID+"' and status = 'A'";
		log.debug("-->-->Sql Query created : select * "+hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("->->Trying to get unique result for the above query");
		Friend friend = (Friend) query.uniqueResult();
		return friend;
	}

	@Transactional
	public boolean save(Friend friend) {
		log.debug("->->Starting of the save(Friend Object) method in FriendDAOImpl");
		friend.setId(getMaxId());
		friend.setIsOnline('Y');
		try {
			sessionFactory.getCurrentSession().save(friend);
			friend.setErrorCode("200");
			friend.setErrorMessage("friend succefully added in friend table.");
			log.debug("->->Friend object successfully saved in db table Friend.");
			return true;
		} catch (Exception e) {
			friend.setErrorCode("404");
			friend.setErrorMessage("friend could not be saved in friend table.");
			log.debug("->->Friend object could not be saved in db table Friend.");
			return false;
		}
	}

	@Transactional
	public boolean update(Friend friend) {
		log.debug("->->Starting of the update(Friend Object) method in FriendDAOImpl");
		try {
			sessionFactory.getCurrentSession().update(friend);
			log.debug("->->Friend object successfully updated in db table Friend.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("->->Friend object could not be updated in db table Friend.");
			return false;
		}
	}

	@Transactional
	public void delete(String userID, String friendID) {
		log.debug("->->Starting of the delete(userID,friendID) method in FriendDAOImpl");
		Friend friend = new Friend();
		friend.setUserID(userID);
		friend.setFriendID(friendID);
		sessionFactory.getCurrentSession().delete(friend);
		
	}

	@Transactional
	public List<Friend> getNewFriendRequests(String userID) {
		log.debug("->->Starting of the getNewFriendRequests(userID) method in FriendDAOImpl");
		// select * from where friendID='?' and status = 'P';
		String hql = "from Friend where friendID = '"+ userID + "' and status = 'P'";
		log.debug( "sql Query is :-->  select * from Friend where friendID = '"+ userID + "' and status = 'P'");
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		
		if (query.list().isEmpty()){
			Friend friend = new Friend();
			friend.setErrorCode("404");
			friend.setErrorMessage("You have no friend request currently.");
			List<Friend> friends = new ArrayList<Friend>();
			friends.add(friend);
			return friends;	
		}
		return query.list();
	}
	
	@Transactional
	public boolean friendRequestExist(String friendID, String userID) {
		log.debug("->->Starting of the friendRequestExist(friendID) method in FriendDAOImpl");
		String hql = "from Friend where friendID = '"+friendID+"' and userID = '"+userID+"' and status ='P'";
		log.debug("---------> sql query is : ---> select * "+hql);
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		
		if(query.list().isEmpty()){
			return false;
		}
		return true;
	}

	@Transactional
	public void setOnLine(String userID) {
		log.debug("->->Starting of the setOnLine(userID) method in FriendDAOImpl");
		String hql = "update Friend set isOnline = 'Y' where userID ='"+userID+"'";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("sql Query is ---> update Friend set isOnline = 'Y' where userID ='"+userID+"'");
		try {
			log.debug("trying to update in db isOnline = y with the above query");
			query.executeUpdate();
		} catch (Exception e) {
			log.debug("error occured while updating db isOnline = y with the above query");
			e.printStackTrace();
		}
	}

	@Transactional
	public void setOffLine(String userID) {
		log.debug("->->Starting of the setOffLine(userID) method in FriendDAOImpl");
		String hql = "update Friend set isOnline = 'N' where userID ='"+userID+"'";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		log.debug("sql Query is ---> update Friend set isOnline = 'N' where userID ='"+userID+"'");
		try {
			log.debug("trying to update in db isOnline = N with the above query");
			query.executeUpdate();
		} catch (Exception e) {
			log.debug("error occured while updating db isOnline = N with the above query");
			e.printStackTrace();
		}
	}
}
