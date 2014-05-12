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
		boolean isAhRequest = PATTERN.matcher(req.getRequestURI()).matches();
		boolean isWarmupRequest = req.getRequestURI().equals("/_ah/warmup");
		if (isAhRequest && !isWarmupRequest) {
			logger.trace("Bypassing Guice filter", isAhRequest, isWarmupRequest);
			chain.doFilter(request, response);
			return ;
		}

		super.doFilter(request, response, chain);
	}
}
