package Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	public enum HttpRequestType{ GET, POST, PATCH };
	
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
	 * Executes the given request to the given url with the given list of Http request headers.
	 * @param reqType The type of Http request to execute.
	 * @param url The URL to send the request to. Cannot be null or an empty string.
	 * @param headers The list of headers to be added to the request.
	 * @return The response for the given request or null if reqType is invalid or if an error occurs during the request.
	 */
	public static CloseableHttpResponse executeRequest(HttpRequestType reqType, String url, Header[] headers){
		assert url!=null && !url.isEmpty();
		HttpUriRequest req;
		switch(reqType){
			case GET: req = new HttpGet(url);
					break;
			case POST: req = new HttpPost(url);
					break;
			case PATCH: req = new HttpPatch(url);
					break;
			default: return null;	//Will never happen
		}
		if(headers!=null){
			for(Header header: headers){
				req.addHeader(header.getKey(), header.getValue());
			}
		}
		try{
			return HttpClients.createDefault().execute(req);
		} catch(IOException e){
			return null;
		}
	}
}
