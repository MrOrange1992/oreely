package at.fh.swenga.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.MovieList;

@Repository
@Transactional
public class MovieListDao {
	 
		@PersistenceContext
		protected EntityManager entityManager;
		
		public List<MovieList> getMovieLists() {
			 
			TypedQuery<MovieList> typedQuery = entityManager.createQuery(
					"select ml from MovieList ml", MovieList.class);
			List<MovieList> typedResultList = typedQuery.getResultList();
			return typedResultList;
		}
		
		public MovieList getMovieList(String name) {
			try {
	 
				TypedQuery<MovieList> typedQuery = entityManager.createQuery(
						"select ml from MovieList ml where ml.name = :name",
						MovieList.class);
				typedQuery.setParameter("name", name);
				MovieList movieList = typedQuery.getSingleResult();
				return movieList;
			} catch (NoResultException e) {
				return null;
			}
		}

}
