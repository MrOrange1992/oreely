package at.fh.swenga.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import at.fh.swenga.dao.*;
import at.fh.swenga.model.*;
import at.fh.swenga.model.Genre;
import info.movito.themoviedbapi.TmdbGenre;
import info.movito.themoviedbapi.model.*;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
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

import javax.annotation.PostConstruct;
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

	private String apiKey = new GetProperties().getPropValues().getProperty("apiKey");

	private TmdbMovies tmdbMovies = new TmdbApi(apiKey).getMovies();

	private User activeUser;

	@PostConstruct
	public void init()
	{
		System.out.println("DEBUG: /init (PostConstruct)");

		if (genreDao.getGenres().size() == 0)
		{
			//save all Genres to DB if not already in Db
			TmdbGenre tmdbGenre = new TmdbApi(apiKey).getGenre();

			List<info.movito.themoviedbapi.model.Genre> tmdbGenres = tmdbGenre.getGenreList("en");

			for (info.movito.themoviedbapi.model.Genre tmDBgenre : tmdbGenres)
			{
				if (genreDao.getGenre(tmDBgenre.getName()) == null)
				{
					Genre genre = new Genre(tmDBgenre.getId(), tmDBgenre.getName());
					genreDao.persist(genre);
				}
			}
		}
	}



	@RequestMapping(value = { "/", "home" })
	public String index(Model model)
	{
		System.out.println("DEBUG: /home");

		//List<MovieModel> userMovies = movieDao.getUserMovies(activeUser);
		//model.addAttribute("movies", userMovies);

		List<Genre> userGenres = genreDao.getUserGenres(activeUser);

		List<MovieModel> recommendations = new ArrayList<>();


		for (Genre genre : userGenres)
		{
			recommendations.addAll(movieDao.searchForGenreRecommendations(genre, 5));
		}

		model.addAttribute("movies", recommendations);

		return "index";
	}

	@RequestMapping(value = "/searchForMovies", method = RequestMethod.GET)
	public String search(Model model, @RequestParam String searchString)
	{
		System.out.println("DEBUG: /searchForMovies");

		model.addAttribute("movies", movieDao.searchMovies(searchString));
		return "forward:search";
	}

	// @RequestMapping(value = "/searchForMovies", method = RequestMethod.GET)
	// public String search(Model model, @RequestParam String searchString,
	// @RequestParam int page) {
	// model.addAttribute("movies", movieDao.searchMovies(searchString, page));
	// return "forward:search";
	// }


	@RequestMapping(value= "/list", method = RequestMethod.GET)
	public String showLists(Model model)
	{
		System.out.println("DEBUG: /list");


		List<MovieList> movieListsByOwner = movieListDao.getMovieListsByOwner(activeUser);
		model.addAttribute("movieLists", movieListsByOwner);

		return "lists";
	}

	@RequestMapping(value = "/addNewList", method = RequestMethod.GET)
	public String addNewList(@RequestParam("name") String name)
	{
		System.out.println("DEBUG: /addNewList");

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
		System.out.println("DEBUG: /deleteList");

		MovieList movieList = movieListDao.getMovieListByID(id);

		movieList = movieListDao.merge(movieList);

		movieListDao.delete(movieList);

		return "forward:list";
	}

	@RequestMapping(value = "/search")
	public String search(Model model)
	{
		System.out.println("DEBUG: /search");

		return "search";
	}

	@RequestMapping(value = "/myProfile")
	public String myProfile(Model model)
	{
		System.out.println("DEBUG: /myProfile");

		List<Genre> genreList = genreDao.getGenres();

		model.addAttribute("genreList", genreList);

		return "settings";
	}

	@RequestMapping(value = "/save")
	public String save(@RequestParam("id") int id, Model model)
	{
		System.out.println("DEBUG: /save");

		MovieModel movie = movieDao.mapMovie(tmdbMovies, id, true);

		// TODO maybe check with Thymeleaf if movie has already been added and
		// if so don't display the add button
		// Do nothing if movie has already been added
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
        try //
        // Add actors to movie/DB
        {
            for (Actor actor : movie.getActors()) {
                actor.addMovie(movie);

                if (actorDao.getActorByName(actor.getName()) == null) {
                    try {
                        actorDao.persist(actor);
                    } catch (DataIntegrityViolationException ex) {
                        System.out.println("Actor " + actor.getName() + " already in DB");
                    }
                    if (actorDao.getActorByName(actor.getName()) == null) {
                        try {
                            actorDao.persist(actor);
                        } catch (DataIntegrityViolationException ex) {
                            // actorDao.merge(actor);
                            System.out.println("Actor " + actor.getName() + " already in DB");
                        }
                    }
                }
            }
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("No Actors for this movie");
        }

		// Essential for getting attached entities stored in DB
		movie = movieDao.merge(movie);

		// try persisting for performance
		try {
			userMovieDao.persist(userMovie);
		} catch (DataIntegrityViolationException ex) {
			userMovieDao.merge(userMovie);
		}

		try {
			movieDao.persist(movie);
		} catch (DataIntegrityViolationException ex) {
			movieDao.merge(movie);
		}

		return "forward:home";
  	}

  	//DONE
	@RequestMapping(value = "/delete")
	public String delete(@RequestParam("id") int id, Model model)
	{
		System.out.println("DEBUG: /delete");

		userMovieDao.delete(userMovieDao.getUserMovieByID(activeUser, movieDao.mapMovie(tmdbMovies, id, false)));

		return "forward:home";
  	}

  	@RequestMapping(value = "/saveSettings", method = RequestMethod.POST)
	public String saveSettings(@RequestParam(value = "checkGenre", required = false) List<String> checkedGenres)
	{
		System.out.println("DEBUG: /saveSettings");

		userDao.removeGenres(activeUser);

		if (checkedGenres != null)
		{
			for (String checkedGenre : checkedGenres)
			{
				Genre genre = genreDao.getGenre(checkedGenre);
				activeUser.addGenres(genre);
				genreDao.merge(genre);
				userDao.merge(activeUser);
			}
		}

		return "forward:home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String handleLogin(@RequestParam(value = "username", required = false) String userName)
	{
		//activeUser = userDao.findByUsername(userName);

		System.out.println("DEBUG: /login");
		return "login";
		//return "forward:initSession";
	}

	@RequestMapping(value = "/initSession")
	public String initSession()
	{
		System.out.println("DEBUG: /initSession");

		activeUser = userDao.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

		return "forward:home";
	}

	@RequestMapping(value = "/registerForm", method = RequestMethod.POST)
	public String registerForm(@ModelAttribute(value="user") User user,  Model model)
	{
		System.out.println("DEBUG: /registerForm");

		if (user == null) user = new User();

		List<Genre> genreList = genreDao.getGenres();

		model.addAttribute("genreList", genreList);

		model.addAttribute("user", user);
		return "register";
	}

	// TODO Genres from checkboxes into User_Genre
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(Model model,
						   @ModelAttribute(value = "user") @Valid User user,
						   @RequestParam("password") String password,
						   @RequestParam("password_confirmation") String confirmPassword,
						   @RequestParam(value = "checkGenre", required = false) List<String> checkedGenres,
						   BindingResult result)
	{
		System.out.println("DEBUG: /register");

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

			if (checkedGenres != null)
			{
				for (String checkedGenre : checkedGenres)
				{
					Genre genre = genreDao.getGenre(checkedGenre);
					user.addGenres(genre);
					genre.addUser(user);
					genreDao.merge(genre);
				}

				userDao.merge(user);
			}

			return "login";
		}
		else { return "forward:registerForm"; }
	}


	@RequestMapping(value = "/details")
	public String details(@RequestParam("id") int id, Model model)
	{
		System.out.println("DEBUG: /details");

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