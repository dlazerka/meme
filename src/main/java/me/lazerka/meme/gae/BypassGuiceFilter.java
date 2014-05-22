package me.lazerka.meme.gae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Bypasses _ah/* requests except some.
 *
 * @author Dzmitry Lazerka
 */
public class BypassGuiceFilter extends com.google.inject.servlet.GuiceFilter {
	private final static Logger logger = LoggerFactory.getLogger(BypassGuiceFilter.class);

	private final Pattern PATTERN = Pattern.compile("/_ah/.*");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.trace("Got request: {}", request instanceof HttpServletRequest
				? ((HttpServletRequest) request).getRequestURI()
				: "not HTTP request");
		HttpServletRequest req = (HttpServletRequest) request;


		// Break the chain for dev server except warmup (must be handled by app).
		String requestURI = req.getRequestURI();
		boolean isAhRequest = PATTERN.matcher(requestURI).matches();

		// Here's the main thing.
		boolean shouldGuiceHandle = requestURI.equals("/_ah/warmup") || requestURI.startsWith("/_ah/upload/");

		if (isAhRequest && !shouldGuiceHandle) {
			logger.trace("Bypassing Guice filter: {}", requestURI);
			chain.doFilter(request, response);
			return ;
		} else if (isAhRequest) {
			logger.info("Not bypassing Guice filter: {}", requestURI);
		}

		super.doFilter(request, response, chain);
	}
}
