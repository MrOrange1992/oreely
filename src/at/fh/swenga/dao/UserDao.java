package at.fh.swenga.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fh.swenga.model.Genre;
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
		System.out.println("findByUsername aufgerufen");

		/*StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		for (int i = 1; i < stackTraceElements.length; i++) {
			StackTraceElement ste = stackTraceElements[i];
			System.out.println(ste.getClassName().toString());
		}

		System.out.println(stackTraceElements.toString());*/

		try {
			TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u where u.userName = :name",
					User.class);
			typedQuery.setParameter("name", userName);
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<User> searchUsers(String searchString)
	{
		try
		{
			TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u where u.userName LIKE :searchString", User.class);
			typedQuery.setParameter("searchString", searchString);
			List<User> typedResultList = typedQuery.getResultList();

			return typedResultList;
		} catch (NoResultException e) { return null; }
	}

	public void removeGenres(User activeUser)
	{
		activeUser.setGenres(new ArrayList<>());
		merge(activeUser);
	}

	public void persist(User user) {
		entityManager.persist(user);
	}

	public User merge(User user) {
		return entityManager.merge(user);
	}

	public void delete(User user) {
		entityManager.remove(user);
	}

	public void persistRole(UserRole role) {
		entityManager.persist(role);
	}
}
