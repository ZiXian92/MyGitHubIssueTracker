package Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

/**
 * Defines the utility class of commonly-used methods in this application.
 * @author ZiXian92
 */
public class Util {
	//Output formatting
	private static final String ITEM_DELIM = ", ";
	
	/**
	 * List of allowed Http requests for this class.
	 */
	private enum HttpRequestType{ GET, POST, PATCH };
	
	/**
	 * Reads the JSON string from the message body of the given HTTP response.
	 * @param in The input stream of the HTTP response's message body. Cannot be null.
	 * @return The JSON string contained in the given message body or whatever is read if an error occurs.
	 */
	public static String getJSONString(InputStream in){
		assert in!=null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder strBuilder = new StringBuilder();
		String input;
		try{
			while((input = reader.readLine())!=null){
				strBuilder = strBuilder.append(input);
			}
			reader.close();
		} catch(IOException e){
			
		}
		return strBuilder.toString();
	}
	
	/**
	 * Converts the given list of strings into a string.
	 * @param list The list of strings to be converted.
	 * @return The output formatted string representation of the given list or an empty string if
	 * 			list is null or empty.
	 */
	public static String convertToString(ArrayList<String> list){
		int numElem;
		if(list==null || (numElem = list.size())==0){
			return "";
		}
		StringBuilder strBuilder = new StringBuilder(list.get(0));
		for(int i=1; i<numElem; i++){
			strBuilder = strBuilder.append(ITEM_DELIM).append(list.get(i));
		}
		return strBuilder.toString();
	}
	
	/**
	 * Sends a Http GET request to the given URL.
	 * @param url The URL of the API method to call. Cannot be null.
	 * @param authCode The Base64-encoded string of username and password to be used for basic authentication.
	 * 					Cannot be null.
	 * @throws IOException If an error occurred during the request.
	 * */
	public static CloseableHttpResponse sendGetRequest(String url, String authCode) throws IOException{
		return sendRequest(HttpRequestType.GET, url, authCode, null);
	}
	
	/**
	 * Sends a Http request to GitHub API using the given paramters.
	 * @param reqType The type of Http request to be sent. Cannot be null.
	 * @param url The URL of the API method to be called. Cannot be null.
	 * @param authCode The Base64-encoded string of username and password for basic authentication.
	 * @param msg The message entity to be used in POST or PATCH requests.
	 * @return The corresponding Http response for the request.
	 * @throws IOException If an error occurred during the request.
	 */
	private static CloseableHttpResponse sendRequest(HttpRequestType reqType,
			String url, String authCode, HttpEntity msg) throws IOException {
		assert reqType!=null && url!=null && authCode!=null;
		HttpUriRequest req;
		switch(reqType){
			case GET: req = new HttpGet(url);
					break;
			case POST: req = new HttpPost(url);
					if(msg!=null){
						((HttpPost)req).setEntity(msg);
					}
					break;
			case PATCH: req = new HttpPatch(url);
					if(msg!=null){
						((HttpPatch)req).setEntity(msg);
					}
					break;
			default: return null;	//will not happen
		}
		req.addHeader(Constants.HEADER_ACCEPT, Constants.VAL_ACCEPT);
		req.addHeader(Constants.HEADER_AUTH, String.format(Constants.VAL_AUTH, authCode));
		return HttpClients.createDefault().execute(req);
	}
}
