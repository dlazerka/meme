package me.lazerka.meme.gae.old;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public class GuiceFilter extends com.google.inject.servlet.GuiceFilter {
	private final Pattern PATTERN = Pattern.compile("/_ah/.*");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		// Break the chain for dev server except warmup (must be handled by app).
		boolean isAhRequest = PATTERN.matcher(req.getRequestURI()).matches();
		boolean isWarmupRequest = req.getRequestURI().equals("/_ah/warmup");
		if (isAhRequest && !isWarmupRequest) {
			chain.doFilter(request, response);
			return ;
		}

		super.doFilter(request, response, chain);
	}
}
