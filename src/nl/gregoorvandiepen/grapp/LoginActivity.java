package nl.gregoorvandiepen.grapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.gregoorvandiepen.grapp.data.OnJSONParseFinishedListener;
import nl.gregoorvandiepen.grapp.data.UserFunctions;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	private static final int DIALOG_ACCOUNTS = 8234834;

	private AccountManager mAccountManager;

	private SharedPreferences preferences;

	private String token;

	// SharedPreferences keys, also used for intent data passing
	public static final String TOKEN_KEY = "token";

	// JSON reply keys
	private static final String SUCCESS_KEY = "success";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        Log.i(TAG,"*************************");
        Log.i(TAG,"* LoginActivity Created *");
        Log.i(TAG,"*************************");

        preferences = this.getSharedPreferences("nl.gregoorvandiepen.grapp", Context.MODE_PRIVATE);

		mAccountManager = AccountManager.get(this);

        //Automatically try to authenticate
        authenticate();

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
	}

    private void authenticate() {
        token = loadToken();
        if (token == null) { //No token found, so try to register (even though a token does not exist,
            // this does not necessarily mean the user is not yet registered,
            // it may also be due to a reinstall
            Log.i(TAG, "No token found! Register first");
            LoginActivity.this.showDialog(DIALOG_ACCOUNTS);
            // btnLogin.setVisibility(View.GONE);
        } else { //Login
            Log.i(TAG, "Token found = " + token);
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Logging in...", true, false);
            dialog.show();
            UserFunctions.authenticateUser(token, new OnJSONParseFinishedListener() {
                @Override
                public void jsonReceived(JSONObject JSON) {
                    try {
                        if (JSON.getBoolean(SUCCESS_KEY)) {
                            LoginActivity.this.token = token;
                            storeToken();
                            Log.i(TAG, "Successfully logged in.");
                            //startRecordActivity();
                            startListenActivity();
                        }
                        else {
                            //Login failed
                            //Toast.makeText(LoginActivity.this,"Log in failed.",Toast.LENGTH_SHORT).show();  // Deze zorgde voor een window leak
                            Log.e(TAG,"Login failed.");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,"Login JSON exception.",e);
                    }
                    finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.hide();
                            }
                        });
                    }
                }
            });
        }
    }

    private void startListenActivity() {
        Intent intent = new Intent(LoginActivity.this, ListenActivity.class);
        intent.putExtra(TOKEN_KEY,token);
        startActivity(intent);
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
		} else {
            Log.e(TAG,"Could not store preferences.");
        }
	}

	private String loadToken() {
		if (preferences != null) {
			return preferences.getString(TOKEN_KEY, null);
		} else
            Log.i(TAG,"No user token found.");
			return null;
	}

	private void registerUser(String googleAccount) {
		Log.d(TAG, "Trying to register user " + googleAccount);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Logging in...", true, false);
        dialog.show();
		UserFunctions.registerUser(googleAccount, new OnJSONParseFinishedListener() {
			@Override
			public void jsonReceived(JSONObject json) {
				try {
                    if(json.getBoolean(SUCCESS_KEY)) {
                    	JSONArray tokenJSONArray = json.getJSONArray("token");
					    String token = tokenJSONArray.getJSONObject(0).getString("token");
					    Log.i(TAG, "Received token = " + token);
					    // Store token
                        LoginActivity.this.token = token;
					    storeToken();
					    startListenActivity();
                    }
                    else {
                        Log.e(TAG,"Registration failed.");
                        Toast.makeText(LoginActivity.this,"Registration failed.",Toast.LENGTH_SHORT).show();
                    }
				} catch (JSONException e) {
					Log.e(TAG,"Register JSON Response Exception",e);
				}
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                        }
                    });
                }
			}
		});
	}
}