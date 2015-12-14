package me.lazerka.meme.ofy;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Objectify;
import me.lazerka.meme.MemeService;
import me.lazerka.meme.api.Meme;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
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
				.order("-createdAt")
				.list();

        if (!result.isEmpty()) {
            logger.trace("Fetched {} memes, latest {}.", result.size(), result.get(0).getId());
        } else {
	        logger.trace("Nothing fetched, latest.");
        }

		return result;
	}

	@Override
	public void create(Meme meme) {
		meme.setCreatedAt(now);
		meme.setCreatedBy(user.getEmail());

		ofy.save()
				.entity(meme)
				.now();
		logger.info("Created meme {}", meme.toString());
	}

	@Override
	public void delete(long id) throws OwnerMismatchException {
		Meme meme = ofy.load()
				.type(Meme.class)
				.id(id)
				.now();
		if (meme == null) {
			logger.warn("Not found meme {} to delete", id);
			return;
		}

		if (!meme.getCreatedBy().equals(user.getEmail())) {
			throw new OwnerMismatchException();
		}

		ofy.delete().entity(meme).now();

		logger.info("Deleted meme {}", id);
	}
}
