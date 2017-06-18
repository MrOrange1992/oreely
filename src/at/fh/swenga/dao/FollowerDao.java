package at.fh.swenga.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.Follower;

@Repository
@Transactional
public class FollowerDao
{
	@PersistenceContext
	protected EntityManager entityManager;
 
	public List<Follower> getFollowers()
	{
		TypedQuery<Follower> typedQuery = entityManager.createQuery("select f from Follower f", Follower.class);
		List<Follower> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
 
	public Follower getFollower(Follower follower)
	{
		try
		{
			TypedQuery<Follower> typedQuery = entityManager.createQuery("select f from Follower f where f = :follower", Follower.class);
			typedQuery.setParameter("follower", follower);
			return 	typedQuery.getSingleResult();
		}
		catch (NoResultException e) { return null; }
	}

	public void persist(Follower follower) {
		entityManager.persist(follower);
	}

	public Follower merge(Follower userFollower) {
		return entityManager.merge(userFollower);
	}

	public void delete(Follower follower) {
		entityManager.remove(follower);
	}

}