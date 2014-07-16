package me.lazerka.meme.ofy;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;
import me.lazerka.meme.api.Meme;
import me.lazerka.meme.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dzmitry Lazerka
 */
public class OfyModule extends AbstractModule {
	private static final Logger logger = LoggerFactory.getLogger(OfyModule.class);

	@Override
	protected void configure() {
		logger.trace("setUpObjectify");
		ObjectifyFactory factory = ObjectifyService.factory();
		JodaTimeTranslators.add(factory);

		factory.register(Meme.class);
		factory.register(User.class);

		// Warmup Objectify.
		Objectify ofy = factory.begin();
		ofy.load()
				.type(Meme.class)
				.keys()
				.first()
				.now();
	}

	@Provides
	private Objectify provideOfy() {
		return ObjectifyService.ofy();
	}

}
