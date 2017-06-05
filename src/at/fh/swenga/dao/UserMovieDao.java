package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.UserMovie;

@Repository
@Transactional
public class UserMovieDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<UserMovie> getUserMovies() {
		TypedQuery<UserMovie> typedQuery = entityManager.createQuery("select um from UserMovie um", UserMovie.class);
		List<UserMovie> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public UserMovie getUserMovie(String name) {
		try {
			TypedQuery<UserMovie> typedQuery = entityManager
					.createQuery("select um from UserMovie um where um.name = :name", UserMovie.class);
			typedQuery.setParameter("name", name);
			UserMovie userMovie = typedQuery.getSingleResult();
			return userMovie;
		} catch (NoResultException e) {
			return null;
		}
	}
}
