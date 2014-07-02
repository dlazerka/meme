package me.lazerka.meme.api;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Virtual entity, only for key parent. Nothing is stored in DB actually.
 * @author Dzmitry Lazerka
 */
@Entity(name = "User")
public class User {
	/**
	 * Email is OK, because app is supposed to work on only one domain.
	 */
	@Id
	String email;

	public User(com.google.appengine.api.users.User user) {
		email = user.getEmail();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
