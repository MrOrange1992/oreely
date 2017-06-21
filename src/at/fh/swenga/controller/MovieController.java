package at.fh.swenga.controller;

import java.util.*;

import at.fh.swenga.dao.*;
import at.fh.swenga.model.*;
import at.fh.swenga.model.Genre;
import at.fh.swenga.model.MovieList;
import info.movito.themoviedbapi.TmdbGenre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.xml.crypto.Data;

@Controller
public class MovieController
{
    @Autowired
    private UserDao userDao;

    //@Autowired
    //private FollowerDao followerDao;

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

        if(userDao.findByUsername("admin") == null)
        {
            System.out.println("DEBUG: no admin found");

            //Calendar bd = new GregorianCalendar(1970,01,01);

            User admin = new User();
            admin.setUserName("admin");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setEmail("admin@oreely.at");
            admin.setEnabled(true);

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode("password");
            admin.setPassword(hashedPassword);

            UserRole userRole = new UserRole(admin, "ROLE_USER");
            admin.addUserRole(userRole);
            UserRole adminRole = new UserRole(admin, "ROLE_ADMIN");
            admin.addUserRole(adminRole);

            userDao.persist(admin);
            userDao.persistRole(userRole);
            userDao.persistRole(adminRole);

            System.out.println("DEBUG: admin created");

            Set<MovieModel> trendingMovies = movieDao.getTrendingMovies();

            MovieList trendingMovieList = new MovieList();
            movieListDao.persist(trendingMovieList);

            trendingMovieList.setName("trendingMovieList");
            trendingMovieList.setOwner(admin);
            trendingMovieList.setMovies(trendingMovies);
            for (MovieModel movie : trendingMovies)
            {
                movie.addMovieList(trendingMovieList);
                movieDao.merge(movie);
            }

            admin.addMovieList(trendingMovieList);
            movieListDao.merge(trendingMovieList);

            try{ userDao.merge(admin); }
            catch (DataIntegrityViolationException ex) { System.out.println("addmovieList"); }

            /*
            System.out.println("DEBUG: trending MovieList created");
            
            List<MovieModel> staffPicks = movieDao.getStaffPicks();
            MovieList staffPickList = new MovieList("staffPickList", admin);
            staffPickList.setMovies(new HashSet(staffPicks));
            movieListDao.merge(staffPickList);
                     
            System.out.println("DEBUG: staff picks MovieList created");
            */
        }
        else { System.out.println("DEBUG: admin found"); }
    }


    @RequestMapping(value = {"/", "home"})
    public String index(Model model)
    {
        System.out.println("DEBUG: /home");

        List<Genre> userGenres = genreDao.getUserGenres(activeUser);
        List<MovieModel> recommendations = new ArrayList<>();

        if (userGenres.size() > 0)
        {
            recommendations.addAll(movieDao.searchForGenreRecommendations(userGenres.get(new Random().nextInt(userGenres.size())), 6));
            model.addAttribute("movies", recommendations);
        }

        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));

        return "index";
    }

    @RequestMapping(value = "/searchForSelection", method = RequestMethod.GET)
    public String searchSelection(Model model, @RequestParam String searchString, @RequestParam(value = "selection") String selection)
    {
        System.out.println("DEBUG: /searchForMovies");

        if (selection.equals("Movies")) model.addAttribute("movies", movieDao.searchMovies(searchString));
        else if (selection.equals("Users")) model.addAttribute("users", userDao.searchUsers(searchString));
        else model.addAttribute("movies", movieDao.searchForGenreRecommendations(genreDao.getGenre(searchString), 6));

        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));

        return "forward:search";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String showLists(Model model)
    {
        System.out.println("DEBUG: /list");

        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));

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

        return "redirect:list";
    }

    @RequestMapping(value = "/deleteList", method = RequestMethod.GET)
    public String deleteList(@RequestParam("id") int id)
    {
        System.out.println("DEBUG: /deleteList");

        MovieList movieList = movieListDao.getMovieListByID(id);

        for (MovieModel movie : movieList.getMovies())
        {
            movie.removeMovieList(movieList);
            movieDao.merge(movie);
        }

        movieList.setMovies(new HashSet<>());
        movieList = movieListDao.merge(movieList);

        activeUser.removeMovieList(movieList);
        movieListDao.delete(movieList);

        return "redirect:list";
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
        model.addAttribute("user", activeUser);
        return "settings";
    }

    @RequestMapping(value = "/friends")
    public String friends(Model model)
    {
        System.out.println("DEBUG: /friends");

        Set<User> friendList = activeUser.getFollowing();

        model.addAttribute("friendList", friendList);

        return "friends";
    }

    @RequestMapping(value = "/editList", params = "action=add")
    public String addToList(@RequestParam("movie_id") int id, @RequestParam(value = "listID", required = false) Integer listID)
    {
        System.out.println("DEBUG: /editList/add");

        //if user has no lists
        if (listID == null) return "forward:list";

        MovieModel movie;

        if (movieDao.getMovieById(id) == null) movie = movieDao.mapMovie(tmdbMovies, id, true);
        else movie = movieDao.getMovieById(id);

        MovieList movieList = movieListDao.getMovieListByID(listID);

        if (userMovieDao.getUserMovie(activeUser, movie) != null)
        {
            if (movieListDao.isMovieInList(movie, listID)) return "forward:list";
            else
            {
                movie = movieDao.getMovieById(id);
                movieList.addMovie(movie);
                movie.addMovieList(movieList);

                movieDao.merge(movie);
                movieListDao.merge(movieList);

                return "redirect:list";
            }
        }

        UserMovie userMovie = new UserMovie(activeUser, movie);

        movie.addUserMovie(userMovie);

        //Add genres to movie/DB
        for (Genre genre : movie.getGenres()) { genre.addMovie(movie); }

        for (Actor actor : movie.getActors())
        {
            if (actorDao.getActorByName(actor.getName()) == null)
            {
                try { actorDao.persist(actor); }
                catch (DataIntegrityViolationException ex) { System.out.println("Actor " + actor.getName() + " already in DB"); }
                actor.addMovie(movie);
            }
        }

        movie.addMovieList(movieList);
        movieList.addMovie(movie);

        // Essential for getting attached entities stored in DB
        movieDao.merge(movie);
        userMovieDao.merge(userMovie);
        movieListDao.merge(movieList);

        return "redirect:list";
    }


    @RequestMapping(value = "/editList", params = "action=remove")
    public String removeFromList(@RequestParam("movie_id") int id, @RequestParam(value = "listID", required = false) Integer listID)
    {
        System.out.println("DEBUG: /editList/remove");

        if (listID == null) return "redirect:list";

        MovieModel movie;

        if (movieDao.getMovieById(id) == null) movie = movieDao.mapMovie(tmdbMovies, id, true);
        else movie = movieDao.getMovieById(id);

        MovieList movieList = movieListDao.getMovieListByID(listID);

        movieList.removeMovie(movie);

        movie.removeMovieList(movieList);

        movieDao.merge(movie);
        movieListDao.merge(movieList);

        return "forward:list";
    }

    @RequestMapping(value = "/followUser")
    public String followUser(@RequestParam(value = "userToFollowUserName") String userToFollowUserName)
    {
        if (userToFollowUserName.equals(activeUser.getUserName()))
            return "forward:friends";

        User userToFollow = userDao.findByUsername(userToFollowUserName);

        if (activeUser.getFollowing().contains(userToFollow))
            return "forward:friends";


        activeUser.followUser(userToFollow);
        //userToFollow.beFollowedBy(activeUser);

        try { userDao.merge(userToFollow); }
        catch (DataIntegrityViolationException ex) { System.out.println("UserToFollow exeption!"); }

        try{ userDao.merge(activeUser); }
        catch (DataIntegrityViolationException ex) { System.out.println("ActiveUser exeption!");}

        return "redirect:friends";

    }

    @RequestMapping(value = "/unfollowUser")
    public String unfollowUser(@RequestParam(value = "userToUnFollowUserName") String userToUnFollowUserName)
    {
        if (userToUnFollowUserName.equals(activeUser.getUserName()))
            return "forward:friends";

        User userToUnFollow = userDao.findByUsername(userToUnFollowUserName);

        if (!activeUser.getFollowing().contains(userToUnFollow))
            return "forward:friends";

        userToUnFollow.getFollowing().remove(activeUser);

        activeUser.unfollowUser(userToUnFollow);

        try{ userDao.merge(activeUser); }
        catch (DataIntegrityViolationException ex) { System.out.println("ActiveUser exeption!");}

        return "forward:friends";
    }

    //DONE
    @RequestMapping(value = "/deleteUserMovie")
    public String delete(@RequestParam("id") int id, Model model)
    {
        System.out.println("DEBUG: /deleteUserMovie");

        userMovieDao.delete(userMovieDao.getUserMovie(activeUser, movieDao.mapMovie(tmdbMovies, id, false)));

        return "/home";
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

        return "redirect:home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String handleLogin(@RequestParam(value = "username", required = false) String userName)
    {
        System.out.println("DEBUG: /login");

        return "login";
    }

    @RequestMapping(value = "/initSession", method = RequestMethod.GET)
    public String initSession(@RequestParam(value = "username", required = false) String userName)
    {
        System.out.println("DEBUG: /initSession");

        if (userName != null)
            activeUser = userDao.findByUsername(userName);

        activeUser = userDao.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return "forward:home";
    }

    @RequestMapping(value = "/registerForm", method = RequestMethod.POST)
    public String registerForm(@ModelAttribute(value = "user") User user, Model model)
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
        List<MovieList> userLists = movieListDao.getMovieListsByOwner(activeUser);

        //take data from DB if movie is already stored -> performance
        if (movieDao.getMovieById(id) != null) movie = movieDao.getMovieById(id);
        else { movie = movieDao.mapMovie(tmdbMovies, id, true); }

        model.addAttribute("lists", userLists);
        model.addAttribute("movie", movie);

        return "details";
    }
    
    @RequestMapping(value = "/watchedTrigger")
    public String watchedTrigger(@RequestParam("movie_id") int id,  Model model)
    {
        System.out.println("DEBUG: /watchedTrigger");

        if (userMovieDao.getUserMovie(activeUser, movieDao.getMovieById(id)) == null)
        {
            MovieModel movie;

            if (movieDao.getMovieById(id) == null)
                movie = movieDao.mapMovie(tmdbMovies, id, true);
            else movie = movieDao.getMovieById(id);

            UserMovie userMovie = new UserMovie(activeUser, movie);

            try { userMovieDao.persist(userMovie); }
            catch (DataIntegrityViolationException ex) { System.out.println("UserMovieDao persist error!"); }

            userMovie.setSeen(true);

            try { userMovieDao.merge(userMovie); }
            catch (DataIntegrityViolationException ex) { System.out.println("UserMovieDao merge error!"); }

            return "redirect:home";

        }
        else
        {

            UserMovie userMovie = userMovieDao.getUserMovie(activeUser, movieDao.getMovieById(id));

            if (userMovie.isSeen())
                userMovie.setSeen(false);
            else userMovie.setSeen(true);

            try { userMovieDao.merge(userMovie); }
            catch (DataIntegrityViolationException ex) { System.out.println("UserMovieDao error"); }

            return "redirect:home";
        }
    }

    @RequestMapping(value = "/legal")
    public String legalinformation(Model model)
    {
        return "legal";
    }


    // @ExceptionHandler(Exception.class)
    public String handleAllException(Exception ex)
    {
        return "error";
    }

}