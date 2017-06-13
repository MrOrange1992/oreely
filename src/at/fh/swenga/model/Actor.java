package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "Actor")
public class Actor {

	@Id
	@Column
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String name;		//not first and last because PersonPeople object from TMDB has only one name attribute
	
	@Column
	private String birthday;
	
	//@ManyToMany(cascade = CascadeType.PERSIST)
	@ManyToMany(mappedBy = "actors",fetch=FetchType.EAGER)
	private List<MovieModel> movies;
	
	public Actor() {}

	public Actor(int id, String name, String birthday) {
		super();
		this.id = id;
		this.name = name;
		this.birthday = birthday;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	public List<MovieModel> getMovies() { return movies; }
	public void setMovies(List<MovieModel> movies) { this.movies = movies; }
	public void addMovie(MovieModel movie) 
	{
		if (movies == null) movies = new ArrayList<MovieModel>();
		movies.add(movie);
	}
	public void removeMovie(MovieModel movie) 
	{
		if(movies.contains(movie)) movies.remove(movie);
	}	
	
}
