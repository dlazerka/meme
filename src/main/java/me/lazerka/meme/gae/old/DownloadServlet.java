package me.lazerka.meme.gae.old;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(DownloadServlet.class.getName());
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private final MemcacheService memcacheService =
      MemcacheServiceFactory.getMemcacheService(DownloadServlet.class.getName());
  private static final Charset UTF8 = Charset.forName("UTF-8");

  /** Downloads image by provided url, and uploads it to blobstore. */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");

    // Anti-dos. Not bulletproof, but still.
    Long lastRequestTs = (Long) memcacheService.get(req.getRemoteAddr());
    long currentTs = System.currentTimeMillis();
    if (lastRequestTs != null && (currentTs - lastRequestTs) < 250) {
      logger.log(Level.WARNING, "Too Many Requests from " + req.getRemoteAddr());
      writeError(429, "Too Many Requests", resp);
      return;
    }
    memcacheService.put(req.getRemoteAddr(), currentTs);

    String urlS = req.getParameter("url");
    if (Util.isNullOrEmpty(urlS)) {
      writeError(HttpServletResponse.SC_BAD_REQUEST, "No url param", resp);
      return;
    }

    URL downloadUrl;
    try {
      downloadUrl = new URL(urlS);
    } catch (MalformedURLException e) {
      String msg = "Malformed url: " + urlS;
      logger.log(Level.WARNING, msg);// Do not spam logs with stacktraces.
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return;
    }

    String protocol = downloadUrl.getProtocol();
    if (!protocol.equals("http") && !protocol.equals("https")) {
      String msg = "Url is not HTTP or HTTPS, but: " + protocol;
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return;
    }

    try {
      byte[] imageContent = downloadFile(resp, urlS, downloadUrl);
      if (imageContent == null) {
        // Error has been written.
        return;
      }
      String jsonResponse = uploadFile(imageContent, resp);
      resp.getWriter().write(jsonResponse);
    } catch (IOException e) {
      logger.log(Level.WARNING, e.getMessage(), e);
      writeError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage(), resp);
      return;
    }
  }

  private String uploadFile(byte[] content, HttpServletResponse resp) throws IOException {
    URL uploadUrl = new URL(blobstoreService.createUploadUrl("/upload"));
    HttpURLConnection uploadConnection = (HttpURLConnection) uploadUrl.openConnection();
    String jsonResponse = null;
    try {
      String boundary = "memgenboundaryasdwqercbcvqwernertaslasq";
      uploadConnection.setDoOutput(true);
      uploadConnection.setRequestMethod("POST");
      uploadConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      OutputStream os = uploadConnection.getOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(os, UTF8);

      osw.write("--" + boundary + "\r\n");
      osw.write("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n");
      osw.write("Content-Type: image/jpeg\r\n\r\n");
      osw.flush();// otherwise expect OOM error.

      os.write(content);
      os.flush();

      osw.write("--" + boundary + "--\r\n");
      osw.close();

      InputStream is = uploadConnection.getInputStream();
      jsonResponse = IOUtils.toString(is, UTF8);
    } catch (IOException e) {
      String msg = "IOException while uploading";
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
    } finally {
      uploadConnection.disconnect();
    }
    return jsonResponse;
  }

  private byte[] downloadFile(HttpServletResponse resp, String urlS,
      URL downloadUrl) throws IOException {
    HttpURLConnection downloadConnection = (HttpURLConnection) downloadUrl.openConnection();
    downloadConnection.connect();

    int responseCode;
    try {
      responseCode = downloadConnection.getResponseCode();
    } catch (IOException e) {
      String msg = "Cannot fetch " + urlS + ": " + e.getMessage();
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return null;
    } catch (IllegalArgumentException e) {
      // In case url is "http:".
      String msg = "Cannot fetch " + urlS + ": " + e.getMessage();
      logger.log(Level.WARNING, msg, e);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return null;
    }

    if (responseCode != 200) {
      String msg = "Download respose code is " + responseCode;
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return null;
    }

    int contentLength = downloadConnection.getContentLength();
    if (contentLength > Util.MAX_IMAGE_SIZE) {
      String msg = "Image size is too large (" + contentLength + "): " + urlS;
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return null;
    } else if (contentLength <= 0) {
      String msg = "No image at url " + urlS + ", contentLength=" + contentLength;
      logger.log(Level.WARNING, msg);
      writeError(HttpServletResponse.SC_BAD_REQUEST, msg, resp);
      return null;
    }

    InputStream is = downloadConnection.getInputStream();
    byte[] imageContent;
    try {
      imageContent = IOUtils.toByteArray(is, contentLength);
    } finally {
      downloadConnection.disconnect();
    }
    return imageContent;
  }

  private void writeError(int statusCode, String message, HttpServletResponse resp) throws IOException {
    resp.setStatus(statusCode);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    JsonWriter w = new JsonWriter(resp.getWriter());
    w.beginObject().name("message").value(message).endObject();
    w.close();
  }}
