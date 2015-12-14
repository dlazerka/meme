package me.lazerka.meme.sql;

import com.google.appengine.api.users.User;
import me.lazerka.meme.MemeService;
import me.lazerka.meme.api.Caption;
import me.lazerka.meme.api.Image;
import me.lazerka.meme.api.Meme;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Dzmitry Lazerka
 */
public class MemeServiceSql implements MemeService {
	private static final Logger logger = LoggerFactory.getLogger(MemeServiceSql.class);

	@Inject
	private MemeMapper memeMapper;

	@Inject
	private ImageMapper imageMapper;

	@Inject
	private CaptionMapper captionMapper;

	@Inject
	private User user;

	@Override
	public List<Meme> getLatest(int count) {
		List<Meme> result = memeMapper.getLatest(count);
		if (!result.isEmpty()) {
			logger.trace("Fetched {} memes, latest {}.", result.size(), result.get(0).getId());
		}

		return result;
	}

	@Transactional
	@Override
	public void create(Meme meme) {
		Image image = checkNotNull(meme.getImage());
		String blobKey = image.getBlobKey().getKeyString();

		long imageId = imageMapper.insert(blobKey, image.getFileName(), image.getSize());
		logger.trace("Created image {}", imageId);

		long memeId = memeMapper.insert(user.getEmail(), imageId);
		meme.setId(memeId);
		logger.trace("Created meme {}", memeId);

		for(Caption caption : meme.getCaptions()) {
			long captionId = captionMapper.insert(memeId, caption.getText(), caption.getTopPx());
			logger.trace("Created caption {}", captionId);
		}

		logger.info("Created meme {}", meme.toString());
	}

	@Transactional
	@Override
	public void delete(long id) throws OwnerMismatchException {
		Meme meme = memeMapper.get(id);

		if (meme == null) {
			logger.warn("Not found meme {} to delete", id);
			return;
		}

		if (!meme.getCreatedBy().equals(user.getEmail())) {
			throw new OwnerMismatchException();
		}

		memeMapper.delete(id);
	}
}
