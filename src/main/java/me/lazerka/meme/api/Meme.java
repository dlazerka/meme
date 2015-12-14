package me.lazerka.meme.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author Dzmitry Lazerka
 */
@Entity(name = "Meme")
@Cache
public class Meme {
	@Id
	Long id;

	@Index
	String createdBy;

	@Index
	DateTime createdAt;

	Image image;

	List<Caption> captions = new ArrayList<>(3);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		checkState(this.id == null);
		this.id = id;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	@JsonIgnore
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
				"id=" + id +
				", createdBy=" + createdBy +
				", createdAt=" + createdAt +
				", image=" + image +
				", captions=" + captions +
				'}';
	}
}
