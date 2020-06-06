package org.frdmrt.jerslet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// Probably be best if we spin this out into its own project and not mix and match with generic tomcat servlets.

@Path("/jerslet")
public class JerseyService {
	
	@GET
	@Path("{clientName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response myDoGet(@PathParam("clientName") String name) {
		String output = "Hello, " + name + "!";
		return Response.status(200).entity(output).build();
	}

}
