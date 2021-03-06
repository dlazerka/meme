package me.lazerka.meme.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
@Entity(name = "Meme")
@Cache
public class Meme {
	@Parent
	Ref<User> owner;

	@Id
	Long id;

	@Index
	DateTime createdAt;

	Image image;

	List<Caption> captions = new ArrayList<>(3);

	@JsonIgnore
	public Ref<User> getOwnerRef() {
		return owner;
	}

	@JsonProperty("ownerEmail")
	public String getOwnerEmail() {
		return owner.getKey().getName();
	}

	@JsonIgnore
	public void setOwner(Ref<User> owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public void setOwner(User owner) {
		this.owner = Ref.create(owner);
	}

	public Long getId() {
		return id;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Caption> getCaptions() {
		return captions;
	}

	@Override
	public String toString() {
		return "Meme{" +
				"owner=" + owner +
				", id=" + id +
				", createdAt=" + createdAt +
				", image=" + image +
				", captions=" + captions +
				'}';
	}
}
