package nl.gregoorvandiepen.grapp.data;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;

public abstract class UserFunctions {

	// Testing in localhost using wamp or xampp
	// use http://10.0.2.2/ to connect to your localhost ie http://localhost/
	// private static String loginURL = "localhost:9999/authenticate";
	// Local configuration

	// Special alias to host loopback
	private static final String registerURL = "http://vandiepen.org:9999/register";
	private static final String loginURL = "http://vandiepen.org:9999/authenticate";
    private static final String uploadURL = "http://vandiepen.org:9999/upload";

    private static final String TAG = "UserFunctions";

	private static String login_tag = "login";
	private static String register_tag = "register";


	public static void registerUser(String googleAccount, OnJSONParseFinishedListener listener) {
		// Building Parameters

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
}