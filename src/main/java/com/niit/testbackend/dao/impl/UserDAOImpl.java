package com.niit.testbackend.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.niit.testbackend.dao.UserDAO;
import com.niit.testbackend.model.User;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO{

	
	 private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);
		 
		@Autowired(required=true)
		private SessionFactory sessionFactory;


		public UserDAOImpl(SessionFactory sessionFactory) {
			try {
				this.sessionFactory = sessionFactory;
			} catch (Exception e) {
				log.error(" Unable to connect to db");
				e.printStackTrace();
			}
		}

		@Transactional
		public List<User> list() {
			log.debug("->->Starting of the method list");
			@SuppressWarnings("unchecked")
			List<User> list = (List<User>) sessionFactory.getCurrentSession()
					.createCriteria(User.class)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

			return list;
		}

		@Transactional
		public List<User> listOtherUser(String userID) {
			log.debug("-->--> starting method listOtherUser inJobDAOImpl");
			String hql = "from User where role = 'S' and status = 'A' and id !='"+userID+"'";
			log.debug("-->--> String hql :"+ hql);
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			log.debug("-->--> SQL query created as : select * "+ hql);
			return query.list();
		}

		@Transactional
		public boolean update(User user) {
			log.debug("->->Starting of the method update");
			try {
				sessionFactory.getCurrentSession().update(user);
				return true;
			} catch (HibernateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		}
		
		@Transactional
		public boolean save(User user) {
			log.debug("->->Starting of the method save");
			try {
				sessionFactory.getCurrentSession().save(user);
				
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		}
		
			


		@Transactional
		public void delete(String id) {
			log.debug("->->Starting of the method delete");
			log.debug("->->id : " + id);
			User user = new User();
			user.setId(id);
			sessionFactory.getCurrentSession().delete(user);
		}

		@Transactional
		public User get(String id, String password) {
			log.debug("->->Starting of the method get");
			log.debug("->->id : " + id);
			log.debug("->->password : " + password);
			String hql = "from User where id=" + "'"+ id+"'  and password = "
					+"'" + password + "'";
		   return getUser(hql);
		}
		
		@Transactional
		public User get(String id) {
			log.debug("->->Starting of the method get");
			log.debug("->->id : " + id);
			String hql = "from User where id=" + "'"+ id + "'" ;
			 return getUser(hql);
			
		}
		
		private User getUser(String hql)
		{
			log.debug("->->Starting of the method getUser");
			log.debug("->->hql : " +hql);
	        Query query = sessionFactory.getCurrentSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			List<User> list = (List<User>) query.list();
			
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
			return null;
		}
		
		@Transactional
		public User authenticate(String id, String password) {
			log.debug("->->Starting of the method isValidUserDetails");
			String hql = "from User where status ='A' and id= '" + id + "' and " + " password ='" + password+"'";
			log.debug("->-> sql query is : Select  * " + hql);
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			List<User> list = (List<User>) query.list();
			
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
			
			return null;
		}


		@Transactional
		public void setOnline(String userID) {
			log.debug("Starting of the metnod setOnline");
			String hql =" UPDATE User	SET isOnline = 'Y' where id='" +  userID + "'" ;
			  log.debug("hql: " + hql);
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			query.executeUpdate();
			log.debug("Ending of the metnod setOnline");
			
		}
		
		@Transactional
		public void setOffLine(String userID) {
			log.debug("Starting of the metnod setOffLine");
			String hql =" UPDATE User	SET isOnline = 'N' where id='" +  userID + "'" ;
			  log.debug("hql: " + hql);
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			query.executeUpdate();
			log.debug("Ending of the metnod setOffLine");
			
		}


}
