/*
 * Copyright (c) 2014 Dzmitry Lazerka
 */

package me.lazerka.meme.gae;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Otherwise local GAE shows 403 instead of welcome-file index.html.
 *
 * @author Dzmitry Lazerka
 */
public class DummyServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// nothing
	}
}
