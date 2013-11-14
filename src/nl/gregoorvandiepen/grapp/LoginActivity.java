package nl.gregoorvandiepen.grapp;

import nl.gregoorvandiepen.grapp.R;
import nl.gregoorvandiepen.grapp.data.DatabaseHandler;
import nl.gregoorvandiepen.grapp.data.OnJSONParseFinishedListener;
import nl.gregoorvandiepen.grapp.data.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements
		OnJSONParseFinishedListener {
	private static final String TAG = LoginActivity.class.getSimpleName();

	private Button btnLogin;
	private Button btnLinkToRegister;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView loginErrorMsg;

	private UserFunctions userFunctions;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.loginEmail);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		userFunctions = new UserFunctions();

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// String email = inputEmail.getText().toString();
				// String password = inputPassword.getText().toString();
				// new MyAsyncTask().execute(email, password);
			}
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				userFunctions.registerUser("gjcsimons@gmail.com",
						LoginActivity.this);
			}
		});
	}

	@Override
	public void jsonReceived(JSONObject JSON) {
		Log.i(TAG, "Received JSON response : " + JSON.toString());
	}
}