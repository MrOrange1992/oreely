package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.User;
import at.fh.swenga.model.UserRole;

@Repository
@Transactional
public class UserDao {

	@PersistenceContext
	protected EntityManager entityManager;
	
	public List<User> findUsersByUsername(String userName) {
		TypedQuery<User> typedQuery = entityManager.createQuery(
				"select u from User u where u.userName = :name", User.class);
		typedQuery.setParameter("name", userName);
		List<User> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
	
	public User findUserByUsername(String userName) {
		TypedQuery<User> typedQuery = entityManager.createQuery(
				"select u from User u where u.userName = :name", User.class);
		typedQuery.setParameter("name", userName);
		List<User> typedResultList = typedQuery.getResultList();
		User user = typedResultList.get(0);
		return user;
	}
	
	
	public void persist(User user) 
	{
		entityManager.persist(user);
	}
	
	public void persistRole(UserRole role)
	{
		entityManager.persist(role);
	}
}
