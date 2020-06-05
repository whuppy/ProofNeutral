package org.frdmrt.headerdumper;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//dummy change
public class DumpHttpHeaders extends HttpServlet {
	private static final long serialVersionUID = 1904358306853295742L;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println("Request Headers:\n");
		Enumeration<String> hedNames = request.getHeaderNames();
		while (hedNames.hasMoreElements()) {
			String hn = hedNames.nextElement();
			String hed = request.getHeader(hn);
			response.getWriter().println(hn + " : " + hed);
		}
		hedNames = request.getHeaderNames();
		List<String> hedNamList = Collections.list(hedNames);
		if (hedNamList.contains("authorization")) {
			response.getWriter().println("\nauthorization header found:");
			String authVal = request.getHeader("authorization");
			String userPassEncoded = authVal.split(" ")[1];
			response.getWriter().println(userPassEncoded);
			byte[] decoded = Base64.getDecoder().decode(userPassEncoded);
			String decStr = new String(decoded);
			response.getWriter().println(decStr);
			String[] userAndPass = decStr.split(":");
			response.getWriter().println(userAndPass[0]);
			response.getWriter().println(userAndPass[1]);
		}
		response.getWriter().println("\nRequest Attributes:\n");
		for (Enumeration<String> allTributes = request.getAttributeNames(); allTributes.hasMoreElements(); ) {
			response.getWriter().println(allTributes.nextElement());
		}
	}

}
