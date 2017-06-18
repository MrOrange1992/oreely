package at.fh.swenga.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Follower
{
	
	@Id
	@Column
	private String followerName;
	
	@Column
	private String userName;
	
	@ManyToMany(mappedBy = "followers",fetch=FetchType.EAGER)
	private Set<User> users;
	
	public Follower() {
	}
 
	public Follower(String followerName, String userName) {
		super();
		this.followerName = followerName;
		this.userName = userName;
	}

    public String getFollowerName() { return followerName; }
    public void setFollowerName(String followerName) { this.followerName = followerName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Set<User> getUsers() {
		return users;
	}
	public void Users(Set<User> users) {
		this.users = users;
	}
	public void addUser(User user){
		if (users == null) users = new HashSet<User>();
        users.add(user);
	}

}
