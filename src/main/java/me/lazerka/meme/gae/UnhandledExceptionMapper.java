package me.lazerka.meme.gae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Singleton
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
	private static final Logger logger = LoggerFactory.getLogger(UnhandledExceptionMapper.class);

	@Override
	public Response toResponse(Exception exception) {
		logger.warn("Unhandled exception", exception);

		if (exception instanceof WebApplicationException) {
			return ((WebApplicationException) exception).getResponse();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN)
				.build();
	}
}
