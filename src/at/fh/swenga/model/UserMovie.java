package at.fh.swenga.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "UserMovie")
public class UserMovie {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	User owner;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	MovieModel movie;
	
	@Column
	private int rating;
	
	@Column
	private boolean starred;
	
	@Column
	private boolean seen;

	public UserMovie() {
	}

	public UserMovie(User owner, MovieModel movie) {
		super();
		this.owner = owner;
		this.movie = movie;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public MovieModel getMovie() {
		return movie;
	}

	public void setMovie(MovieModel movie) {
		this.movie = movie;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}	

}
