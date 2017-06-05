package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.GenreModel;
import at.fh.swenga.model.MovieModel;

@Repository
@Transactional
public class GenreDao {
 
	@PersistenceContext
	protected EntityManager entityManager;
	
	public List<GenreModel> getGenres() 
	{	 
		TypedQuery<GenreModel> typedQuery = entityManager.createQuery(
				"select g from Genre g", GenreModel.class);
		List<GenreModel> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
 
	public GenreModel getGenre(String name) 
	{
		try {
			TypedQuery<GenreModel> typedQuery = entityManager.createQuery(
					"select g from Genre g where g.name = :name",
					GenreModel.class);
			typedQuery.setParameter("name", name);
			GenreModel genre = typedQuery.getSingleResult();
			return genre;
		} catch (NoResultException e) {
			return null;
		}
 
	}
	
	public void persist(GenreModel genre) 
	{
		entityManager.persist(genre);
	}

}