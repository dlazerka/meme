package me.lazerka.meme.gae.old;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Maps;
import com.google.inject.Scopes;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import javax.inject.Singleton;
import java.util.Map;

class ServletModule extends JerseyServletModule {
	@Override
	protected void configureServlets() {
		// Objectify requires this while using Async+Caching
		// until https://code.google.com/p/googleappengine/issues/detail?id=4271 gets fixed.
		bind(ObjectifyFilter.class).in(Singleton.class);
		filter("/*").through(ObjectifyFilter.class);

		// Route all requests through GuiceContainer.
//		serve("/db").with(DBServlet.class);
		serve("/api/*").with(GuiceContainer.class, getJerseyParams());

		// Handle "application/json" by Jackson.
		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

		//bind(ShiftStatsResource.class);

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

		//params.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		//params.put("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
		//params.put("com.sun.jersey.config.feature.logging.DisableEntitylogging", "true");
		//params.put("com.sun.jersey.config.feature.Trace", "true");
		return params;
	}
}
