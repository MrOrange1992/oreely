package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import at.fh.swenga.model.Genre;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "Movie")
public class MovieModel //extends MovieDb implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private int tmdb_id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column
	@Type(type="text")
	private String overview;

	@Column(nullable = false)
	private boolean adult;

	@Column
	private float vote_average;

	@Column
	private int vote_count;

	@Temporal(TemporalType.DATE)
	private Date release_date;

	@Column
	private int runtime;

	@Column
	private long budget;

	@Column
	private long revenue;

	@Column(nullable = true, length = 200)
	private String poster_path;

	/* Deprecated
	@Column(nullable = true, length = 30)
	private String original_language;
	*/

	@Column(nullable = true, length = 100)
	private String original_name;

	@Column(nullable = true, length = 200)
	private String homepage;
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<Genre> genres;

	@ManyToMany(cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Actor> actors;
	
	@OneToMany(mappedBy="movie",fetch=FetchType.EAGER)
    private Set<UserMovie> userMovies;
	
	@ManyToMany(cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	private Set<MovieList> movieLists; //was List -> ERROR

	
	// TODO: Relationships to list_movie, user_movie & movie_actor

	public MovieModel() {}

	public MovieModel(int id, int tmdb_id, String title, String overview, boolean adult, float vote_average, int vote_count,
					  Date release_date, int runtime, long budget, long revenue, String poster_path,
					  String original_name, String homepage) {
		super();
		this.id = id;
		this.tmdb_id = tmdb_id;
		this.title = title;
		this.overview = overview;
		this.adult = adult;
		this.vote_average = vote_average;
		this.vote_count = vote_count;
		this.release_date = release_date;
		this.runtime = runtime;
		this.budget = budget;
		this.revenue = revenue;
		this.poster_path = poster_path;
		this.original_name = original_name;
		this.homepage = homepage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTmdb_id() {
		return tmdb_id;
	}

	public void setTmdb_id(int tmdb_id) {
		this.tmdb_id = tmdb_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOverview() { return overview; }

	public void setOverview(String overview) { this.overview = overview; }

	public boolean isAdult() {
		return adult;
	}

	public void setAdult(boolean adult) {
		this.adult = adult;
	}

	public float getVote_average() {
		return vote_average;
	}

	public void setVote_average(float vote_average) {
		this.vote_average = vote_average;
	}

	public int getVote_count() {
		return vote_count;
	}

	public void setVote_count(int vote_count) {
		this.vote_count = vote_count;
	}

	public Date getRelease_date() {
		return release_date;
	}

	public void setRelease_date(Date date) {
		this.release_date = date;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public long getBudget() {
		return budget;
	}

	public void setBudget(long l) {
		this.budget = l;
	}

	public long getRevenue() {
		return revenue;
	}

	public void setRevenue(long l) {
		this.revenue = l;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getOriginal_name() {
		return original_name;
	}

	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	
	//user movies
	public Set<UserMovie> getUserMovies() { return userMovies; }
	public void setUserMovies(Set<UserMovie> userMovies) { this.userMovies = userMovies; }
	public void addUserMovie(UserMovie userMovie){
		if (userMovies == null) userMovies = new HashSet<UserMovie>();
		userMovies.add(userMovie);
	}
	public void removeUserMovie(UserMovie userMovie) 
	{
		if(userMovies.contains(userMovie)) userMovies.remove(userMovie);
	}

	//movie list
	public Set<MovieList> getMovieLists() {
		return movieLists;
	}
	public void setMovieLists(Set<MovieList> movieLists) {
		this.movieLists = movieLists;
	}
	public void addMovieList(MovieList movieList) {
		if (movieLists == null) movieLists = new HashSet<MovieList>();
		movieLists.add(movieList);
	}

	//genres
	public List<Genre> getGenres() {
		return this.genres;
	}
	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}
	public void addGenre(Genre genre) {
		if (genres== null) genres= new ArrayList<Genre>();
		genres.add(genre);
	}

	//actors
	public Set<Actor> getActors() {
		return actors;
	}
	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}
	public void addActor(Actor actor) {
		if(actors == null) actors = new HashSet<Actor>();
		actors.add(actor);
	}

}
