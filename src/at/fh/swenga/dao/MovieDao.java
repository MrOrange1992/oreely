package at.fh.swenga.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fh.swenga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.service.GetProperties;
import at.fh.swenga.service.SearchHelper;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonPeople;


@Repository
@Transactional
public class MovieDao
{
	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	private GenreDao genreDao;

	@Autowired
	private ActorDao actorDao;

	@Autowired
	private UserMovieDao userMovieDao;


	private GetProperties gp = new GetProperties();
	private Properties properties = gp.getPropValues();

	private String apiKey = properties.getProperty("apiKey");
	
	private TmdbMovies tmdbMovies = new TmdbApi(apiKey).getMovies();
	private TmdbSearch tmdbSearch = new TmdbApi(apiKey).getSearch();
	private TmdbPeople tmdbPeople = new TmdbApi(apiKey).getPeople();
	
	private List<SearchHelper> lastSearches = new ArrayList<>();
	
	DateFormat format = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH);

	public List<MovieModel> getMovies()
	{
		TypedQuery<MovieModel> typedQuery = entityManager.createQuery("select m from MovieModel m", MovieModel.class);
		List<MovieModel> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}


	public List<MovieModel> getUserMovies(User user)
	{
		// Inner JOIN UserMovie um ON m.id = um WHERE um.owner_userName = :userName
		TypedQuery<MovieModel> typedQuery = entityManager.createQuery("SELECT m FROM MovieModel m left join fetch  m.userMovies um where um.owner = :user", MovieModel.class);
		typedQuery.setParameter("user", user);

		List<MovieModel> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}



	//TODO replace mapMovie with other lightweight method
	//because mapping movie genres and actors for multiple movies takes too long
	public List<MovieModel> searchMovies(String searchString)
	{
		System.out.println("SEARCH STARTED");
		
		long startTime = System.currentTimeMillis();
		
        MovieResultsPage result = tmdbSearch.searchMovie(searchString, null, "en-US", false, null);
        List<MovieDb> resultList = result.getResults();      
        List<MovieModel> movieModelList = new ArrayList<MovieModel>();
        
        lastSearches.add(new SearchHelper(searchString, resultList));
        
    	int maxMovies = 6;
    	int count = 0;
        
        for (MovieDb mDB : resultList)
        {	
        	if (count < maxMovies)
        	{
        		movieModelList.add(mapMovie(tmdbMovies, mDB.getId(), false));		//Replace!!!
        		count ++;
        		System.out.println(count);
        	}
        	else break;
        }
		
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
        
        return movieModelList;
	}
	
	public List<MovieModel> searchMovies(String searchString, int page)
	{
		System.out.println("SEARCH STARTED");
		
		long startTime = System.currentTimeMillis();
		
		List<MovieDb> resultList = null;
		
	    for(SearchHelper sh : lastSearches){
	        if(sh.getSearchString() == searchString)
	        	resultList = sh.getResultList();
	    }
		     
        List<MovieModel> movieModelList = new ArrayList<MovieModel>();
        
    	int maxMovies = page + 6;
    	int count = page;
        
        for (MovieDb mDB : resultList)
        {	
        	if (count < maxMovies)
        	{
        		movieModelList.add(mapMovie(tmdbMovies, mDB.getId(), false));		//Replace!!!
        		count ++;
        		System.out.println(count);
        	}
        	else break;
        }
		
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
        
        return movieModelList;
	}

	public MovieModel getMovieById(int id) throws DataAccessException
	{
		return entityManager.find(MovieModel.class, id);
	}

	//TODO FR: Add Genre und Actor in DAOs auslagern
	public MovieModel mapMovie(TmdbMovies movies, int id, boolean fullContent)
	{		
		//TmdbMovies movies = new TmdbApi(apiKey).getMovies();
    	MovieDb movie = movies.getMovie(id, "en", MovieMethod.credits);
    	MovieModel movieModel = new MovieModel();

    	movieModel.setId(movie.getId());
    	movieModel.setTmdb_id(movie.getId());
    	movieModel.setTitle(movie.getTitle());
    	try{
        	movieModel.setOverview(movie.getOverview());
        	movieModel.setAdult(movie.isAdult());        	
        	movieModel.setVote_average(movie.getVoteAverage());
        	movieModel.setVote_count(movie.getVoteCount()); 	
    	}
    	catch (Exception e) { System.out.println("ERROR in 1st block"); }

    	try 
    	{ 
    		if (movie.getReleaseDate() != "") 
    			movieModel.setRelease_date(format.parse(movie.getReleaseDate()));
    	}
    	catch (ParseException e) { e.printStackTrace(); }
    	
    	try{
        	movieModel.setRuntime(movie.getRuntime());
        	movieModel.setBudget(movie.getBudget());
        	movieModel.setRevenue(movie.getRevenue());
        	movieModel.setPoster_path(movie.getPosterPath());
        	movieModel.setOriginal_name(movie.getOriginalTitle());
        	movieModel.setHomepage(movie.getHomepage());
    	}
    	catch (Exception e) { System.out.println("ERROR in 2nd block"); }
    	
    	//System.out.println(movie.getGenres().get(0).getName());
    	
    	//FR: Map themoviedbapi.model.Genre to GenreModel
    	for (info.movito.themoviedbapi.model.Genre genre : movie.getGenres())
    	{
    		Genre gm = new Genre(genre.getId(), genre.getName());
    		movieModel.addGenre(gm);
    		gm.addMovie(movieModel);
    	}

		if (fullContent)
		{
			List<PersonCast> movieCast = movie.getCast();

			if (movie.getCast().size() >= 7)
				movieCast = movieCast.subList(0,6);

			for (PersonCast cast : movieCast)
			{
				PersonPeople tmdbActor = tmdbPeople.getPersonInfo(cast.getId());

				Actor actor = new Actor(tmdbActor.getId(), tmdbActor.getName(), tmdbActor.getBirthday());
				movieModel.addActor(actor);
				actor.addMovie(movieModel);
			}
		}

    	return movieModel;
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
