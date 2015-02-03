package me.lazerka.meme.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import me.lazerka.meme.MemeService;
import me.lazerka.meme.api.Meme;
import me.lazerka.meme.api.User;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
public class MemeServiceOfy implements MemeService {
	private static final Logger logger = LoggerFactory.getLogger(MemeServiceOfy.class);

	@Inject
	Objectify ofy;

	@Inject
	User user;

	@Inject
	@Named("now")
	DateTime now;

	@Override
	public List<Meme> getLatest(int count) {
		List<Meme> result = ofy.load()
				.type(Meme.class)
				.limit(100)
				.chunkAll()
				.filter("createdAt > ", new Date(1))
				.order("-createdAt")
				.list();

        if (!result.isEmpty()) {
            logger.trace("Fetched {} memes, latest {}.", result.size(), result.get(0).getId());
        }

		return result;
	}

	@Override
	public void create(Meme meme) {
		meme.setCreatedAt(now);
		meme.setOwner(user);

		ofy.save()
				.entity(meme)
				.now();
		logger.info("Created meme {}", meme.toString());
	}

	@Override
	public void delete(String email, long id) {
		/*
		if (!user.getEmail().equals(owner)) {
			Response response = Response.status(Status.FORBIDDEN).entity("Not created by you.").build();
			throw new WebApplicationException(response);
		}
		*/

        Key<User> parentKey = Key.create(User.class, email);
        Key<Meme> memeKey = Key.create(parentKey, Meme.class, id);

		ofy.delete().key(memeKey).now();

		logger.info("Deleted meme {}", id);
	}
}
