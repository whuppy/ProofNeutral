package org.frdmrt.spotifyclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
			  .append("				contentType: <input size=\"80\" type=\"text\" name=\"contentType\" value=\"text/html\" /><br>\r\n")
			  .append("				clientId : <input size=\"80\" type=\"text\" name=\"clientId\" /><br>\r\n")
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
		String authToken = request.getParameter("authToken");
		String clientId = request.getParameter("clientId");
		String clientSecret = request.getParameter("clientSecret");
		String objectId = request.getParameter("objectId");
		String operationUrl = request.getParameter("operationUrl");
		if ((null == operationUrl)  || operationUrl.trim().isEmpty()) {
			operationUrl = "/me/playlists"; // make sure there's a leading slash!
			// ooh, maybe add it here if the first char isn't '/'
			// also try "/playlists/5yzFco7t9AI5nU2f2jRBXh"
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
		
		// If there's an authToken, the do a Spotify:
		if (authToken != null && !authToken.trim().isEmpty()) {
			// write out some web page:
			writer.append("Hitting Spotify with authToken= " + authToken);
			writer.append("<br>objectId=" + objectId);
			writer.append("<br>client_id=" + clientId);
			writer.append("<br>clientSecret=" + clientSecret);
			
			// set up the URL:
			String spotify_api = "https://api.spotify.com/v1" ; // leave out trailing slash
			String spotifyUrl = spotify_api + operationUrl;
			//System.out.println("spotifyApi before URLencoder=" + spotifyUrl);
			//spotifyUrl = URLDecoder.decode(spotifyUrl, "UTF-8");
			//spotifyUrl = URLEncoder.encode(spotifyUrl, "UTF-8");
			//System.out.println("spotifyApi after URLencodr=" + spotifyUrl);
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
				if (200 == spotifyResponse.statusCode() ) {
					String currentBatchJson = spotifyResponse.body();
					allResultsJobject = JsonParser.parseString(currentBatchJson).getAsJsonObject();
					String nextBatchUrl = getNextBatchUrl(currentBatchJson);
					
					// Keep getting batches and appending to "items" until no more "next": 
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
						JsonArray currentBatchJarray = currentBatchJobject.get("items").getAsJsonArray();
						allResultsJobject.get("items").getAsJsonArray().addAll(currentBatchJarray);
						nextBatchUrl = getNextBatchUrl(currentBatchJson);
					}
					
					// Look at the final results:
					if (operationUrl.contains("me/playlists")) { // Operation was "retrieve all playlists"
						writer.append(parsePlaylistsJobject(allResultsJobject));
						Gson myGson = new GsonBuilder()
								.setPrettyPrinting()
								.create();
						String allResultsPrettyJson = myGson.toJson(allResultsJobject);
						//System.out.println(allResultsPrettyJson);
						writer.append("<hr><pre>" + allResultsPrettyJson + "</pre>");
					}
					else if (spotifyUrl.contains("v1/playlists")) { // Operation was "retrieve a specific playlist"
						if (spotifyUrl.contains("/tracks")) { // Operation was "retrieve tracks from a specific playlist"
							writer.append(parseSinglePlaylistTracks(allResultsJobject));
						}
						else { // Just the playlist and the first 100 tracks
							writer.append(parseSinglePlaylist(allResultsJobject));	
						}
					}
					else { // fall back on just dumping the JSON
						Gson myGson = new GsonBuilder()
								.setPrettyPrinting()
								.create();
						String allResultsPrettyJson = myGson.toJson(allResultsJobject);
						//System.out.println(allResultsPrettyJson);
						writer.append("<hr><pre>" + allResultsPrettyJson + "</pre>");
					}
				}
				else if (401 == spotifyResponse.statusCode() ) {
					writer.append("<hr>HTTP Error 401 Unauthorized<br>");
					writer.append(spotifyResponse.body());
				}
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		} else {
			writer.append("<br>You left the authToken field blank, we ain't tryin Spotify without an authToken.");
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
		result.append("<hr>Introspecting playlistsJobject . . .");
		try {
			if (null == playlistsJobject) {
				result.append("<br>parsePlaylistJobject received a null playlistsJobject");
				System.out.print("parsePlaylistJobject received a null playlistsJobject");
			} else {
				result.append("<br><br>Retrieving playlistsJobject keySet . . .");
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
				result.append("<table>");
				result.append("<tr><th>Name</th><th>ID</th><th>Num</th></tr>");
				while (plit.hasNext()) {
					JsonElement plElement = plit.next();
					JsonObject plObject = plElement.getAsJsonObject();
					String plName = plObject.get("name").getAsString();
					String plId = plObject.get("id").getAsString();
					JsonObject tracksObject = plObject.get("tracks").getAsJsonObject();
					String numTracks = tracksObject.get("total").getAsString();
					result.append("<tr><td>" + plName + "</td><td>" + plId + "</td><td>" + numTracks + "</tr>" );
					
				}
				result.append("</table>");
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
	
	String parseSinglePlaylist(JsonObject singlePlaylistJobject) {
		StringBuilder result = new StringBuilder();
		result.append("<hr>Introspecting singlePlaylistJobject . . .");
		try {
			if (null == singlePlaylistJobject) {
				result.append("<br>parseSinglePlaylist received a null singlePlaylistJobject");
				System.out.print("parseSinglePlaylist received a null singlePlaylistJobject");
			} else {
				result.append("<br><br>Retrieving singlePlaylistJobject keySet . . .");
				Set<String> plkeys = singlePlaylistJobject.keySet();
				Iterator<String> it = plkeys.iterator();
				while (it.hasNext()) {
					String keyName = it.next();
					JsonElement valElement = singlePlaylistJobject.get(keyName);
					if (keyName.equalsIgnoreCase("tracks")) {
						result.append("<br>Skipping \"tracks\" JSON is too long to show here");
					} 
					else if (keyName.equalsIgnoreCase("items")) {
						result.append("<br>\"items\" found. We must be actually looking at a tracks JsonObject");
						result.append(parseSinglePlaylistTracks(singlePlaylistJobject));
					}
					else {
						result.append("<br>" + keyName + ": " + valElement.toString());
						//System.out.println(keyName);
					}
				}
				
				JsonElement tracksElement = singlePlaylistJobject.get("tracks");
				if (null == tracksElement || tracksElement.isJsonNull()) {
					result.append("<br>tracks: JsonNull or null");
				} else {
					JsonObject tracksJobject = tracksElement.getAsJsonObject();
					result.append(parseSinglePlaylistTracks(tracksJobject));
				}
				result.append("<br>. . . done introspecting singlePlaylistJobject");
			}
		}
		catch (Exception e) {
			result.append("<hr>Exception parsing playlistsJson");
			result.append("<br>Exception: " + e.getMessage() );
			throw e;
		}
		
		return result.toString();
	}
	
	String parseSinglePlaylistTracks(JsonObject tracksJobject) {
		StringBuilder result = new StringBuilder();
		result.append("<hr>Introspecting tracksJobject . . .");
		try {
			if (null == tracksJobject) {
				result.append("<br>parseSinglePlaylistTracks received a null tracksJobject");
				System.out.print("parseSinglePlaylistTracks received a null tracksJobject");
			} else {
				result.append("<br><br>Retrieving tracksJobject keySet . . .");
				Set<String> plkeys = tracksJobject.keySet();
				Iterator<String> it = plkeys.iterator();
				while (it.hasNext()) {
					String keyName = it.next();
					JsonElement valElement = tracksJobject.get(keyName);
					if (keyName.equalsIgnoreCase("items")) {
						result.append("<br>" + keyName + " JSON is too long (" + valElement.getAsJsonArray().size() + " items) to show here");
					} 
					else {
						result.append("<br>" + keyName + ": " + valElement.toString());
					}
				}
			}
			result.append("<br>Drilling down into \"items\" . . .");
			JsonElement itemsElement = tracksJobject.get("items");
			if (null == itemsElement || itemsElement.isJsonNull()) {
				result.append("<br>items: JsonNull or null");
			} else {
				JsonArray itemsJarray = itemsElement.getAsJsonArray();
				Iterator<JsonElement> itArIt = itemsJarray.iterator();
				result.append("<table>");
				result.append("<tr><th>Artists[0]</th><th>Name</th><th>Album</th><th>Track ID</th></tr>");
				while (itArIt.hasNext()) {
					JsonObject trackArrayObject = itArIt.next().getAsJsonObject();
					JsonObject actualTrackJobject = trackArrayObject.get("track").getAsJsonObject();
					String atName = actualTrackJobject.get("name").getAsString();
					String atTrackId = null;
					if (actualTrackJobject.get("is_local").getAsString().equalsIgnoreCase("false")) {
						atTrackId = actualTrackJobject.get("id").getAsString();
					}
					else {
						atTrackId = "N/A";
					}
					//String atArtist = actualTrackJobject.get("artist").getAsJsonObject().toString();
					JsonArray artistsJarray = actualTrackJobject.get("artists").getAsJsonArray();
					JsonObject firstArtistJobject = artistsJarray.get(0).getAsJsonObject();
					String atFirstArtistName = firstArtistJobject.get("name").getAsString();
					JsonObject atAlbumJobject = actualTrackJobject.get("album").getAsJsonObject();
					String atAlbumName = atAlbumJobject.get("name").getAsString();
					result.append("<tr><td>" + atFirstArtistName + "</td><td>" + atName + "</td><td>" + atAlbumName + "</td><td>" 
							+ atTrackId + "</td></tr>" );
					//System.out.println(atName);
				}
				result.append("</table>");
			}
			result.append("<br>. . . done introspecting tracksJobject.<br>");
		}
		catch (Exception e) {
			result.append("<hr>Exception parsing tracksJobject");
			result.append("<br>Exception: " + e.getMessage());
			throw e;
		}

		return result.toString();
	}
}
