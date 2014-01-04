package nl.gregoorvandiepen.grapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import nl.gregoorvandiepen.grapp.data.OnJSONParseFinishedListener;
import nl.gregoorvandiepen.grapp.data.UserFunctions;

public class RecordActivity extends Activity {

    private static final String TAG = "RecordActivity";

    private Button recordButton;
    private Button saveButton;

    private MediaRecorder mRecorder;

    private boolean recording = false;

    private File recordFile;

    //Used to store the file locally
    private final String fileName = "joke.3gp";
    private String jokeTitle = null;
    private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record);

        Log.d(TAG,"********************************************");
        Log.d(TAG,"*          RecordActivity Started          *");
        Log.d(TAG,"********************************************");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString(LoginActivity.TOKEN_KEY);
        if(token == null) {
            //Something went wrong
            Log.e(TAG,"Could not get the user's token.");
            finish();
        } else {
            Log.d(TAG,"The user token was succesfully received : " + token);
        }

        saveButton = (Button) findViewById(R.id.saveRecording);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!recording) {
                    if(jokeTitle != null) {
                        final ProgressDialog dialog = ProgressDialog.show(RecordActivity.this,"","Grap Uploaden...",false);
                        //Save the recording and upload to the server
                        UserFunctions.uploadJoke(token,jokeTitle,recordFile,new OnJSONParseFinishedListener() {
                            @Override
                            public void jsonReceived(JSONObject JSON) {
                                    Log.d(TAG,"JSON Response Received!");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.hide();
                                            Toast.makeText(RecordActivity.this,"Grap is geüpload.",Toast.LENGTH_SHORT).show();
                                        }
                                });
                                boolean success = false;
                                try {
                                    success = JSON.getBoolean("success");
                                    if(success) {
                                        Log.i(TAG,"Succesfully uploaded joke file to server, Joke ID = " + JSON.getString("joke_id"));
                                    }
                                    else {
                                        Log.e(TAG,"Server Error when uploading recording.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(RecordActivity.this,"ERROR: Geen titel opgegeven.",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.e(TAG,"Android is still recording...?");
                }
            }
        });

        recordButton = (Button)findViewById(R.id.recordBttn);
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Record audio

                if(!recording) { //Currently not recording, so record now
                    recordButton.setText("Stop");
                    resetRecorder();
                    mRecorder.start();
                    recording = true;
                    saveButton.setVisibility(View.INVISIBLE);
                    Log.i(TAG,"Start recording");
                }
                else { //Currently recording, stop the recording
                    mRecorder.stop();
                    //Reveal save button
                    recordButton.setText("Record");
                    Log.i(TAG,"Stop recording");
                    recording = false;
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });

        final EditText titleText = (EditText) findViewById(R.id.jokeTitleEditText);
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String newJokeTitle = titleText.getText().toString().trim();
                if(newJokeTitle != "") {
                    RecordActivity.this.jokeTitle = newJokeTitle;
                }
            }
        });

        titleText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int keyCode,
                                          KeyEvent event) {
                if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) &&
                        (keyCode           == KeyEvent.KEYCODE_ENTER)   )
                {
                    // hide virtual keyboard
                    InputMethodManager imm =
                            (InputMethodManager)RecordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(titleText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
	}

    private void resetRecorder() {
        //Setup recorder
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        //Create the file, making sure it already exists
        recordFile = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        try {
            recordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.setOutputFile(recordFile.getPath());
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Recorder Setup Error", e);
            //No use continuing if this fails
            finish();
        }
    }
    
    public void clickSpeaker(View v){
        Intent intent = new Intent(this, ListenActivity.class);
        intent.putExtra(LoginActivity.TOKEN_KEY, token);
        startActivity(intent);
    }

}
