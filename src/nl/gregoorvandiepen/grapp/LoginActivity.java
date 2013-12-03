package nl.gregoorvandiepen.grapp;

import nl.gregoorvandiepen.grapp.data.OnJSONParseFinishedListener;
import nl.gregoorvandiepen.grapp.data.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	private static final int DIALOG_ACCOUNTS = 8234834;

	private Button btnLogin;
	private Button btnLinkToRegister;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView loginErrorMsg;

	private UserFunctions userFunctions;

	private AccountManager mAccountManager;

	private SharedPreferences preferences;

	private String token;

	// SharedPreferences keys
	private static final String TOKEN_KEY = "token";

	// JSON reply keys
	private static final String SUCCESS_KEY = "success";

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

		mAccountManager = AccountManager.get(this);

		token = loadToken();
		if (token == null) {
			Log.i(TAG, "No token found! Register first");
			// btnLogin.setVisibility(View.GONE);
		} else {
			Log.i(TAG, "Token found = " + token);
			Intent i = new Intent(this, DashboardActivity.class);
			startActivity(i); 
		}

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				userFunctions.authenticateUser(token, new OnJSONParseFinishedListener() {
					@Override
					public void jsonReceived(JSONObject JSON) {
						try {
							if (JSON.getBoolean(SUCCESS_KEY)) {
								Log.i(TAG, "Succesfully logged in.");
								Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
								startActivity(intent);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				LoginActivity.this.showDialog(DIALOG_ACCOUNTS);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ACCOUNTS:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select a Google account");
			final Account[] accounts = mAccountManager.getAccountsByType("com.google");
			final int size = accounts.length;
			final String[] names = new String[size];
			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Stuff to do when the account is selected by the user
					registerUser(names[which]);
				}
			});
			return builder.create();
		}
		return null;
	}

	private void storeToken() {
		if (preferences != null) {
			preferences.edit().putString(TOKEN_KEY, token).commit();
		}
	}

	private String loadToken() {
		if (preferences != null) {
			return preferences.getString(TOKEN_KEY, null);
		} else
			return null;
	}

	private void registerUser(String googleAccount) {
		Log.d(TAG, "Trying to register user " + googleAccount);
		userFunctions.registerUser(googleAccount, new OnJSONParseFinishedListener() {
			@Override
			public void jsonReceived(JSONObject json) {
				try {
					String token = json.getString("token");
					Log.i(TAG, "Received token = " + token);
					// Store token
					LoginActivity.this.token = token;
					storeToken();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}