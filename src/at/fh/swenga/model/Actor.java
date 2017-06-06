package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Actor")
public class Actor {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String firstName;
	
	@Column
	private String lastName;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Calendar birthday;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<MovieModel> movies;
	
	public Actor() {}

	public Actor(String firstName, String lastName, Calendar birthday) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setBirthday(Calendar birthday) {
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
