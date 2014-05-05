package me.lazerka.meme.gae.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AdhocServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(AdhocServlet.class.getName());
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private UserService userService = UserServiceFactory.getUserService();
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private Key allKey = KeyFactory.createKey("Meme", "ALL");

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    boolean loggedIn = userService.isUserLoggedIn();
    if (!loggedIn || !userService.getCurrentUser().getEmail().equals("dzmitry_lazerka@epam.com")) {
      resp.sendError(403, "LoggedIn: " + loggedIn);
      return;
    }

    PreparedQuery pq = datastore.prepare(new Query("Comment"));
//    List<Key> keys = new ArrayList<Key>();
    List<Entity> entities = new ArrayList<Entity>();
    for (Entity entity : pq.asIterable(FetchOptions.Builder.withChunkSize(100))) {
      long ts = (Long) entity.getProperty("timestamp");
      entity.setProperty("date", new Date(ts));
      String user = (String) entity.getProperty("user");
      entity.setProperty("author", user.toLowerCase() + "@epam.com");
      entities.add(entity);
    }
    datastore.put(entities);
    resp.setContentType("text/plain");
    resp.getWriter().append("\n" + entities.size());
  }
}
