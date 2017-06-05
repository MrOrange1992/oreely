package at.fh.swenga.model;

import java.util.ArrayList;
import java.util.Date;
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

import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;

@Entity
@Table(name = "Movie")
public class MovieModel extends MovieDb implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private int tmdb_id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private boolean adult;

	@Column
	private float vote_average;

	@Column
	private int vote_count;

	@Temporal(TemporalType.DATE)
	private Date release_date;		//FR: Change Date to Calendar? 

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
	
	@ManyToMany(cascade=CascadeType.PERSIST)  //MERGE?
	private List<GenreModel> genres;
	
	// TODO: Relationships to list_movie, user_movie & movie_actor

	public MovieModel() {}
	
	public MovieModel(int id, int tmdb_id, String title, boolean adult, float vote_average, int vote_count,
			Date release_date, int runtime, long budget, long revenue, String poster_path, String original_language,
			String original_name, String homepage) {
		super();
		this.id = id;
		this.tmdb_id = tmdb_id;
		this.title = title;
		this.adult = adult;
		this.vote_average = vote_average;
		this.vote_count = vote_count;
		this.release_date = release_date;
		this.runtime = runtime;
		this.budget = budget;
		this.revenue = revenue;
		this.poster_path = poster_path;
		//this.original_language = original_language;
		this.original_name = original_name;
		this.homepage = homepage;
	}
	
	//FR: using getModelGenres because getGenres is already implemented in super() MovieDb
	public List<GenreModel> getModelGenres() {
		return this.genres;
	}
 
	public void setModelGenres(List<GenreModel> genres) {
		this.genres = genres;
	}
 
	public void addModelGenre(GenreModel genre) {
		if (genres== null) {
			genres= new ArrayList<GenreModel>();
		}
		genres.add(genre);
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

	/*Ü
	public String getOriginal_language() {
		return original_language;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}
	*/

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

}
