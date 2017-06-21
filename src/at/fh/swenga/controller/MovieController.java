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

    /**
     * Saves all Genres from tmdb to DB if not already exists
     * Saves Admin and staff accounts to DB
     */
    @PostConstruct
    public void init()
    {
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
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            //ADMIN
            User admin = new User();
            admin.setUserName("admin");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setEmail("admin@oreely.at");
            admin.setEnabled(true);
            String hashedPassword = passwordEncoder.encode("password");
            admin.setPassword(hashedPassword);
            UserRole userRole = new UserRole(admin, "ROLE_USER");
            admin.addUserRole(userRole);
            UserRole adminRole = new UserRole(admin, "ROLE_ADMIN");
            admin.addUserRole(adminRole);
            userDao.persist(admin);
            userDao.persistRole(userRole);
            userDao.persistRole(adminRole);

            //LUKAS
            User lukas = new User();
            lukas.setUserName("lukas69");
            lukas.setFirstName("Lukas");
            lukas.setLastName("Schneider");
            lukas.setEmail("lukas69@oreely.at");
            lukas.setEnabled(true);
            String hashedPasswordLukas = passwordEncoder.encode("1234");
            lukas.setPassword(hashedPasswordLukas);
            UserRole userRoleLukas = new UserRole(lukas, "ROLE_USER");
            lukas.addUserRole(userRoleLukas);
            userDao.persist(lukas);
            userDao.persistRole(userRoleLukas);

            //MARKUS
            User markus = new User();
            markus.setUserName("wulf");
            markus.setFirstName("Markus");
            markus.setLastName("Wolf");
            markus.setEmail("markus@oreely.at");
            markus.setEnabled(true);
            String hashedPasswordMarkus = passwordEncoder.encode("1234");
            markus.setPassword(hashedPasswordMarkus);
            UserRole userRoleMarkus = new UserRole(markus, "ROLE_USER");
            markus.addUserRole(userRoleMarkus);
            userDao.persist(markus);
            userDao.persistRole(userRoleMarkus);

            //FELIX
            User felix = new User();
            felix.setUserName("flexboy");
            felix.setFirstName("Felix");
            felix.setLastName("Rauchenwald");
            felix.setEmail("flexboy@oreely.at");
            felix.setEnabled(true);
            String hashedPasswordFelix = passwordEncoder.encode("1234");
            felix.setPassword(hashedPasswordFelix);
            UserRole userRoleFelix = new UserRole(felix, "ROLE_USER");
            felix.addUserRole(userRoleFelix);
            userDao.persist(felix);
            userDao.persistRole(userRoleFelix);

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

            List<MovieModel> staffPicks = movieDao.getStaffPicks();
            
            MovieList staffPickList = new MovieList();
            movieListDao.persist(staffPickList);
            
            staffPickList.setName("staffPickList");
            staffPickList.setOwner(admin);
            staffPickList.setMovies(new HashSet(staffPicks));
            for (MovieModel movie : staffPicks)
            {
            	movie.addMovieList(staffPickList);
            	movieDao.merge(movie);
            }
            
            admin.addMovieList(staffPickList);
            movieListDao.merge(staffPickList);
            
            try{ userDao.merge(admin); }
            catch (DataIntegrityViolationException ex) { System.out.println("addmovieList"); }
        }
        else { System.out.println("admin found"); }
    }

    /**
     * Mapping for index.html
     * Adds Attributes to model for Recommendations, Trending Movies and Staff Picks
     * @param model
     * @return String to index.html
     */
    @RequestMapping(value = {"/", "home"})
    public String index(Model model)
    {
        List<Genre> userGenres = genreDao.getUserGenres(activeUser);
        List<MovieModel> recommendations = new ArrayList<>();

        if (userGenres.size() > 0)
        {
            recommendations.addAll(movieDao.searchForGenreRecommendations(userGenres.get(new Random().nextInt(userGenres.size())), 6));
            model.addAttribute("movies", recommendations);
        }

        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));
        model.addAttribute("trendingMovies", movieListDao.getMovieListByName("trendingMovieList").getMovies());
        model.addAttribute("staffMovies", movieListDao.getMovieListByName("staffPickList").getMovies());

        return "index";
    }

    /**
     * Mapping for search.html
     * Adds Attributes to model depending on selected option for search string
     * @param model
     * @param searchString  String user enters for search
     * @param selection     Selected Option for search, can be Movie, Users or Genres
     * @return  Forward String to search.html
     */
    @RequestMapping(value = "/searchForSelection", method = RequestMethod.GET)
    public String searchSelection(Model model, @RequestParam String searchString, @RequestParam(value = "selection") String selection)
    {
        if (selection.equals("Movies") && searchString != "") model.addAttribute("movies", movieDao.searchMovies(searchString));
        else if (selection.equals("Users")) model.addAttribute("users", userDao.searchUsers(searchString));
        else if (selection.equals("Genres")) model.addAttribute("movies", movieDao.searchForGenreRecommendations(genreDao.getGenre(searchString), 6));
        else return "forward:search";
        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));

        return "forward:search";
    }

    /**
     * Mapping for list.html
     * Adds Attributes for all user lists to model
     * @param model
     * @return  String to lists.html
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String showLists(Model model)
    {
        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));
        return "lists";
    }

    /**
     * Mapping for creation of new user movielist
     * @param name  Name for new movielist
     * @return  Redirect to /list mapping
     */
    @RequestMapping(value = "/addNewList", method = RequestMethod.GET)
    public String addNewList(@RequestParam("name") String name)
    {
        MovieList movieList = new MovieList();
        movieListDao.persist(movieList);

        movieList.setName(name);
        movieList.setOwner(activeUser);

        activeUser.addMovieList(movieList);
        movieListDao.merge(movieList);

        return "redirect:list";
    }

    /**
     * Mapping for deletion of user movielist
     * @param id    ID Attribute of list for search in DB
     * @return  Redirect to /list mapping
     */
    @RequestMapping(value = "/deleteList", method = RequestMethod.GET)
    public String deleteList(@RequestParam("id") int id)
    {
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

    /**
     * Mapping for search.html
     * @param model
     * @return  search.html
     */
    @RequestMapping(value = "/search")
    public String search(Model model) { return "search";}

    /**
     * Mapping settings.html
     * Adds list of user genres to model
     * Adds user object to model
     * @param model
     * @return
     */
    @RequestMapping(value = "/myProfile")
    public String myProfile(Model model)
    {
        List<Genre> genreList = genreDao.getGenres();

        model.addAttribute("genreList", genreList);
        model.addAttribute("user", activeUser);
        return "settings";
    }

    /**
     * Mapping for friends.html
     * Adds all movielists of friends to model
     * @param model
     * @return
     */
    @RequestMapping(value = "/friends")
    public String friends(Model model)
    {
        Set<User> friendList = activeUser.getFollowing();
        model.addAttribute("friendList", friendList);
        model.addAttribute("lists", movieListDao.getMovieListsByOwner(activeUser));

        return "friends";
    }

    /**
     * Mapping for adding a movie to a user movielist
     * @param id    ID of movie to add to list
     * @param listID    ID of list to add movie to
     * @return  Forward/Redirect to /list mapping
     */
    @RequestMapping(value = "/editList", params = "action=add")
    public String addToList(@RequestParam("movie_id") int id, @RequestParam(value = "listID", required = false) Integer listID)
    {
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

        //Add actors to DB
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

    /**
     * Mapping for removing a movie from user movielist
     * @param id    ID of movie to delete
     * @param listID    ID of list to remove movie from
     * @return  Forward/Redirect to /list mapping
     */
    @RequestMapping(value = "/editList", params = "action=remove")
    public String removeFromList(@RequestParam("movie_id") int id, @RequestParam(value = "listID", required = false) Integer listID)
    {
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

    /**
     * Mapping for following another user
     * @param userToFollowUserName  Name of user to follow
     * @return  Forward to friends.html
     */
    @RequestMapping(value = "/followUser")
    public String followUser(@RequestParam(value = "userToFollowUserName") String userToFollowUserName)
    {
        if (userToFollowUserName.equals(activeUser.getUserName()))
            return "forward:friends";

        User userToFollow = userDao.findByUsername(userToFollowUserName);

        if (activeUser.getFollowing().contains(userToFollow))
            return "forward:friends";

        activeUser.followUser(userToFollow);

        try { userDao.merge(userToFollow); }
        catch (DataIntegrityViolationException ex) { System.out.println("UserToFollow exeption!"); }

        try{ userDao.merge(activeUser); }
        catch (DataIntegrityViolationException ex) { System.out.println("ActiveUser exeption!");}

        return "redirect:friends";
    }

    /**
     * Mapping for unfollowing another user
     * @param userToUnFollowUserName    Name of user to unfollow
     * @return  Forward to friends.html
     */
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

    /**
     * Mapping to delete a UserMovie
     * @param id    ID of Movie to delete
     * @param model
     * @return  /home mapping
     */
    @RequestMapping(value = "/deleteUserMovie")
    public String delete(@RequestParam("id") int id, Model model)
    {
        System.out.println("DEBUG: /deleteUserMovie");

        userMovieDao.delete(userMovieDao.getUserMovie(activeUser, movieDao.mapMovie(tmdbMovies, id, false)));

        return "/home";
    }

    /**
     * Mapping to save user genre selection
     * @param checkedGenres list of checked genres from view
     * @return  Redirect to /home mapping
     */
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

    /**
     * Mapping to login.html for spring security
     * @return login.html
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String handleLogin() { return "login"; }

    /**
     * Mapping for initialisation of session
     * Saves successfully logged in user object to activeUser object for further use in session
     * @param userName  Name of user
     * @return  Forward to /home  mapping
     */
    @RequestMapping(value = "/initSession", method = RequestMethod.GET)
    public String initSession(@RequestParam(value = "username", required = false) String userName)
    {
        if (userName != null) activeUser = userDao.findByUsername(userName);

        activeUser = userDao.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return "forward:home";
    }

    /**
     * Mapping for register.html
     * Adds user genres to model
     * Adds logged in user object to model
     * @param user  User object to add to model
     * @param model
     * @return  register.html
     */
    @RequestMapping(value = "/registerForm", method = RequestMethod.POST)
    public String registerForm(@ModelAttribute(value = "user") User user, Model model)
    {
        if (user == null) user = new User();

        List<Genre> genreList = genreDao.getGenres();

        model.addAttribute("genreList", genreList);
        model.addAttribute("user", user);

        return "register";
    }

    /**
     * Mapping for registering a new user
     * Validates inputs like password matching, valid email
     * Creates hash for password to save to DB
     * @param model
     * @param user  User object to register
     * @param password  Typed password
     * @param confirmPassword   Matching to validate with password
     * @param checkedGenres List of genres user has checked
     * @param result
     * @return  Forward/Redirect to /registerForm mapping if validation unsuccessful else to login.html
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model,
                           @ModelAttribute(value = "user") @Valid User user,
                           @RequestParam("password") String password,
                           @RequestParam("password_confirmation") String confirmPassword,
                           @RequestParam(value = "checkGenre", required = false) List<String> checkedGenres,
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
        else { return "redirect:registerForm"; }
    }

    /**
     * Mapping for details.html
     * Adds all user movielists to model to edit lists
     * Adds movie object to model to display detail data
     * @param id    ID of movie to display
     * @param model
     * @return  details.html
     */
    @RequestMapping(value = "/details")
    public String details(@RequestParam("id") int id, Model model)
    {
        MovieModel movie;
        List<MovieList> userLists = movieListDao.getMovieListsByOwner(activeUser);

        //take data from DB if movie is already stored -> performance
        if (movieDao.getMovieById(id) != null) movie = movieDao.getMovieById(id);
        else { movie = movieDao.mapMovie(tmdbMovies, id, true); }

        model.addAttribute("lists", userLists);
        model.addAttribute("movie", movie);

        return "details";
    }

    /**
     * Mapping to trigger watched boolean in UserMovie
     * @param id    ID of movie to set watched/unwatched
     * @param model
     * @return  Redirect to /home mapping
     */
    @RequestMapping(value = "/watchedTrigger")
    public String watchedTrigger(@RequestParam("movie_id") int id,  Model model)
    {
        if (userMovieDao.getUserMovie(activeUser, movieDao.getMovieById(id)) == null)
        {
            MovieModel movie;

            if (movieDao.getMovieById(id) == null) movie = movieDao.mapMovie(tmdbMovies, id, true);
            else movie = movieDao.getMovieById(id);

            UserMovie userMovie = new UserMovie();//(activeUser, movie);
            userMovieDao.persist(userMovie);
            userMovie.setOwner(activeUser);
            userMovie.setMovie(movie);
            userMovie.setSeen(true);

            activeUser.addUserMovie(userMovie);

            try { userDao.merge(activeUser); }
            catch (DataIntegrityViolationException ex) { System.out.println("ActiveUser merge error!"); }

            try { userMovieDao.merge(userMovie); }
            catch (DataIntegrityViolationException ex) {System.out.println("UserMovieDao persist error!"); }

            try { userMovieDao.merge(userMovie); }
            catch (DataIntegrityViolationException ex) { System.out.println("UserMovieDao merge error!"); }

            return "redirect:home";
        }
        else
        {
            UserMovie userMovie = userMovieDao.getUserMovie(activeUser, movieDao.getMovieById(id));

            if (userMovie.isSeen()) userMovie.setSeen(false);
            else userMovie.setSeen(true);

            try { userMovieDao.merge(userMovie); }
            catch (DataIntegrityViolationException ex) { System.out.println("UserMovieDao error"); }

            return "redirect:home";
        }
    }

    /**
     * Mapping for legal information
     * @param model
     * @return  legal.html
     */
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