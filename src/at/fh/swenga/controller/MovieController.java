package at.fh.swenga.controller;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import at.fh.swenga.dao.*;
import at.fh.swenga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.dao.GenreDao;
import at.fh.swenga.dao.MovieDao;
import at.fh.swenga.dao.MovieListDao;
import at.fh.swenga.dao.UserDao;
import at.fh.swenga.model.MovieList;
import at.fh.swenga.model.MovieModel;
import at.fh.swenga.model.User;
import at.fh.swenga.model.UserRole;
import at.fh.swenga.service.GetProperties;
//import at.fh.swenga.service.UserValidator;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MovieController
{
	@Autowired
	private UserDao userDao;

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private GenreDao genreDao;

	@Autowired
	private ActorDao actorDao;

	@Autowired
	private UserMovieDao userMovieDao;

	@Autowired
	private MovieListDao movieListDao;

	private TmdbMovies tmdbMovies = new TmdbApi(new GetProperties().getPropValues().getProperty("apiKey")).getMovies();

	private User activeUser;

	@RequestMapping(value = { "/", "home" })
	public String index(Model model)
	{
		activeUser = userDao.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

		List<MovieModel> userMovies = movieDao.getUserMovies(activeUser);

		model.addAttribute("movies", userMovies);

		return "index";
	}

	@RequestMapping(value = "/searchForMovies", method = RequestMethod.GET)
	public String search(Model model, @RequestParam String searchString) {
		model.addAttribute("movies", movieDao.searchMovies(searchString));
		return "forward:search";
	}
	
	@RequestMapping(value= "/list", method = RequestMethod.GET)
	public String showLists(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userDao.findByUsername(auth.getName());
		
		if(movieListDao.getMovieListByOwner(user) == null)
		{
			MovieList movieList = new MovieList("TestList", user);
			MovieModel movie1 = new MovieModel(1, 0, "test1", null, false, 0.0F, 0, new Date(), 0, 0, 0, "a", "a", "a");
			movieDao.merge(movie1);
			movieList.addMovie(movie1);
			MovieModel movie2 = new MovieModel(2, 2, "test2", null, false, 0.0F, 0, new Date(), 0, 0, 0, "a", "a", "a");
			movieDao.merge(movie2);
			movieList.addMovie(movie2);
			movieListDao.merge(movieList);
			user.addMovieList(movieList);
			userDao.merge(user);
		}
		
        MovieList movieListByOwner = movieListDao.getMovieListByOwner(user);
		movieListByOwner.getMovies();
		model.addAttribute("lists", movieListByOwner);
		//System.out.println("Felix");
		return "lists";
	}


	// Test for index.html
	@RequestMapping(value = "/getMyMovies", method = RequestMethod.GET)
	@Transactional
	public String testSearch(Model model, @RequestParam String searchString) {
		List<MovieModel> movies = movieDao.getMovies();
		model.addAttribute("movies", movies);
		return "forward:list";
	}

	@RequestMapping(value = "/search")
	public String search(Model model) {
		return "search";
	}

	@RequestMapping(value = "/save")
	public String save(@RequestParam("id") int id, Model model)
	{
		MovieModel movie = movieDao.mapMovie(tmdbMovies, id);

		if (userMovieDao.getUserMovieByID(activeUser, movie) != null)
			return "forward:home";

		if (movieDao.getMovieById(id) != null)
			movie = movieDao.merge(movie);

		UserMovie userMovie = new UserMovie(activeUser, movie);

		movie.addUserMovie(userMovie);

		for (Genre genre : movie.getGenres())
		{
			genre.addMovie(movie);

			try
			{
				genreDao.persist(genre);
			}
			catch (DataIntegrityViolationException ex)
			{
				genreDao.merge(genre);
				System.out.println("Genre " + genre.getName() + " already in DB");
			}
		}
		for (Actor actor : movie.getActors())
		{
			actor.addMovie(movie);

			try
			{
				actorDao.persist(actor);
			}
			catch (DataIntegrityViolationException ex)
			{
				actorDao.merge(actor);
				System.out.println("Actor " + actor.getName() + " already in DB");
			}
		}

		//Essential for getting attached entities stored in DB
		movie = movieDao.merge(movie);


		try { userMovieDao.persist(userMovie); }
		catch (DataIntegrityViolationException ex)
		{
			userMovieDao.merge(userMovie);
		}
		try { movieDao.persist(movie); }
		catch (DataIntegrityViolationException ex)
		{
			movieDao.merge(movie);
		}

		return "forward:home";
  	}

  	//TODO not working properly
	@RequestMapping(value = "/delete")
	//@Transactional
	public String delete(@RequestParam("id") int id, Model model)
	{
		userMovieDao.delete(userMovieDao.getUserMovieByID(activeUser, movieDao.mapMovie(tmdbMovies, id)));

		return "forward:home";
  	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String handleLogin()
	{
		return "login";
	}

	@RequestMapping(value = "/registerForm", method = RequestMethod.POST)
	public String registerForm(Model model)
	{
		User user = new User();
		model.addAttribute("user", user);
		return "register";
	}

	// TODO login unsafe as f**k
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@ModelAttribute(value = "user") User user) {
		// FR: NOOB check for credentials
		if (user.getUserName() != "" && user.getFirstName() != "" && user.getLastName() != ""
				&& user.getUserName() != "" && user.getEmail() != "" && user.getPassword() != "") {
			// FR: Password hashing
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(hashedPassword);
			UserRole userRole = new UserRole(user, "ROLE_USER");
			user.addUserRole(userRole);
			user.setEnabled(true);

			userDao.persist(user);
			userDao.persistRole(userRole);
			return "login";
		} else {
			System.out.println("NOPE");
			return "forward:registerForm";
		}
	}


	//TODO Genre not attached!!
	@RequestMapping(value = "/details")
	public String details(@RequestParam("id") int id, Model model)
	{
		MovieModel movie = movieDao.getMovieById(id);
		model.addAttribute("movie", movie);

		//System.out.println(id);

		return "details";
	}


	// @ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {

		return "error";

	}

}