package at.fh.swenga.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.*;


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
public class MovieList
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String name;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private User owner;
	
	@ManyToMany(mappedBy = "followingMovieLists")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<User> follower;
	
	@ManyToMany(mappedBy = "movieLists", fetch = FetchType.EAGER)
	public Set<MovieModel> moviesInList;
	
	@Version
	long version;

	public MovieList() {}

	public MovieList(String name, User owner)
	{
		super();
		this.name = name;
		this.owner = owner;
	}

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//owner
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}

	//follower
	public List<User> getFollower() {
		return follower;
	}
	public void setFollower(List<User> follower) {
		this.follower = follower;
	}

	//movies
	public Set<MovieModel> getMovies() {
		return moviesInList;
	}
	public void setMovies(Set<MovieModel> movies) {
		this.moviesInList = movies;
	}
	public void addMovie(MovieModel movie)
	{
		if (moviesInList == null) moviesInList = new HashSet<>();
		moviesInList.add(movie);
	}
	public void removeMovie(MovieModel movie) { if(moviesInList.contains(movie)) moviesInList.remove(movie); }

	@Override
	public boolean equals(Object other)
	{
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof MovieList))return false;
		MovieList otherList = (MovieList) other;
		return (this.id == otherList.getId());
	}

	@Override
	public int hashCode() { return Objects.hash(id, name); }
}
