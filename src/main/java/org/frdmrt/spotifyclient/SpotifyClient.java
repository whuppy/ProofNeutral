package org.frdmrt.spotifyclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;


public class SpotifyClient extends HttpServlet {
	private static final long serialVersionUID = 1904358306353295742L;
	
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
			  .append("			<title>Spotify Client</title>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n")
			  .append("			<form action=\"spotifyclient\" method=\"POST\">\r\n") // This has to match up with web.xml entry!
			  .append("				UserName: \r\n")
			  .append("				<input size=\"80\" type=\"text\" name=\"userName\" /><br>\r\n")
			  .append("				AuthToken:\r\n<input size=\"80\" type=\"text\" name=\"authToken\" /><br>\r\n")
			  .append("				ArtistName:\r\n<input size=\"80\" type=\"text\" name=\"artistName\" /><br>\r\n")
			  .append("				OctalDigits<input size=\"80\" type=\"text\" name=\"octalDigits\" /><br>\r\n")
			  .append("				<input type=\"submit\" value=\"Submit\" />\r\n")
			  .append("			</form>\r\n")
			  .append("		</body>\r\n")
			  .append("</html>\r\n");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String octalDigits = request.getParameter("octalDigits");
		String authToken = request.getParameter("authToken");
		String artistName = request.getParameter("artistName");
		String userName = request.getParameter("userName");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		// create HTML response
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<title>POST Results from Spotify Client</title>\r\n")
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
			writer.append("Base64 encoding of byte array = " + base64String + ".<br>\r\n" );
		} else {
			writer.append("	You left the octalDigits field blank.<br>\r\n");
		}
		
		if (userName != null && !userName.trim().isEmpty()) {
			// If there's a userName then let's hit Spotify!
			writer.append("<hr>Hitting Spotify with userName=" + userName);
		} else {
			writer.append("<hr>You left the userName field blank");
		}
		
		writer.append("<hr>\r\n")
		  .append("			<form action=\"spotifyclient\" method=\"GET\">\r\n") // Has to match up with web.xml entry!
		  .append("				<input type=\"submit\" value=\"Go back to entry form.\" />\r\n")
		  .append("			</form>\r\n");
		  
		writer.append("		</body>\r\n")
			  .append("</html>\r\n");
	}	

}
