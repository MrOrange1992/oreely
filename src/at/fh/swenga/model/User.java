package at.fh.swenga.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "User")
public class User implements java.io.Serializable
{
	private static final long serialVersionUID = 8198173157518983615L;

	/*
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	*/

	@Id
	@Column
	@NotNull
	@NotEmpty
	private String userName;
	
	@Column
	@NotNull
	@NotEmpty
	private String firstName;
	
	@Column
	@NotNull
	@NotEmpty
	private String lastName;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Calendar birthday;
	
	@Column
	@NotNull
	@NotEmpty
	private String email;

	@Column /* (name = "password", nullable = false, length = 60) */
	@NotNull
	@NotEmpty
	private String password;

	@Column /* (name = "enabled", nullable = false) */
	private boolean enabled;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<UserRole> userRoles = new HashSet<UserRole>(0);

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Genre> genres;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<UserFollower> followers;
	
	@OneToMany(mappedBy="owner",fetch=FetchType.EAGER)
    private Set<MovieList> movieLists;

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<MovieList> followingMovieLists;
	
	@OneToMany(mappedBy="owner",fetch=FetchType.EAGER)
    private Set<UserMovie> userMovies;
	
	@OneToMany(mappedBy="user",fetch=FetchType.EAGER)
    private Set<UserFeed> userFeed;
	
	//@Version
	//long version;

	public User() {}
	
	public User(String userName, String firstName, String lastName, Calendar birthday, String email, String password, boolean enabled, Set<UserRole> userRole, List<Genre> genres)
	{
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.userRoles = userRole;
		this.genres = genres;
	}

	/*
	 * GETTER / SETTER
	 */
	/*
	//id
	public int getId() { return id; }
	public void setId(int id) {this.id = id;}
	*/

	//userName
	public String getUserName() { return userName; }
	public void setUserName(String userName) { this.userName = userName; }
	
	//firstName
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	//lastName
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName;}

	//birthday
	public Calendar getBirthday() { return birthday; }
	public void setBirthday(Calendar birthday) { this.birthday = birthday; }
	
	//email
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	//password
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	//enabled
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	//userRole
	public Set<UserRole> getUserRole() { return userRoles; }
	public void setUserRole(Set<UserRole> userRoles) { this.userRoles = userRoles; }
	public void addUserRole(UserRole userRole) { userRoles.add(userRole); }

	//genres
	public List<Genre> getGenres() { return genres; }
	public void setGenres(List<Genre> genres) { this.genres = genres; }
	public void addGenres(Genre genre) 
	{
		if (genres == null) genres = new ArrayList<Genre>();
		genres.add(genre);
	}
	public void removeGenre(Genre genre) 
	{
		if(genres.contains(genre)) genres.remove(genre);
	}

	//followers
	public List<UserFollower> getFollowers() { return followers; }
	public void setFollowers(List<UserFollower> followers) { this.followers = followers; }
	public void addFollower(UserFollower follower) 
	{
		if (followers == null) followers = new ArrayList<UserFollower>();
		followers.add(follower);
	}
	public void removeFollower(UserFollower follower) 
	{
		if(followers.contains(follower)) followers.remove(follower);
	}
	
	//lists
	public Set<MovieList> getMovieLists() { return movieLists; }
	public void setMovieLists(Set<MovieList> movieLists) { this.movieLists = movieLists; }
	public void addMovieList(MovieList movieList){
		if (movieLists == null) movieLists = new HashSet<MovieList>();
		movieLists.add(movieList);
	}
	public void removeMovieList(MovieList movieList) 
	{
		if(movieLists.contains(movieList)) movieLists.remove(movieList);
	}
	
	//following lists
	public List<MovieList> getFollowingMovieLists() { return followingMovieLists; }
	public void setFollowingMovieLists(List<MovieList> followingMovieLists) { this.followingMovieLists = followingMovieLists; }
	public void addFollowingMovieList(MovieList followingMovieList){
		if (followingMovieLists == null) followingMovieLists = new ArrayList<MovieList>();
		followingMovieLists.add(followingMovieList);
	}
	public void removeFollowingMovieList(MovieList followingMovieList) 
	{
		if(followingMovieLists.contains(followingMovieList)) followingMovieLists.remove(followingMovieList);
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
	
	//user feed
	public Set<UserFeed> getUserFeed() { return userFeed; }
	public void addUserFeed(UserFeed uf) 
	{
		if (userFeed == null) userFeed = new HashSet<UserFeed>();
		userFeed.add(uf);
	}
}
