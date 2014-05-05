package me.lazerka.meme.gae.old;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.ByteRange;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // Note that if you're an application admin, AppEngine will reset this to "no-cache".
    resp.setHeader("Cache-Control", "public, max-age=2592000");// 1 month

    String blobKey = req.getParameter("blobKey");
    if (Util.isNullOrEmpty(blobKey)) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No 'blobKey' param");
      return;
    }
    ByteRange byteRange = blobstoreService.getByteRange(req);
    if (byteRange == null) {
      blobstoreService.serve(new BlobKey(blobKey), resp);
    } else {
      blobstoreService.serve(new BlobKey(blobKey), byteRange, resp);
    }
  }
}
