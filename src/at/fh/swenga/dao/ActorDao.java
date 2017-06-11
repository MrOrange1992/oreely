package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.Actor;

@Repository
@Transactional
public class ActorDao {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	public List<Actor> searchActors(String searchString) {
		TypedQuery<Actor> typedQuery = entityManager.createQuery(
				"select a from Actor a where a.firstName like :search or a.lastName like :search",
				Actor.class);
		typedQuery.setParameter("search", "%" + searchString + "%");
		List<Actor> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
	
	public void persist(Actor actor) 
	{
		entityManager.persist(actor);
	}

	public Actor merge(Actor actor)
	{
		return entityManager.merge(actor);
	}

}
