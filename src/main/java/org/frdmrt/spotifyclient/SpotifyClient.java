package org.frdmrt.spotifyclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SpotifyClient extends HttpServlet {
	private static final long serialVersionUID = 1904358306353295742L;
	private JsonObject allResultsJobject = null;
	
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
			  .append("				userName: <input size=\"80\" type=\"text\" name=\"userName\" value=\"125348716\" /><br>\r\n")
			  .append("				artistName: <input size=\"80\" type=\"text\" name=\"artistName\" /><br>\r\n")
			  .append("				octalDigits: <input size=\"80\" type=\"text\" name=\"octalDigits\" /><br>\r\n")
			  .append("				contentType: <input size=\"80\" type=\"text\" name=\"contentType\" value=\"text/html\" /><br>\r\n")
			  .append("				clientId : <input size=\"80\" type=\"text\" name=\"clientId\" value=\"7f5f9e8debc44b2cafb579b0125e9177\" /><br>\r\n")
			  .append("				clientSecret : <input size=\"80\" type=\"text\" name=\"clientSecret\" /><br>\r\n")
			  .append("				objectId : <input size=\"80\" type=\"text\" name=\"objectId\" /><br>\r\n")
			  .append("				operationUrl : <input size=\"80\" type=\"text\" name=\"operationUrl\" value=\"/me/playlists\" /><br>\r\n")
			  .append("				authToken: <input size=\"100\" type=\"text\" name=\"authToken\" /><br>\r\n")
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
		//String artistName = request.getParameter("artistName");
		//String userName = request.getParameter("userName");
		String clientId = request.getParameter("clientId");
		String clientSecret = request.getParameter("clientSecret");
		String objectId = request.getParameter("objectId");
		String operationUrl = request.getParameter("operationUrl");
		if ((null == operationUrl)  || operationUrl.trim().isEmpty()) {
			operationUrl = "/me/playlists"; // make sure there's a leading slash!
			// ooh, maybe add it here if the first char isn't '/'
		}
		String contentType = request.getParameter("contentType");
		if ((null == contentType)  || contentType.trim().isEmpty()) {
			contentType = "text/html"; // yes redundant to value="text/html" in the form. Belt and suspenders
		}
		
		response.setContentType(contentType);
		response.setCharacterEncoding("UTF-8");
		
		// create HTML response
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<title>POST Results from Spotify Client</title>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n");
		
		// run the Octohelper
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
		
		// Do a Spotify:
		if (authToken != null && !authToken.trim().isEmpty()) {
			// write out some web page:
			writer.append("<hr>Hitting Spotify with authToken= " + authToken);
			writer.append("<br>objectId=" + objectId);
			writer.append("<br>client_id=" + clientId);
			writer.append("<br>clientSecret=" + clientSecret);
			
			// set up the URL:
			String spotify_api = "https://api.spotify.com/v1" ; // leave out trailing slash
			String spotifyUrl = spotify_api + operationUrl;
			//String spotifyUrl = "http://localhost:8080/ProofNeutral/dumpheaders" ;
			writer.append("<br>spotifyUrl=" + spotifyUrl);
			
			// set up the HTTP Client:
			HttpClient client = HttpClient.newBuilder().build();
		    HttpRequest outgoingRequest = HttpRequest.newBuilder()
		            .uri(URI.create(spotifyUrl))
		            .header("Authorization", "Bearer " + authToken)
		            .header("Accept", "application/json")
		            .header("Content-Type", contentType)
		            .build();
		    HttpResponse<String> spotifyResponse;
			try {
				// Hit Spotify for the first batch of JSON:
				spotifyResponse = client.send(outgoingRequest, BodyHandlers.ofString());
				System.out.println("HTTP Response from Spotify: " + spotifyResponse.statusCode());
				String currentBatchJson = spotifyResponse.body();
				allResultsJobject = JsonParser.parseString(currentBatchJson).getAsJsonObject();
				//writer.append(parsePlaylistsJson(currentBatchJson)); 
				String nextBatchUrl = getNextBatchUrl(currentBatchJson);
				//writer.append("<br>nextBatchUrl=" + nextBatchUrl);
				
				// Keep getting and processing batches until no more "next": 
				while (null != nextBatchUrl && !nextBatchUrl.isEmpty()) {
					outgoingRequest = HttpRequest.newBuilder()
							.uri(URI.create(nextBatchUrl))
				            .header("Authorization", "Bearer " + authToken)
				            .header("Accept", "application/json")
				            .header("Content-Type", contentType)
				            .build();
					spotifyResponse = client.send(outgoingRequest, BodyHandlers.ofString());
					System.out.println("HTTP Response from Spotify: " + spotifyResponse.statusCode());
					currentBatchJson = spotifyResponse.body();
					JsonObject currentBatchJobject = JsonParser.parseString(currentBatchJson).getAsJsonObject();
					JsonArray currentBatchPlaylistsJarray = currentBatchJobject.get("items").getAsJsonArray();
					//System.out.println("sizeof currentBatchPlaylistsJarray=" + currentBatchPlaylistsJarray.size());
					allResultsJobject.get("items").getAsJsonArray().addAll(currentBatchPlaylistsJarray);
					//writer.append(parsePlaylistsJson(currentBatchJson));
					//writer.append("<br>Added batch of size=" + currentBatchPlaylistsJarray.size() + " to allResultsJobject \"items\" JsonArray");
					nextBatchUrl = getNextBatchUrl(currentBatchJson);
					//writer.append("<br>nextBatchUrl=" + nextBatchUrl);
				}
				
				// Look at the final results:
				writer.append(parsePlaylistsJobject(allResultsJobject));
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		} else {
			writer.append("<hr>You left the authToken field blank, we ain't tryin Spotify without an authToken.");
			writer.append("<br>Go to <A href=https://developer.spotify.com/console/get-current-user-playlists/>this Spotify API page for Get a List of Current User's Playlists</a> for a new token. They expire quickly.");
		}
		writer.append("<hr>\r\n")
		  .append("			<form action=\"spotifyclient\" method=\"GET\">\r\n") // Has to match up with web.xml entry!
		  .append("				<input type=\"submit\" value=\"Go back to entry form.\" />\r\n")
		  .append("			</form>\r\n");
		  
		writer.append("		</body>\r\n")
			  .append("</html>\r\n");
	}
	
	String parsePlaylistsJobject(JsonObject playlistsJobject) {
		StringBuilder result = new StringBuilder();
		result.append("<hr>Introspecting the JsonObject . . .");
		try {
			if (null == playlistsJobject) {
				result.append("<br>parsePlaylistJobject received a null playlistsJobject");
				System.out.print("parsePlaylistJobject received a null playlistsJobject");
			} else {
				result.append("<br><br>Retrieving playlists keySet . . .");
				Set<String> plkeys = playlistsJobject.keySet();
				Iterator<String> it = plkeys.iterator();
				while (it.hasNext()) {
					String keyName = it.next();
					if (!keyName.equalsIgnoreCase("items")) {
						JsonElement valElement = playlistsJobject.get(keyName);
						if (null == valElement || valElement.isJsonNull()) {
							result.append("<br>" + keyName + ": JsonNull or null");
						} else {
							//System.out.println("Trying to get element for keyName=" + keyName);
							String valString = valElement.getAsString();
							result.append("<br>" + keyName + ":" + valString);
						}
					} else {
						result.append("<br>" + keyName );
					}
				}
				result.append("<br><br>Retrieving playlists . . .");
				JsonArray playlistArray = playlistsJobject.getAsJsonArray("items");
				Iterator<JsonElement> plit = playlistArray.iterator();
				while (plit.hasNext()) {
					JsonElement plElement = plit.next();
					JsonObject plObject = plElement.getAsJsonObject();
					String plName = plObject.get("name").getAsString();
					result.append("<br>" + plName);
				}
			}
		}
		catch (Exception e) {
			result.append("<hr>Exception parsing playlistsJson");
			result.append("<br>Exception: " + e.getMessage() );
			throw e;
		}

		return result.toString();
	}

	
	String parsePlaylistsJson(String playlistsJson) {
		StringBuilder result = new StringBuilder();
		result.append("<hr>Parsing the JSON into an object . . .");

		JsonObject playlistsJobject = null;
		try {
			playlistsJobject =  JsonParser.parseString(playlistsJson).getAsJsonObject();
			if (null == playlistsJobject) {
				result.append("<br>null JsonObject from playlistsJson=" + playlistsJson);
				System.out.print("null playlistsObject from playlistsJson=" + playlistsJson);
			} else {
				result.append(parsePlaylistsJobject(playlistsJobject));
			}
		}
		catch (Exception e) {
			result.append("<hr>Exception parsing playlistsJson");
			result.append("<br>Exception: " + e.getMessage() );
			throw e;
		}

		return result.toString();
	}
	
	
	String getNextBatchUrl(String currentBatchJson) {
		if (currentBatchJson.isEmpty()) {
			return null;
		} else {
			StringBuilder result = new StringBuilder();
			try {
				JsonObject jobject =  JsonParser.parseString(currentBatchJson).getAsJsonObject();
				JsonElement nextUrlElement = jobject.get("next");
				String nextBatchUrl = nextUrlElement.getAsString();
				result.append(nextBatchUrl);
			}
			catch (Exception e) {
				return null;
			}
			return result.toString();
		}
	}
	
}
