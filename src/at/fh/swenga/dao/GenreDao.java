package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fh.swenga.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.Genre;

@Repository
@Transactional
public class GenreDao {
 
	@PersistenceContext
	protected EntityManager entityManager;
	
	public List<Genre> getGenres() 
	{	 
		TypedQuery<Genre> typedQuery = entityManager.createQuery(
				"select g from Genre g", Genre.class);
		List<Genre> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public List<Genre> getUserGenres(User user)
	{
		try
		{
			TypedQuery<Genre> typedQuery = entityManager.createQuery("select g from Genre g JOIN FETCH g.users u WHERE u = :user", Genre.class);
			typedQuery.setParameter("user", user);
			List<Genre> typedResultList = typedQuery.getResultList();
			return typedResultList;
		}
		catch (NoResultException e) {
			return null;
		}
	}
 
	public Genre getGenre(String name) 
	{
		try
		{
			TypedQuery<Genre> typedQuery = entityManager.createQuery("select g from Genre g where g.name = :name", Genre.class);
			typedQuery.setParameter("name", name);
			Genre genre = typedQuery.getSingleResult();
			return genre;
		} catch (NoResultException e) {
			return null;
		}
 
	}
	
	public void persist(Genre genre) 
	{
		entityManager.persist(genre);
	}

	public Genre merge(Genre genre)
	{
		return entityManager.merge(genre);
	}


}