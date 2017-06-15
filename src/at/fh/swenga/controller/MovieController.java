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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class MovieController
{
	@Autowired private UserDao userDao;

	@Autowired private MovieDao movieDao;

	@Autowired private GenreDao genreDao;

	@Autowired private ActorDao actorDao;

	@Autowired private UserMovieDao userMovieDao;

	@Autowired private MovieListDao movieListDao;

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
		/*
		if(movieListDao.getMovieListByOwner(activeUser) == null)
		{
			List<MovieModel> allMovies = movieDao.getMovies();

			MovieList movieList = new MovieList();

			//movieList = movieListDao.merge(movieList);

			movieListDao.persist(movieList);

			for (MovieModel movie : allMovies)
			{
				movie.addMovieList(movieList);
				movie = movieDao.merge(movie);
				movieList.addMovie(movie);
			}

			//movieList = movieListDao.merge(movieList);
			//activeUser = userDao.merge(activeUser);

			movieList.setName("TestList1");
			movieList.setOwner(activeUser);
			activeUser.addMovieList(movieList);
			movieList = movieListDao.merge(movieList);

			userDao.merge(activeUser);
		}
		*/
		
        List<MovieList> movieListsByOwner = movieListDao.getMovieListsByOwner(activeUser);
		model.addAttribute("movieLists", movieListsByOwner);

		return "lists";
	}

	@RequestMapping(value = "/addNewList", method = RequestMethod.GET)
	public String addNewList(@RequestParam("name") String name)
	{
		MovieList movieList = new MovieList();

		movieListDao.persist(movieList);

		movieList.setName(name);
		movieList.setOwner(activeUser);
		activeUser.addMovieList(movieList);
		movieListDao.merge(movieList);

		userDao.merge(activeUser);

		return "forward:list";
	}

	@RequestMapping(value = "/deleteList", method = RequestMethod.GET)
	public String deleteList(@RequestParam("id") int id)
	{
		MovieList movieList = movieListDao.getMovieListByID(id);

		movieList = movieListDao.merge(movieList);

		movieListDao.delete(movieList);

		return "forward:list";
	}

	@RequestMapping(value = "/search")
	public String search(Model model) {
		return "search";
	}


	@RequestMapping(value = "/save")
	public String save(@RequestParam("id") int id, Model model)
	{
		MovieModel movie = movieDao.mapMovie(tmdbMovies, id, true);

		//TODO maybe check with Thymeleaf if movie has already been added and if so don't display the add button
		//Do nothing if movie has already been added
		if (userMovieDao.getUserMovieByID(activeUser, movie) != null)
			return "forward:home";

		//Bring movie to actual context with merge if already stored in DB
		if (movieDao.getMovieById(id) != null) movie = movieDao.merge(movie);

		UserMovie userMovie = new UserMovie(activeUser, movie);

		movie.addUserMovie(userMovie);

		//Add genres to movie/DB
		for (Genre genre : movie.getGenres())
		{
			genre.addMovie(movie);

			if (genreDao.getGenre(genre.getName()) == null)
			{
				//Try persisting -> performance, if already in DB DataIntegrityViolationException is thrown
				try { genreDao.persist(genre); }
				catch (DataIntegrityViolationException ex) { System.out.println("Genre " + genre.getName() + " already in DB"); }
			}
		}

		//Add actors to movie/DB
		for (Actor actor : movie.getActors())
		{
			actor.addMovie(movie);

			if (actorDao.getActorByName(actor.getName()) == null)
			{
				try { actorDao.persist(actor); }
				catch (DataIntegrityViolationException ex) { System.out.println("Actor " + actor.getName() + " already in DB"); }
			}
		}

		//Essential for getting attached entities stored in DB
		movie = movieDao.merge(movie);

		//try persisting for performance
		try { userMovieDao.persist(userMovie); }
		catch (DataIntegrityViolationException ex) { userMovieDao.merge(userMovie); }

		try { movieDao.persist(movie); }
		catch (DataIntegrityViolationException ex) { movieDao.merge(movie); }

		return "forward:home";
  	}

  	//DONE
	@RequestMapping(value = "/delete")
	public String delete(@RequestParam("id") int id, Model model)
	{
		userMovieDao.delete(userMovieDao.getUserMovieByID(activeUser, movieDao.mapMovie(tmdbMovies, id, false)));

		return "forward:home";
  	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String handleLogin()
	{
		return "login";
	}

	@RequestMapping(value = "/registerForm", method = RequestMethod.POST)
	public String registerForm(@ModelAttribute(value="user") User user,  Model model)
	{
		if (user == null) user = new User();

		model.addAttribute("user", user);
		return "register";
	}

	// TODO Genres from checkboxes into User_Genre
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(Model model,
						   @ModelAttribute(value = "user") @Valid User user,
						   @RequestParam("password") String password,
						   @RequestParam("password_confirmation") String confirmPassword,
						   BindingResult result)
	{
		if (!result.hasErrors())
		{
			//Check if username is already in DB (must be unique!)
			if (userDao.findByUsername(user.getUserName()) != null)
			{
				model.addAttribute("credentialError", "Username already exists!");
				model.addAttribute("user", user);
				return "forward:registerForm";
			}

			//check if password fields match
			if (!password.equals(confirmPassword))
			{
				model.addAttribute("credentialError", "Passwords do not match!");
				model.addAttribute("user", user);
				return "forward:registerForm";
			}

			//password hashing
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(hashedPassword);
			UserRole userRole = new UserRole(user, "ROLE_USER");
			user.addUserRole(userRole);
			user.setEnabled(true);

			userDao.persist(user);
			userDao.persistRole(userRole);
			return "login";
		}
		else { return "forward:registerForm"; }
	}


	@RequestMapping(value = "/details")
	public String details(@RequestParam("id") int id, Model model)
	{
		MovieModel movie;

		//take data from DB if movie is already stored -> performance
		if(movieDao.getMovieById(id) != null) movie = movieDao.getMovieById(id);
		else { movie = movieDao.mapMovie(tmdbMovies, id, true); }

		model.addAttribute("movie", movie);

		return "details";
	}


	// @ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) { return "error"; }

}