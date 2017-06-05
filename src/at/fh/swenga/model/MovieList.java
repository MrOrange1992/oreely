package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class MovieList {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String name;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	User owner;
	
	@ManyToMany(mappedBy = "followingMovieLists",fetch=FetchType.EAGER)
	private List<User> follower;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<MovieModel> movies;
	
	@Version
	long version;

	public MovieList() {
	}	

	public MovieList(String name, User owner) {
		super();
		this.name = name;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<User> getFollower() {
		return follower;
	}

	public void setFollower(List<User> follower) {
		this.follower = follower;
	}

	public List<MovieModel> getMovies() {
		return movies;
	}

	public void setMovies(List<MovieModel> movies) {
		this.movies = movies;
	}		
	
	public void addMovie(MovieModel movie){
		if (movies == null) movies = new ArrayList<MovieModel>();
		movies.add(movie);
	}
	public void removeMovie(MovieModel movie) 
	{
		if(movies.contains(movie)) movies.remove(movie);
	}
	
}
