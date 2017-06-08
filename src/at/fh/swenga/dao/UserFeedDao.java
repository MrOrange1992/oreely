package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.User;
import at.fh.swenga.model.UserFeed;

@Repository
@Transactional
public class UserFeedDao {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	public List<UserFeed> getAllUserFeed() 
	{	 
		TypedQuery<UserFeed> typedQuery = entityManager.createQuery(
				"select uf from UserFeed uf", UserFeed.class);
		List<UserFeed> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
 
	public UserFeed getUserFeed(User user) 
	{
		try {
			TypedQuery<UserFeed> typedQuery = entityManager.createQuery(
					"select uf from UserFeed uf where uf.user = :user",
					UserFeed.class);
			typedQuery.setParameter("user", user);
			UserFeed userFeed = typedQuery.getSingleResult();
			return userFeed;
		} catch (NoResultException e) {
			return null;
		}
 
	}
	
	public void persist(UserFeed userFeed) 
	{
		entityManager.persist(userFeed);
	}

}
