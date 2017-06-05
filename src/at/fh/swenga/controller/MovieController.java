package at.fh.swenga.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.dao.GenreDao;
import at.fh.swenga.dao.MovieDao;
import at.fh.swenga.model.MovieModel;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;

@Controller
public class MovieController {
	
	private String apiKey = "12fdcdf9fd22633ac1feae68a09ab7e9"; //TODO: Key in apikey.properties like db (gitignore)
	
	private TmdbMovies movies = new TmdbApi(apiKey).getMovies();
	
	@Autowired
	MovieDao movieDao;
	
	@Autowired
	GenreDao genreDao;
	
	
	@RequestMapping(value = { "/", "list" })
	public String index(Model model)
	{	
		List<MovieModel> movies = movieDao.getMovies();
		model.addAttribute("movies", movies);

		return "index";
	}
	
	@RequestMapping(value = "/searchForMovies", method = RequestMethod.GET)
	public String search(Model model, @RequestParam String searchString)
	{
		model.addAttribute("movies", movieDao.searchMovies(searchString));
		return "forward:search";
  	}
	
	//Test for index.html
	@RequestMapping(value = "/getMyMovies", method = RequestMethod.GET)
	@Transactional
	public String testSearch(Model model, @RequestParam String searchString)
	{
		List<MovieModel> movies = movieDao.getMovies();
		
		model.addAttribute("movies", movies);
		return "forward:list";
  	}
	
	
	@RequestMapping(value = "/search")
	public String search(Model model)
	{
		return "search";
  	}
	
	@RequestMapping(value = "/save")
	public String save(@RequestParam("id") int id, Model model)
	{
		//TmdbMovies movies = new TmdbApi(apiKey).getMovies();
		
		MovieModel movie = movieDao.mapMovie(movies, id);

		try
		{
			movieDao.persist(movie);
		}
		catch (Exception ex) 	//FR: if duplicate PRIM Key -> DataIntegrityViolationException
		{ 
			System.out.println(ex); 
		} 
		
		return "forward:list";
  	}
	
	@RequestMapping(value = "/delete")
	@Transactional
	public String delete(@RequestParam("id") int id, Model model)
	{
		//TmdbMovies movies = new TmdbApi(apiKey).getMovies();
		
		MovieModel movie = movieDao.mapMovie(movies, id);

		try
		{
			MovieModel managedMovie = movieDao.merge(movie);
			movieDao.delete(managedMovie);
		}
		catch (Exception ex) 
		{ 
			System.out.println(ex); 
		}
		
		return "forward:list";
  	}
	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String handleLogin() 
	{
		return "login";
	}
	
	// @ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {

		return "error";

	}
}