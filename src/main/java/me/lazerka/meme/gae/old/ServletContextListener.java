package me.lazerka.meme.gae.old;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ServletContextListener implements javax.servlet.ServletContextListener {
	private static final String INJECTOR_NAME = Injector.class.getName();

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// Redirect java.util.logging through SLF4J.
		// Doesn't work in GAE, cause java.util.logging.LogManager is restricted.
		//SLF4JBridgeHandler.removeHandlersForRootLogger();
		//SLF4JBridgeHandler.install();

		ServletContext servletContext = servletContextEvent.getServletContext();

		Injector injector = Guice.createInjector(new ServletModule());
		servletContext.setAttribute(INJECTOR_NAME, injector);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		servletContext.removeAttribute(INJECTOR_NAME);
	}
}
