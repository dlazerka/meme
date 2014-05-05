package me.lazerka.meme.gae.old;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment;

@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {
  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(MainServlet.class.getName());
  private final Pattern memePattern = Pattern.compile("^/([0-9]+)$");

  private final Util util = new Util();
  private final MemeDao memeDao = new MemeDao();
  private final UserService userService = UserServiceFactory.getUserService();

  private String welcomeFileContent;

  @Override
  public void init() throws ServletException {
    try {
      readFile();
    } catch (FileNotFoundException e) {
      throw new ServletException(e);
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String uri = req.getRequestURI();

    // If opening a particular meme like "GET /12345".
    String meme = "null";
    Matcher memeMatcher = memePattern.matcher(uri);
    if (memeMatcher.matches()) {
      long id = Long.parseLong(memeMatcher.group(1));
      meme = memeDao.getAsJson(id);
    }

    if (meme == null || !(memeMatcher.matches() || uri.equals("/"))) {
      resp.sendError(404, "Not Found");
      return;
    }

    if (SystemProperty.environment.value() == Environment.Value.Development) {
      readFile();
    }
    String replaced = welcomeFileContent;
    replaced = replaced.replace("###MEME###", meme);

    String allMemes = memeDao.getAllAsJson(req, 0, MemeDao.Sort.DATE);
    String topMemes = memeDao.getAllAsJson(req, 0, MemeDao.Sort.RATING);
    String deletedMemesIds = memeDao.getDeletedIdsAsJson();

    replaced = replaced.replace("###ALL_MEMES###", allMemes);
    replaced = replaced.replace("###TOP_MEMES###", topMemes);
    replaced = replaced.replace("###DELETED_MEMES_IDS###", deletedMemesIds);

    // Check authentication.
    // If not logged in, send him to login url.
    // If logged in, but email doesn't end on @epam.com, send to google.com/a/epam.com.
    // We shouldn't send to logout url, because it logs user out of internal services.
    boolean userAuthenticated = util.isAuthenticated();
    String returnUrl = req.getRequestURL().toString();
    String logoutUrl = userService.createLogoutURL(returnUrl);
    // there's no point redirecting user to real login page, because it will login him automatically.
    String loginUrl = userService.createLoginURL(returnUrl);

    replaced = replaced.replace("###IS_AUTHENTICATED###", "" + userAuthenticated);
    replaced = replaced.replace("###IS_LOGGED_IN###", "" + userService.isUserLoggedIn());
    replaced = replaced.replace("###LOGIN_URL###", loginUrl);
    replaced = replaced.replace("###LOGOUT_URL###", logoutUrl);
    if (userService.isUserLoggedIn()) {
      String email = userService.getCurrentUser().getEmail();
      replaced = replaced.replace("###USER_EMAIL###", email);
    }

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().write(replaced);
  }

  private void readFile() throws FileNotFoundException, IOException {
    FileInputStream is = new FileInputStream("index.html");
    try {
      welcomeFileContent = IOUtils.toString(is, Charset.forName("UTF-8"))
          .replace("###APP_VERSION###", SystemProperty.applicationVersion.get())
          .replace("###ENV###", SystemProperty.environment.value().toString())
          .replace("###MEMES_PER_PAGE###", MemeDao.MEMES_PER_PAGE + "");
    } finally {
      IOUtils.closeQuietly(is);
    }
  }
}
