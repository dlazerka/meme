package me.lazerka.meme.gae.old;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.lazerka.meme.gae.old.MemeDao.Sort;

@SuppressWarnings("serial")
public class MemesServlet extends HttpServlet {
  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(MemesServlet.class.getName());

  private final MemeDao memeDao = new MemeDao();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("X-Chrome-Exponential-Throttling", "disable");

    int page = 0;
    if (!Util.isNullOrEmpty(req.getParameter("page"))) {
      page = Integer.parseInt(req.getParameter("page"));
    }

    Sort sort = Sort.DATE;
    if ("rating".equals(req.getParameter("sort"))) {
      sort = Sort.RATING;
    }

    String json = memeDao.getAllAsJson(req, page, sort);
    resp.getWriter().write(json);
  }
}
