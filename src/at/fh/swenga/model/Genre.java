package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Genre")
public class Genre //extends info.movito.themoviedbapi.model.Genre implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")	
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, length = 30)
	private String name;

	@ManyToMany(mappedBy = "genres", fetch = FetchType.EAGER)
	private Set<User> users;
	
	@ManyToMany(mappedBy = "genres",fetch=FetchType.EAGER)
	private List<MovieModel> movies;

	public Genre() {}

	public Genre(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//movies
	public List<MovieModel> getMovies() {
		return movies;
	}
	public void setMovies(List<MovieModel> movies) {
		this.movies = movies;
	}
	public void addMovie(MovieModel movie)
	{
		if (movies == null) movies = new ArrayList<MovieModel>();
		movies.add(movie);
	}

	//users
	public Set<User> getUsers() { return users; }
	public void setUsers(Set<User> users) { this.users = users; }
	public void addUser(User user)
	{
		if (users == null) users = new HashSet<User>();
		users.add(user);
	}

}
