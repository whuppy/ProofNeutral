package org.frdmrt.envirodumper;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

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
		
		response.getWriter().println("\nProperties:\n");
		Properties sysProps =  System.getProperties();
		Enumeration<Object> propKeysEnum = sysProps.keys();
		// Sort the keys alphabetically:
		Vector<Object> propKeysVector = new Vector<Object>();
		while (propKeysEnum.hasMoreElements()) {
			propKeysVector.add(propKeysEnum.nextElement());
        }
        Collections.sort(propKeysVector, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        // Output the sorted keys:
        Enumeration<Object> sortedKeys = propKeysVector.elements();
		while (sortedKeys.hasMoreElements()) {
			String key = (String) sortedKeys.nextElement();
			String val = sysProps.getProperty(key);
			response.getWriter().println(key + " = " + val);
		}

	}

}
