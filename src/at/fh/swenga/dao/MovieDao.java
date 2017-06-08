package at.fh.swenga.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.model.MovieModel;
import at.fh.swenga.service.GetProperties;
import at.fh.swenga.model.Genre;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;


@Repository
@Transactional
public class MovieDao {
	
	GetProperties gp = new GetProperties();
	Properties properties = gp.getPropValues();
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	public String apiKey = properties.getProperty("apiKey");
	
	public List<MovieModel> getMovies() 
	{
		TypedQuery<MovieModel> typedQuery = entityManager.createQuery("select m from MovieModel m",
				MovieModel.class);
		List<MovieModel> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}
	
	public List<MovieModel> searchMovies(String searchString) 
	{	
		TmdbSearch search = new TmdbApi(apiKey).getSearch();
		TmdbMovies movies = new TmdbApi(apiKey).getMovies();
        MovieResultsPage result = search.searchMovie(searchString, null, "en-US", false, null);        
        List<MovieDb> resultList = result.getResults();      
        List<MovieModel> movieModelList = new ArrayList<MovieModel>();
        
        for (MovieDb mDB : resultList)
        {
        	movieModelList.add(mapMovie(movies, mDB.getId()));
        }
        
        return movieModelList;
	}
	
	//TODO FR 
	public MovieModel mapMovie(TmdbMovies movies, int id)
	{
		//TmdbMovies movies = new TmdbApi(apiKey).getMovies();
    	MovieDb movie = movies.getMovie(id, "en", MovieMethod.credits);
    	MovieModel mm = new MovieModel();
    	
    	mm.setId(movie.getId());
    	mm.setTmdb_id(movie.getId());
    	mm.setTitle(movie.getTitle());
    	mm.setAdult(movie.isAdult());
    	mm.setVote_average(movie.getPopularity());
    	mm.setVote_count(movie.getVoteCount());
    	
    	DateFormat format = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH);
    	try 
    	{ 
    		if (movie.getReleaseDate() != "") 
    			mm.setRelease_date(format.parse(movie.getReleaseDate())); 
    	}
    	catch (ParseException e) { e.printStackTrace(); }
    	
    	mm.setRuntime(movie.getRuntime());
    	mm.setBudget(movie.getBudget());
    	mm.setRevenue(movie.getRevenue());
    	mm.setPoster_path(movie.getPosterPath());
    	mm.setOriginal_name(movie.getOriginalTitle());
    	mm.setHomepage(movie.getHomepage());
    	
    	//System.out.println(movie.getGenres().get(0).getName());
    	
    	//FR: Map themoviedbapi.model.Genre to GenreModel
    	for (info.movito.themoviedbapi.model.Genre genre : movie.getGenres())
    	{
    		Genre gm = new Genre(genre.getId(), genre.getName());
    		//genreDao.persist(gm);	//TODO FR: Error: detached entity passed to persist: at.fh.swenga.model.GenreModel
    		mm.addGenre(gm);
    		//System.out.println(genre.getName());
    	}
    	
    	//this.persist(mm);
    	return mm;	
	}
	
	
	public void persist(MovieModel movie) 
	{
		entityManager.persist(movie);
	}
	
	public MovieModel merge(MovieModel movie) 
	{
		return entityManager.merge(movie);
	}
	
	public void delete(MovieModel movie) 
	{
		entityManager.remove(movie);
	}

}
