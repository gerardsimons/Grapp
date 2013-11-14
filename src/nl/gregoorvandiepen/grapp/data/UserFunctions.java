package nl.gregoorvandiepen.grapp.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

	// Testing in localhost using wamp or xampp
	// use http://10.0.2.2/ to connect to your localhost ie http://localhost/
	// private static String loginURL = "localhost:9999/authenticate";
	// Local configuration

	// Special alias to host loopback
	private static String registerURL = "http://vandiepen.org:9999/register";

	private static String login_tag = "login";
	private static String register_tag = "register";

	// constructor
	public UserFunctions() {

	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	/*
	 * public JSONObject loginUser(String email, String password) { // Building
	 * Parameters List<NameValuePair> params = new ArrayList<NameValuePair>();
	 * params.add(new BasicNameValuePair("tag", login_tag)); params.add(new
	 * BasicNameValuePair("email", email)); params.add(new
	 * BasicNameValuePair("password", password)); JSONObject json =
	 * jsonParser.makeHttpRequest(loginURL, JSONRequest.Method.POST, params); //
	 * return json // Log.e("JSON", json.toString()); return json; }
	 */

	/**
	 * function make Login Request
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * */
	public void registerUser(String googleAccount,
			OnJSONParseFinishedListener listener) {
		// Building Parameters

		NameValuePair[] params = new NameValuePair[] { new BasicNameValuePair(
				"googleAccount", googleAccount) };

		new JSONRequest(registerURL, params, listener);
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	/**
	 * Function to logout user Reset Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

}