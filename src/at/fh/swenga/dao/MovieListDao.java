package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.MovieList;
import at.fh.swenga.model.MovieModel;
import at.fh.swenga.model.User;

@Repository
@Transactional
public class MovieListDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<MovieList> getMovieLists() {

		TypedQuery<MovieList> typedQuery = entityManager.createQuery("select ml from MovieList ml", MovieList.class);
		List<MovieList> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public List<MovieList> getMovieListsByOwner(User owner)
	{
		try
		{
			TypedQuery<MovieList> typedQuery = entityManager
					.createQuery("select ml from MovieList ml where ml.owner = :owner", MovieList.class);
			typedQuery.setParameter("owner", owner);
			List<MovieList> movieLists = typedQuery.getResultList();
			return movieLists;
		} catch (NoResultException e) {
			return null;
		}
	}

	public MovieList getMovieListByID(int id) {
		try {
			TypedQuery<MovieList> typedQuery = entityManager
					.createQuery("select ml from MovieList ml where ml.id = :id", MovieList.class);
			typedQuery.setParameter("id", id);
			MovieList movieList = typedQuery.getSingleResult();
			return movieList;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Boolean isMovieInList(MovieModel movie, int listID)
	{
		try
		{
			TypedQuery<MovieModel> typedQuery = entityManager.createQuery("select m from MovieModel m JOIN FETCH m.movieLists ml where ml.id = :listID AND m = :movie", MovieModel.class);
			typedQuery.setParameter("listID", listID);
			typedQuery.setParameter("movie", movie);
			if (typedQuery.getSingleResult() == null)
				return false;
			else return true;
		}
		catch (NoResultException e)
		{
			return false;
		}
	}

	// public MovieList getMovieListByName(String name) {
	// try {
	// TypedQuery<MovieList> typedQuery = entityManager.createQuery(
	// "select ml from MovieList ml where ml.name = :name",
	// MovieList.class);
	// typedQuery.setParameter("name", name);
	// MovieList movieList = typedQuery.getSingleResult();
	// return movieList;
	// } catch (NoResultException e) {
	// return null;
	// }
	// }
	//
	// public List<MovieList> getMovieListsByName(String searchString) {
	// TypedQuery<MovieList> typedQuery = entityManager.createQuery(
	// "select ml from MovieList ml where ml.name like :search",
	// MovieList.class);
	// typedQuery.setParameter("search", searchString);
	// List<MovieList> typedResultList = typedQuery.getResultList();
	// return typedResultList;
	// }

	public void persist(MovieList movieList) { entityManager.persist(movieList); }

	public MovieList merge(MovieList movieList) {
		return entityManager.merge(movieList);
	}

	public void delete(MovieList movieList) { entityManager.remove(entityManager.contains(movieList) ? movieList : entityManager.merge(movieList)); }

}
