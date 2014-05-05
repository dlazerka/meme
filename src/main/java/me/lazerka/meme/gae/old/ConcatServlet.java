package me.lazerka.meme.gae.old;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment;

@SuppressWarnings("serial")
abstract public class ConcatServlet extends HttpServlet {
  private byte[] concatenated;

  /**
   * Relative to servlet root ("war") directory.
   * For example, "js/main.js".
   */
  abstract protected String[] getFilePaths();

  protected byte[] getConcatenated() throws IOException {
    if (concatenated == null ||
        SystemProperty.environment.value() == Environment.Value.Development) {
      String[] filePaths = getFilePaths();
      concatenated = concat(filePaths);
    }
    return concatenated;
  }

  private byte[] concat(String[] filePaths) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream(256 * 1024);

    for (String filePath : filePaths) {
      FileInputStream is = new FileInputStream(filePath);
      try {
        IOUtils.copy(is, os);
      } finally {
        IOUtils.closeQuietly(is);
      }
      os.write('\n');
    }
    return os.toByteArray();
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    try {
      getConcatenated();
    } catch(IOException e) {
      throw new ServletException(e);
    }
  }

}
