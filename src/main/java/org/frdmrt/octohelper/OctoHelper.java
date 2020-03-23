package org.frdmrt.octohelper;

import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigInteger;

import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;


public class OctoHelper extends HttpServlet {

	private static final long serialVersionUID = -1641096228274971485L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// set response headers
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		// create HTML form
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<title>Octo Helper</title>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n")
			  .append("			<form action=\"octohelper\" method=\"POST\">\r\n")
			  .append("				Enter Octal Digits: \r\n")
			  .append("				<input size=\"80\" type=\"text\" name=\"octalDigits\" />\r\n")
			  .append("				<input type=\"submit\" value=\"Convert\" />\r\n")
			  .append("			</form>\r\n")
			  .append("		</body>\r\n")
			  .append("</html>\r\n");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String octalDigits = request.getParameter("octalDigits");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		// create HTML response
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<title>Welcome message</title>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n");
		if (octalDigits != null && !octalDigits.trim().isEmpty()) {
			writer.append("	Original input: " + octalDigits + ".<br>\r\n");
			octalDigits = octalDigits.replaceAll("[^0-7]","");
			writer.append("	Stripped to Octal Digits only: " + octalDigits + ".<br>\r\n");
			BigInteger octalBigInt = new BigInteger(octalDigits, 8);
			writer.append("	Converted to BigInt: " + octalBigInt + ".<br>\r\n");
			String hexString = octalBigInt.toString(16);
			writer.append("	In hex format: " + hexString + ".<br>\r\n");
			if (hexString.length() % 2 == 0) {
				writer.append("hexString length = " + hexString.length() + ".<br>\r\n" );
			} else {
				writer.append("hexString needs trimming, length = " + hexString.length() + ".<br>\r\n" );
				hexString = hexString.substring(1);
				writer.append("First digit stripped from hexString, length = " + hexString.length() + ".<br>\r\n" );
			}
			byte[] hexBinary = DatatypeConverter.parseHexBinary(hexString);
			writer.append("Size of byte array = " + hexBinary.length + ".<br>\r\n" );
			String base64String = Base64.getEncoder().encodeToString(hexBinary);
			writer.append("Bae64 encoding of byte array = " + base64String + ".<br>\r\n" );
		} else {
			writer.append("	You left the input field blank.<br>\r\n");
		}
		  writer
		  .append("<hr>\r\n")
		  .append("			<form action=\"octohelper\" method=\"POST\">\r\n")
		  .append("				Enter Octal Digits: \r\n")
		  .append("				<input size=\"80\" type=\"text\" name=\"octalDigits\" />\r\n")
		  .append("				<input type=\"submit\" value=\"Convert\" />\r\n")
		  .append("			</form>\r\n");
		  
		writer.append("		</body>\r\n")
			  .append("</html>\r\n");
	}	
	
}
