package at.fh.swenga.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class UserFollower {
	
	@Id
	private String user_id;
	
	private String follower_id;
	
	@ManyToMany(mappedBy = "followers",fetch=FetchType.EAGER)
	private List<User> follower;
	
	public UserFollower() {
	}
 
	public UserFollower(String user_id, String follower_id) {
		super();
		this.user_id = user_id;
		this.follower_id = follower_id;		
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getFollower_id() {
		return follower_id;
	}

	public void setFollower_id(String follower_id) {
		this.follower_id = follower_id;
	}

	public List<User> getFollower() {
		return follower;
	}

	public void setFollower(List<User> follower) {
		this.follower = follower;
	}	

}
