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

	public MovieList getMovieListByOwner(User owner) {
		try {
			TypedQuery<MovieList> typedQuery = entityManager
					.createQuery("select ml from MovieList ml where ml.owner = :owner", MovieList.class);
			typedQuery.setParameter("owner", owner);
			MovieList movieList = typedQuery.getSingleResult();
			return movieList;
		} catch (NoResultException e) {
			return null;
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

	public void persist(MovieList movieList) {
		entityManager.persist(movieList);
	}

	public MovieList merge(MovieList movieList) {
		return entityManager.merge(movieList);
	}

	public void delete(MovieList movieList) {
		entityManager.remove(movieList);
	}

}
