package me.lazerka.meme.gae.old;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class JsServlet extends ConcatServlet {

  @Override
  protected String[] getFilePaths() {
    return new String[] {
        "components/msg/Msg.js",
        "components/comment/Comment.js",
        "components/comment/CommentView.js",
        "components/comment/Comments.js",
        "components/comment/CommentsView.js",
        "components/comment/CommentForm.js",
        "components/vote/Vote.js",
        "components/vote/VoteView.js",
        "components/meme/Meme.js",
        "components/meme/MemeView.js",
        "components/meme/Memes.js",
        "components/create/MemePreview.js",
        "components/create/CreateView.js",
        "js/analytics.js",
        "js/plugin.js",
        "js/AppRouter.js",
        "js/main.js",
      };
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("application/javascript");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Cache-Control", "public, max-age=2592000");// 1 month
    resp.getOutputStream().write(getConcatenated());
  }
}
