package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fh.swenga.model.MovieModel;
import at.fh.swenga.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.UserMovie;

@Repository
@Transactional
public class UserMovieDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<UserMovie> getUserMovies(String userName)
	{
		try
		{
			TypedQuery<UserMovie> typedQuery = entityManager.createQuery("select um from UserMovie um where um.owner_userName = :userName", UserMovie.class);
			typedQuery.setParameter("userName", userName);
			List<UserMovie> typedResultList = typedQuery.getResultList();

			return typedResultList;
		}
		catch (NoResultException e) { return null; }
	}

	public UserMovie getUserMovieByID(User owner, MovieModel movie)
	{
		try
		{
			TypedQuery<UserMovie> typedQuery = entityManager.createQuery("SELECT um FROM UserMovie um\n" +
					"WHERE um.owner = :owner AND um.movie = :movie", UserMovie.class);
			typedQuery.setParameter("owner", owner).setParameter( "movie", movie);
			UserMovie userMovie = typedQuery.getSingleResult();

			return userMovie;
		}
		catch (NoResultException e) { return null; }
	}

	public void persist(UserMovie UserMovie)
	{
		entityManager.persist(UserMovie);
	}

	public void delete(UserMovie UserMovie)
	{
		entityManager.remove(entityManager.contains(UserMovie) ? UserMovie : entityManager.merge(UserMovie));
	}

	public UserMovie merge(UserMovie userMovie)
	{
		return entityManager.merge(userMovie);
	}


}
