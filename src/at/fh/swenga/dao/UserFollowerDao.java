package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.UserFollower;

@Repository
@Transactional
public class UserFollowerDao {
	
	@PersistenceContext
	protected EntityManager entityManager;
 
	public List<UserFollower> getFollowers() {
 
		TypedQuery<UserFollower> typedQuery = entityManager.createQuery(
				"select f from UserFollower f", UserFollower.class);
		List<UserFollower> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
 
	public UserFollower getFollower(String name) {
		try {
			TypedQuery<UserFollower> typedQuery = entityManager.createQuery(
					"select f from UserFollower f where f.name = :name",
					UserFollower.class);
			typedQuery.setParameter("name", name);
			UserFollower follower = typedQuery.getSingleResult();
			return follower;
		} catch (NoResultException e) {
			return null;
		}
 
	}

}
