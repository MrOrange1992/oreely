package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.MovieModel;
import at.fh.swenga.model.User;
import at.fh.swenga.model.UserRole;

@Repository
@Transactional
public class UserDao {

	@PersistenceContext
	protected EntityManager entityManager;
	
	public User findByUsername(String userName) {
		TypedQuery<User> typedQuery = entityManager.createQuery(
				"select u from User u where u.userName = :name", User.class);
		typedQuery.setParameter("name", userName);
		return typedQuery.getSingleResult();
	}

	public void persist(User user)
	{
		entityManager.persist(user);
	}
	
	public User merge(User user)
	{
		return entityManager.merge(user);
	}

	public void delete(User user)
	{
		entityManager.remove(user);
	}

	public void persistRole(UserRole role)
	{
		entityManager.persist(role);
	}
}
