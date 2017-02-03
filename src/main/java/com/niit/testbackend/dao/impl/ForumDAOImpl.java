package com.niit.testbackend.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.niit.testbackend.dao.ForumDAO;
import com.niit.testbackend.model.Forum;
import com.niit.testbackend.model.ForumComment;

@Repository("ForumDAO")
public class ForumDAOImpl implements ForumDAO{
	
	private static final Logger log = LoggerFactory.getLogger(ForumDAOImpl.class);
	
	@Autowired
	SessionFactory sessionFactory;
	
	public ForumDAOImpl (SessionFactory sessionFactory){
		
		try {
			this.sessionFactory = sessionFactory;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public List<Forum> getAllForum() {
		log.debug("->->Starting of the getAllForum method in ForumDAOImpl");
		String hql = "from forum where status='A'";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Transactional
	public Forum getForumDetails(Long forumID) {
		log.debug("->->Starting of the getForumDetails(forumID) method in ForumDAOImpl");
		return (Forum) sessionFactory.getCurrentSession().get(Forum.class, forumID);
	}

	@Transactional
	public List<Forum> getMyForum(String userID) {
		log.debug("->->Starting of the getMyForum(userID) method in ForumDAOImpl");
		String hql = "from forum where userID = '"+userID+"' and status = 'A'";
										// A-Approved, N-New, R-Rejected
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Transactional
	public boolean saveForum(Forum forum) {
		log.debug("->->Starting of the saveForum(forum) method in ForumDAOImpl");
		try {
			sessionFactory.getCurrentSession().save(forum);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean updateForum(Forum forum) {
		log.debug("->->Starting of the updateForum(forum) method in ForumDAOImpl");
		try {
			sessionFactory.getCurrentSession().update(forum);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean deleteForum(Forum forum) {
		log.debug("->->Starting of the deleteForum(forum) method in ForumDAOImpl");
		try {
			sessionFactory.getCurrentSession().delete(forum);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	@Transactional
	public List<ForumComment> getForumComment(Long forumID) {
		log.debug("->->Starting of the getForumComment(forumID) method in ForumDAOImpl");
		String hql = "from forumcomment where forumID = "+forumID;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Transactional
	public boolean saveForumComment(ForumComment forumComment) {
		log.debug("->->Starting of the saveForumComment(forumComment) method in ForumDAOImpl");
		try {
			sessionFactory.getCurrentSession().save(forumComment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean deleteForumComment(ForumComment forumComment) {
		log.debug("->->Starting of the deleteForumComment(forumComment) method in ForumDAOImpl");
		try {
			sessionFactory.getCurrentSession().delete(forumComment);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean deleteAllCommentOfForum(Long forumID) {
		log.debug("->->Starting of the deleteAllCommentOfForum(forumID) method in BlogDAOImpl");
		String hql = "delete from forumcomment where forumID ="+forumID;
		
		try {
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
}
