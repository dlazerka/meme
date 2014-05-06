package me.lazerka.meme.gae.old;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import javax.inject.Singleton;
import java.util.Map;

/**
 * @author Dzmitry Lazerka
 */
class ServletModule extends JerseyServletModule {
	@Override
	protected void configureServlets() {
		// Objectify requires this while using Async+Caching
		// until https://code.google.com/p/googleappengine/issues/detail?id=4271 gets fixed.
		bind(ObjectifyFilter.class).in(Singleton.class);
		filter("/*").through(ObjectifyFilter.class);

		// Route all requests through GuiceContainer.
//		serve("/db").with(DBServlet.class);
		serve("/rest/*").with(GuiceContainer.class, getJerseyParams());

		// Handle "application/json" by Jackson.
		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

		bind(MailService.class).toInstance(MailServiceFactory.getMailService());
		bind(MemcacheService.class).toInstance(MemcacheServiceFactory.getMemcacheService());
		bind(UserService.class).toInstance(UserServiceFactory.getUserService());
		bind(URLFetchService.class).toInstance(URLFetchServiceFactory.getURLFetchService());

		registerObjectifyEntities();
	}

	private void registerObjectifyEntities() {
		//ObjectifyService.register(DeviceEntity.class);
	}

	private Map<String, String> getJerseyParams() {
		Map<String,String> params = Maps.newHashMap();

		params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "me.lazerka.meme.gae.servlet");
		// Read somewhere that it's needed for GAE.
		params.put(PackagesResourceConfig.FEATURE_DISABLE_WADL, "true");

		// This makes use of custom Auth+filters using OAuth2.
		// Commented because using GAE default authentication.
		// params.put(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, AuthFilterFactory.class.getName());

		//params.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		//params.put("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		//params.put("com.sun.jersey.config.feature.logging.DisableEntitylogging", "true");
		//params.put("com.sun.jersey.config.feature.Trace", "true");
		return params;
	}

	@Provides
	private Objectify provideOfy() {
		return ObjectifyService.ofy();
	}

	@Provides
	private User provideUser(UserService userService) {
		return userService.getCurrentUser();
	}

}
