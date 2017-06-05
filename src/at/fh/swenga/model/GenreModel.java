package at.fh.swenga.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import info.movito.themoviedbapi.model.core.NamedIdElement;

@Entity
@Table(name = "Genre")
public class GenreModel extends NamedIdElement /*implements java.io.Serializable*/ {	

	private static final long serialVersionUID = 1L;

	@Id
	//@ManyToOne //OK?
	@Column(name = "id")	
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false, length = 30)
	private String name;
	
	@ManyToMany(mappedBy = "genres",fetch=FetchType.EAGER)
	private List<MovieModel> movies;
	
	public GenreModel() {}

	public GenreModel(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public List<MovieModel> getMovies() {
		return movies;
	}
 
	public void setMovies(List<MovieModel> movies) {
		this.movies = movies;
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
	
}
