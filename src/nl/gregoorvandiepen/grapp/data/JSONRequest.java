package nl.gregoorvandiepen.grapp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JSONRequest {

	private static final String TAG = JSONRequest.class.getSimpleName();
	private OnJSONParseFinishedListener delegate;
	private String url;

	// constructor
	public JSONRequest(String url, OnJSONParseFinishedListener delegate) {
		this.delegate = delegate;
		this.url = url;
	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public void makeHttpRequest(NameValuePair[] params) {

		HTTPTask asyncTask = new HTTPTask();
		asyncTask.execute(params);
	}

	private class HTTPTask extends AsyncTask<NameValuePair, Void, Void> {

		protected Void doInBackground(NameValuePair... params) {

			// Making HTTP request
			try {

				// request method is POST
				// defaultHttpClient
				HttpClient httpClient = new DefaultHttpClient();

				Log.d(TAG, "URL = " + url);
				HttpPost httpPost = new HttpPost(url);
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (NameValuePair nvp : params) {
					paramList.add(nvp);
				}

				httpPost.setEntity(new UrlEncodedFormEntity(paramList));

				// Execute
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				// Read the response
				InputStream is = httpEntity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();

				Log.i(TAG, "Server response : " + sb.toString());
				String json = sb.toString();

				delegate.jsonReceived(new JSONObject(json));
				// delegate.jsonReceived(result);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// return JSON String
			return null;
		}

	}
}