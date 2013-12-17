package nl.gregoorvandiepen.grapp.data;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JSONRequest {

	private static final String TAG = JSONRequest.class.getSimpleName();
	private OnJSONParseFinishedListener delegate;
	private String url;

    private File file;

	// constructor
	public JSONRequest(String url, OnJSONParseFinishedListener delegate) {
		this.delegate = delegate;
		this.url = url;
	}

	// function get json from url
	// by making HTTP POST or GET method
	public void makeHttpRequest(NameValuePair[] params) {

		HTTPPostTask asyncTask = new HTTPPostTask();
		asyncTask.execute(params);
	}

    public void makeFileHttpRequest(File file, NameValuePair[] params) {
        //Store globally so the http task can access it
        this.file = file;
        HTTPFilePostTask asyncTask = new HTTPFilePostTask();
        asyncTask.execute(params);
    }

    private class HTTPFilePostTask extends AsyncTask<NameValuePair, Void, Void> {
        @Override
        protected Void doInBackground(NameValuePair... nameValuePairs) {

            // Making HTTP request
            try {

                // request method is POST
                // defaultHttpClient
                HttpClient httpClient = new DefaultHttpClient();

                Log.d(TAG, "URL = " + url);
                HttpPost httpPost = new HttpPost(url);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                FileBody fb = new FileBody(file);
                builder.addPart("upload", fb);
                for(NameValuePair nvp : nameValuePairs) {
                    builder.addTextBody(nvp.getName(),nvp.getValue());
                }

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                // Execute
                Log.i(TAG,"Executing HTTP Post request.");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                Log.i(TAG,"HTTP Response Received.");
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
                Log.e(TAG,"JSONFileHTTPRequest",e);
            } catch (ClientProtocolException e) {
                Log.e(TAG,"JSONFileHTTPRequest",e);
            } catch (IOException e) {
                Log.e(TAG,"JSONFileHTTPRequest",e);
            } catch (JSONException e) {
                Log.e(TAG,"JSONFileHTTPRequest",e);
            }

            // return JSON String
            Log.d(TAG,"End of HTTPPostFileTask");
            return null;
        }
    }


	private class HTTPPostTask extends AsyncTask<NameValuePair, Void, Void> {
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
                Log.e(TAG,"JSONHTTPRequest",e);
			} catch (ClientProtocolException e) {
                Log.e(TAG,"JSONHTTPRequest",e);
			} catch (IOException e) {
                Log.e(TAG,"JSONHTTPRequest",e);
			} catch (JSONException e) {
                Log.e(TAG,"JSONHTTPRequest",e);
			}
			// return JSON String
			return null;
		}

	}
}