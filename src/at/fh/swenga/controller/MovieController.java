package at.fh.swenga.controller;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
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
import at.fh.swenga.model.MovieModel;
import at.fh.swenga.model.User;
import at.fh.swenga.model.UserRole;
import at.fh.swenga.service.GetProperties;
//import at.fh.swenga.service.UserValidator;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;

@Controller
public class MovieController {
	
	GetProperties gp = new GetProperties();
	Properties properties = gp.getPropValues();

	private TmdbMovies movies = new TmdbApi(properties.getProperty("apiKey")).getMovies();

	@Autowired
	UserDao userDao;

	@Autowired
	MovieDao movieDao;

	@Autowired
	GenreDao genreDao;
	
	@Autowired
	MovieListDao movieListDao;

	// @Autowired
	// private UserValidator userValidator;

	@RequestMapping(value = { "/", "home" })
	public String index(Model model) {
		List<MovieModel> movies = movieDao.getMovies();
		model.addAttribute("movies", movies);

		return "index";
	}

	@RequestMapping(value = "/searchForMovies", method = RequestMethod.GET)
	public String search(Model model, @RequestParam String searchString) {
		model.addAttribute("movies", movieDao.searchMovies(searchString));
		return "forward:search";
	}
	
	@RequestMapping(value= "/myLists", method = RequestMethod.GET)
	public String showLists(Model model){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userDao.findUserByUsername(auth.getName());
		model.addAttribute("lists", movieListDao.getMovieList(user));
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
	public String save(@RequestParam("id") int id, Model model) {
		// TmdbMovies movies = new TmdbApi(apiKey).getMovies();

		MovieModel movie = movieDao.mapMovie(movies, id);

		try {
			movieDao.persist(movie);
		} catch (Exception ex) // FR: if duplicate PRIM Key ->
								// DataIntegrityViolationException
		{
			System.out.println(ex);
		}

		return "forward:list";
	}

	@RequestMapping(value = "/delete")
	@Transactional
	public String delete(@RequestParam("id") int id, Model model) {
		// TmdbMovies movies = new TmdbApi(apiKey).getMovies();

		MovieModel movie = movieDao.mapMovie(movies, id);

		try {
			MovieModel managedMovie = movieDao.merge(movie);
			movieDao.delete(managedMovie);
		} catch (Exception ex) {
			System.out.println(ex);
		}

		return "forward:home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String handleLogin() {
		return "login";
	}

	@RequestMapping(value = "/registerForm", method = RequestMethod.POST)
	public String registerForm(Model model) {
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

	// @ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {

		return "error";

	}
	
}