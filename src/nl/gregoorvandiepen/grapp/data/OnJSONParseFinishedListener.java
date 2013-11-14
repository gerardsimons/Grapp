package nl.gregoorvandiepen.grapp.data;

import org.json.JSONObject;

public interface OnJSONParseFinishedListener {

	public void jsonReceived(JSONObject JSON);
}
