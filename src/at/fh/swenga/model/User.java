package at.fh.swenga.model;

import java.util.ArrayList;
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
import javax.persistence.Version;

import at.fh.swenga.model.Genre;

@Entity
@Table(name = "users")
public class User implements java.io.Serializable {
	// private static final long serialVersionUID = 8198173157518983615L;

	@Id
	@Column /*
			 * (name = "username", unique = true, nullable = false, length = 45)
			 */
	private String username;

	@Column /* (name = "password", nullable = false, length = 60) */
	private String password;

	@Column /* (name = "enabled", nullable = false) */
	private boolean enabled;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<UserRole> userRole = new HashSet<UserRole>(0);

	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Genre> genres;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<UserFollower> followers;

	@Version
	long version;

	public User() {
	}

	public User(String username, String password, boolean enabled) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	public User(String username, String password, boolean enabled,
			Set<UserRole> userRole, List<Genre> genres) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.userRole = userRole;
		this.genres = genres;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserRole> getUserRole() {
		return userRole;
	}

	public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public void addGenres(Genre genre) {
		if (genres == null) {
			genres = new ArrayList<Genre>();
		}
		genres.add(genre);
	}
	
	public void removeGenre(Genre genre) {
		if(genres.contains(genre)){
			genres.remove(genre);
		}
	}

	public List<UserFollower> getFollowers() {
		return followers;
	}

	public void setFollowers(List<UserFollower> followers) {
		this.followers = followers;
	}

	public void addFollower(UserFollower follower) {
		if (followers == null) {
			followers = new ArrayList<UserFollower>();
		}
		followers.add(follower);
	}
	
	public void removeFollower(UserFollower follower) {
		if(followers.contains(follower)){
			followers.remove(follower);
		}
	}	
}
