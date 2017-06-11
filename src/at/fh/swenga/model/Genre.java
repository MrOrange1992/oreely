package at.fh.swenga.model;

import java.util.List;

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

	//@ManyToMany(mappedBy = "genres", fetch = FetchType.EAGER)
	//private List<User> users;
	
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

	public List<MovieModel> getMovies() {
		return movies;
	}
 
	public void setMovies(List<MovieModel> movies) {
		this.movies = movies;
	}
	
	/*public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}*/

}
