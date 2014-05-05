package me.lazerka.meme.gae.old;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CssServlet extends ConcatServlet {

  @Override
  protected String[] getFilePaths() {
    return new String[] {
        "css/main.css",
        "components/comment/comment.css",
        "components/meme/meme.css",
        "components/vote/vote.css",
      };
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/css");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Cache-Control", "public, max-age=2592000");// 1 month
    resp.getOutputStream().write(getConcatenated());
  }
}
