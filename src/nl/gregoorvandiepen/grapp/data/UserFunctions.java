package nl.gregoorvandiepen.grapp.data;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;

public abstract class UserFunctions {

	private static final String registerURL 	= "http://vandiepen.org:9999/register";
	private static final String loginURL 		= "http://vandiepen.org:9999/authenticate";
    private static final String uploadURL 		= "http://vandiepen.org:9999/upload";
    private static final String listJokesURL 	= "http://vandiepen.org:9999/listJokes";
    private static final String listenToJokeURL = "http://vandiepen.org:9999/listenToJoke";
    private static final String likeJokeURL = "http://vandiepen.org:9999/likeJoke";

    private static final String TAG = "UserFunctions";

	public static void registerUser(String googleAccount, OnJSONParseFinishedListener listener) {
		NameValuePair[] params = new NameValuePair[] { new BasicNameValuePair("google", googleAccount) };

		JSONRequest requester = new JSONRequest(registerURL, listener);
		requester.makeHttpRequest(params);
	}

	public static void authenticateUser(String token, OnJSONParseFinishedListener listener) {
		NameValuePair[] params = new NameValuePair[] { new BasicNameValuePair("token", token) };

		JSONRequest requester = new JSONRequest(loginURL, listener);
		requester.makeHttpRequest(params);
	}

    public static void uploadJoke(String token, String title, File file, OnJSONParseFinishedListener listener) {
        Log.d(TAG,"Trying to upload joke to the server.");

        NameValuePair[] params = new NameValuePair[] { new BasicNameValuePair("token", token), new BasicNameValuePair("title",title) };

        JSONRequest requester = new JSONRequest(uploadURL, listener);
        requester.makeFileHttpRequest(file, params);
    }

    public static void listJokes(String token, int lowerLimit, int upperLimit, OnJSONParseFinishedListener listener) {
        Log.d(TAG,"Trying to list jokes from the server.");

        NameValuePair[] params = new NameValuePair[] {
        		new BasicNameValuePair("token", token),
        		new BasicNameValuePair("lowerLimit", Integer.toString(lowerLimit)),
        		new BasicNameValuePair("upperLimit", Integer.toString(upperLimit))
        	};

        JSONRequest requester = new JSONRequest(listJokesURL, listener);
        requester.makeHttpRequest(params);
    }
    
    public static void listenToJoke(String token, String uniqid, OnJSONParseFinishedListener listener) {
        Log.d(TAG,"Trying to insert joke into history.");

        NameValuePair[] params = new NameValuePair[] {
        		new BasicNameValuePair("token", token),
        		new BasicNameValuePair("uniqid", uniqid)
        	};

        JSONRequest requester = new JSONRequest(listenToJokeURL, listener);
        requester.makeHttpRequest(params);
    }
    
    public static void likeJoke(String token, String uniqid, int likes, OnJSONParseFinishedListener listener) {
        Log.d(TAG,"Trying to (dis)like joke.");

        NameValuePair[] params = new NameValuePair[] {
        		new BasicNameValuePair("token", token), 
        		new BasicNameValuePair("uniqid", uniqid),
        		new BasicNameValuePair("likes", Integer.toString(likes))
        	};

        JSONRequest requester = new JSONRequest(likeJokeURL, listener);
        requester.makeHttpRequest(params);
    }
}