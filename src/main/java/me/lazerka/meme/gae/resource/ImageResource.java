package me.lazerka.meme.gae.resource;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions.Builder;
import com.googlecode.objectify.Objectify;
import me.lazerka.meme.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

/**
 * @author Dzmitry Lazerka
 */
@Path("/image")
public class ImageResource {

	private static final Logger logger = LoggerFactory.getLogger(ImageResource.class);

	private final String BLOBSTORE_CALLBACK = "/blobstore-callback";

	@Inject
	Objectify ofy;

	@Inject
	BlobstoreService blobstore;

	@Inject
	ImagesService images;

	@Inject
	User user;

	@GET
	@Path("/url-for-upload")
	@Produces("text/plain")
	public String getUploadUrl(@Context UriInfo uriInfo) {
		// Compute callback path to {@link #blobstoreCallback}.
		String absolutePath = uriInfo.getBaseUriBuilder()
				.path(ImageResource.class)
				.path(BLOBSTORE_CALLBACK)
				.build()
				.getPath();

		String uploadUrl = blobstore.createUploadUrl(
				absolutePath,
				UploadOptions.Builder.withMaxUploadSizeBytes(32 << 20));
		logger.debug("Generated upload url to {}: {}", absolutePath, uploadUrl);
		return uploadUrl;
	}

	@POST
	@Path(BLOBSTORE_CALLBACK)
	@Produces("text/plain")
	public String blobstoreCallback(@Context HttpServletRequest request) {
		Map<String, List<BlobInfo>> uploads = blobstore.getBlobInfos(request);
		String formFieldName = "file";
		List<BlobInfo> blobInfos = uploads.get(formFieldName);
		logger.info("Received blobInfos: {}", blobInfos);

		if (blobInfos.isEmpty()) {
			Response response = Response.status(Status.BAD_REQUEST).entity("No blobs in upload").build();
			throw new WebApplicationException(response);
		} else if (blobInfos.size() > 1) {
			// Deleting blobs.
			List<BlobKey> blobKeys = blobstore.getUploads(request).get(formFieldName);
			blobstore.delete(blobKeys.toArray(new BlobKey[blobKeys.size()]));

			Response response = Response.status(Status.BAD_REQUEST).entity("More than one blobs in upload").build();
			throw new WebApplicationException(response);
		}

		BlobInfo blobInfo = blobInfos.get(0);

		String servingUrl;
		try {
			logger.debug("request.isSecure={}", request.isSecure());
			servingUrl = images.getServingUrl(Builder.withBlobKey(blobInfo.getBlobKey()).secureUrl(request.isSecure()));
		} catch (ImagesServiceFailureException e) {
			logger.warn("Exception while asking Images service to serve blobIngo {}: {}", e.getMessage(), e);
			blobstore.delete(blobInfo.getBlobKey());

			Response response = Response.status(Status.BAD_REQUEST)
					// message is empty actually
					.entity("Image Service cannot serve it, is it an image?" + e.getMessage())
					.build();
			throw new WebApplicationException(response);
		}
		logger.info("Returning serving URL: {}", servingUrl);
		// TODO return file name, size and whatnot.
		return servingUrl;
	}

	/*
	@GET
	@Path("/{blobKey}")
	@Produces("text/plain")
	public String serve(@PathParam("blobKey") String blobKey) {
		blobstore.serve(blobKey);
		logger.debug("Generated upload url: {}", uploadUrl);
		return uploadUrl;
	}
	*/
}
