package org.frdmrt.envirodumper;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DumpEnvironment extends HttpServlet {
	private static final long serialVersionUID = 1904358306853295742L;
	@Override
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Environment Values:\n");
		Map<String,String> envVars = System.getenv();
		for (String key : envVars.keySet()) {
			response.getWriter().println(key + ": " + envVars.get(key));
		}

	}

}
