package me.lazerka.meme.sql;

import me.lazerka.meme.MemeService;
import me.lazerka.meme.api.Caption;
import me.lazerka.meme.api.Image;
import me.lazerka.meme.api.Meme;
import me.lazerka.meme.api.User;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
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
	Provider<User> userProvider;

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
		User user = userProvider.get();

		long imageId = imageMapper.insert(blobKey, image.getFileName(), image.getSize());
		logger.trace("Created image {}", imageId);

		long memeId = memeMapper.insert(user.getEmail(), imageId);
		logger.trace("Created meme {}", memeId);

		for(Caption caption : meme.getCaptions()) {
			long captionId = captionMapper.insert(memeId, caption.getText(), caption.getTopPx());
			logger.trace("Created caption {}", captionId);
		}

		logger.info("Created meme {}", meme.toString());
	}

	@Transactional
	@Override
	public void delete(long id) {
		// TODO: Check for owner?

		memeMapper.delete(id);

		// TODO: remove blob or not?
	}
}
