package me.lazerka.meme.gae.resource;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.ServingUrlOptions.Builder;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
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

	@GET
	@Produces("application/json")
	public List<Meme> get(@Context HttpServletRequest request) {
		List<Meme> entities = ofy.load()
				.type(Meme.class)
				.limit(100)
				.chunkAll()
				.order("-createdAt")
				.list();

		// Fill UI-only serving url.
		for(Meme meme : entities) {
			BlobKey blobKey = meme.getImage().getBlobKey();
			String url = getServingUrl(blobKey, request);
			meme.getImage().setUrl(url);
		}

		logger.trace("Returning {} entities.", entities.size());

		return entities;
	}

	private String getServingUrl(BlobKey blobKey, HttpServletRequest request) {
		try {
			logger.debug("request.isSecure={}", request.isSecure());
			ServingUrlOptions urlOptions = Builder.withBlobKey(blobKey)
					.secureUrl(request.isSecure())
					.imageSize(500); // must conform to CSS
			String servingUrl = images.getServingUrl(urlOptions);
			logger.debug("Created serving URL: {}", servingUrl);

			return servingUrl;

		} catch (ImagesServiceFailureException e) {
			logger.warn("Exception while asking Images service to serve blobKey {}: {}", blobKey, e);

			Response response = Response.status(Status.BAD_REQUEST)
					// message is empty actually
					.entity("Image Service cannot serve it, is it an image?" + e.getMessage())
					.build();
			throw new WebApplicationException(response);
		}
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Meme create(Meme meme) {
		logger.trace("Posted meme {}", meme.toString());

		if (meme.getId() != null) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		meme.setCreatedAt(now);
		meme.setOwner(user);

		ofy.save().entity(meme).now();

		logger.info("Created meme {}", meme.toString());

		return meme;
	}

	/*
	Disable updates for now.

	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Meme update(Meme meme) {
		logger.trace("Putted meme {}", meme.toString());

		Meme existing = fetchExisting(meme);
		checkCreator(existing);

		if (!existing.getCreatedAt().equals(meme.getCreatedAt())) {
			Response response = Response.status(Status.FORBIDDEN).entity("You cannot modify timeCreated").build();
			throw new WebApplicationException(response);
		}

		ofy.save().entity(meme).now();

		return meme;
	}
	*/

	@DELETE
	@Path("/{ownerEmail}/{id}")
	@Consumes("application/json")
	public void delete(@PathParam("ownerEmail") String ownerEmail, @PathParam("id") long id) {
		/*
		if (!user.getEmail().equals(owner)) {
			Response response = Response.status(Status.FORBIDDEN).entity("Not created by you.").build();
			throw new WebApplicationException(response);
		}
		*/

		Key<User> userKey = Key.create(User.class, ownerEmail);
		Key<Meme> memeKey = Key.create(userKey, Meme.class, id);

		ofy.delete().key(memeKey).now();

		// TODO: remove blob or not?
	}
}
