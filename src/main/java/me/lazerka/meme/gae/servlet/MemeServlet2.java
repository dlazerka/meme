package me.lazerka.meme.gae.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/meme")
public class MemeServlet2 {
	@GET
	@Produces("text/plain")
	public String get() {
		return "ok";
	}
}
