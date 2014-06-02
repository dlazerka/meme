package me.lazerka.meme.gae.resource;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.ServingUrlOptions.Builder;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import me.lazerka.meme.api.Meme;
import me.lazerka.meme.api.User;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
@Path("/meme")
public class MemeResource {
	private static final Logger logger = LoggerFactory.getLogger(MemeResource.class);
	@Inject
	Objectify ofy;

	@Inject
	BlobstoreService blobstore;

	@Inject
	ImagesService images;


	@Inject
	@Named("now")
	DateTime now;

	@Inject
	User user;

	@Context
	HttpServletRequest request;

	@GET
	@Produces("application/json")
	public List<Meme> get() {
		List<Meme> entities = ofy.load()
				.type(Meme.class)
				.limit(100)
				.chunkAll()
				.order("-tm")
				.list();

		Meme m = new Meme();
		m.setCreator(user);
		BlobKey blobKey = new BlobKey("asdf");
		m.setBlobKey(blobKey);
		m.setTimeCreated(now);
		entities.add(m);
		entities.add(m);

		for(Meme meme : entities) {
			Link link = getServingUrl(meme.getBlobKey());
			meme.setServingUrl(link);
		}

		logger.trace("Returning {} entities.", entities.size());

		return entities;
	}

	private Link getServingUrl(BlobKey blobKey) {
		try {
			logger.debug("request.isSecure={}", request.isSecure());
			ServingUrlOptions urlOptions = Builder.withBlobKey(blobKey).secureUrl(request.isSecure());
			String servingUrl = images.getServingUrl(urlOptions);
			logger.debug("Created serving URL: {}", servingUrl);

			return new Link(servingUrl);

		} catch (ImagesServiceFailureException e) {
			logger.warn("Exception while asking Images service to serve blobKey {}: {}", blobKey, e);

			Response response = Response.status(Status.BAD_REQUEST)
					// message is empty actually
					.entity("Image Service cannot serve it, is it an image?" + e.getMessage())
					.build();
			throw new WebApplicationException(response);
		}
	}

	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Meme put(Meme meme) {
		logger.trace("Putting meme {}", meme.toString());

		meme.setTimeCreated(now);
		meme.setCreator(user);

		ofy.save().entity(meme).now();

		return meme;
	}

	@DELETE
	@Consumes("application/json")
	public void delete(Meme memeFromClient) {
		Meme memeFromDb;
		try {
			memeFromDb = ofy.load().entity(memeFromClient).safe();
		} catch (NotFoundException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		if (!memeFromDb.getCreatorRef().equals(Ref.create(user))) {
			Response response = Response.status(Status.FORBIDDEN).entity("Not created by you").build();
			throw new WebApplicationException(response);
		}

		ofy.delete().entity(memeFromClient).now();
	}
}
