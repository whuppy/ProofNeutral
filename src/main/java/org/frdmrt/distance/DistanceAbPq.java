package org.frdmrt.distance;

import java.io.IOException;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DistanceAbPq extends HttpServlet {

	private static final long serialVersionUID = -1641096228274971485L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		AbPqDistanceObject abpq = new AbPqDistanceObject(100);
		request.setAttribute("abpq", abpq);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/RenderAbPq.jsp");
		requestDispatcher.forward(request, response);
	}
}
