package nl.gregoorvandiepen.grapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.gregoorvandiepen.grapp.R;
import nl.gregoorvandiepen.grapp.data.Joke;
import nl.gregoorvandiepen.grapp.data.OnJSONParseFinishedListener;
import nl.gregoorvandiepen.grapp.data.UserFunctions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListenActivity extends Activity implements OnPreparedListener, OnCompletionListener{
    private static final String TAG = "MainActivity";
 
    // JSON reply keys
 	private static final String SUCCESS_KEY = "success";
 	
    private MediaPlayer mediaPlayer;
    
    private List<Joke> jokes;

	private String token;
	private JSONArray jokesJSONArray;

	private View activePlayButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.activity_listen);
    	jokes = new ArrayList<Joke>();
    	mediaPlayer = new MediaPlayer();
    	Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString(LoginActivity.TOKEN_KEY);
        if(token == null) {
            //Something went wrong
            Log.e(TAG,"Could not get the user's token.");
            //finish();
        } else {
            Log.d(TAG,"The user token was succesfully received : " + token);
        }
    	UserFunctions.listJokes(token, 0, 10, new OnJSONParseFinishedListener() {
            @Override
            public void jsonReceived(JSONObject JSON) {
                try {
                    if (JSON.getBoolean(SUCCESS_KEY)) {
                        Log.i(TAG, "Jokelist received.");
                        jokesJSONArray = JSON.getJSONArray("result");
                    }
                    else {
                        Log.e(TAG,"List jokes failed.");
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"Jokelist JSON exception.",e);
                }
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	
                        	for (int i = 0; i< jokesJSONArray.length(); i++){
                        		try {
									jokes.add(new Joke(jokesJSONArray.getJSONObject(i)));
								} catch (JSONException e) {
									e.printStackTrace();
								}
                        	}
                        	
                        	final ListView listview = (ListView) findViewById(R.id.joke_list);
                        	final JokeArrayAdapter adapter = new JokeArrayAdapter(ListenActivity.this, jokes);
                        	listview.setAdapter(adapter);
                        }
                    });
                }
            }
        });
	}

	public class JokeArrayAdapter extends ArrayAdapter<Joke> {
		private final Context context;
		private final List<Joke> jokes;
		
		public JokeArrayAdapter(Context context, List<Joke> jokes) {
			super(context, R.layout.joke_row, jokes);
			this.context = context;
			this.jokes = jokes;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.joke_row, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.joke_row_name);
			Joke joke = jokes.get(position);
			textView.setText(joke.getTitle());
			rowView.findViewById(R.id.joke_row_id).setTag(position);
			View playButton = rowView.findViewById(R.id.joke_row_play_button);
			playButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (activePlayButton != null){
						activePlayButton.setSelected(false);
					}
					activePlayButton = v;
					activePlayButton.setSelected(true);
					RelativeLayout rl = (RelativeLayout)v.getParent();
					int pos = (Integer) rl.getTag();
					UserFunctions.listenToJoke(token, jokes.get(pos).getUniqid(), new OnJSONParseFinishedListener() {
                        @Override
                        public void jsonReceived(JSONObject JSON) {
                                Log.d(TAG,"JSON Response Received!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    	Toast.makeText(ListenActivity.this,"Grap is aan history toegevoegd.", Toast.LENGTH_SHORT).show();
                                    }
                            });
                        }
                    });
					loadMediaPlayer(jokes.get(pos).getURL());
				}
			});
			View likeButton = rowView.findViewById(R.id.joke_row_like_button);
			likeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					final RelativeLayout rl = (RelativeLayout)v.getParent();
					int pos = (Integer) rl.getTag();
					UserFunctions.likeJoke(token, jokes.get(pos).getUniqid(), 1, new OnJSONParseFinishedListener() {
                        @Override
                        public void jsonReceived(JSONObject JSON) {
                                Log.d(TAG,"JSON Response Received!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    	rl.findViewById(R.id.joke_row_dislike_button).setVisibility(View.INVISIBLE);
                                        Toast.makeText(ListenActivity.this,"Grap is geliked.", Toast.LENGTH_SHORT).show();
                                    }
                            });
                        }
                    });
				}
			});
			View dislikeButton = rowView.findViewById(R.id.joke_row_dislike_button);
			dislikeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					final RelativeLayout rl = (RelativeLayout)v.getParent();
					int pos = (Integer) rl.getTag();
					UserFunctions.likeJoke(token, jokes.get(pos).getUniqid(), 0, new OnJSONParseFinishedListener() {
                        @Override
                        public void jsonReceived(JSONObject JSON) {
                                Log.d(TAG,"JSON Response Received!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    	rl.findViewById(R.id.joke_row_like_button).setVisibility(View.INVISIBLE);
                                        Toast.makeText(ListenActivity.this,"Grap is gedisliked.", Toast.LENGTH_SHORT).show();
                                    }
                            });
                        }
                    });
				}
			});
			return rowView;
		}
	}
	
	public void clickMicrophone(View v){
        Intent intent = new Intent(ListenActivity.this, RecordActivity.class);
        intent.putExtra(LoginActivity.TOKEN_KEY, token);
        startActivity(intent);
    }
	
    private void loadMediaPlayer(String URL) {
    	if (mediaPlayer != null){
    		if (mediaPlayer.isPlaying()){
    			mediaPlayer.pause();
    		}
    	}
    	mediaPlayer = new MediaPlayer();
    	try {
    		mediaPlayer.setDataSource(URL);
    		mediaPlayer.prepareAsync();
    	} catch (IllegalArgumentException e1) {
    		e1.printStackTrace();
    	} catch (SecurityException e1) {
    		e1.printStackTrace();
    	} catch (IllegalStateException e1) {
    		e1.printStackTrace();
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    	mediaPlayer.setOnPreparedListener(this);
    	mediaPlayer.setOnCompletionListener(this);
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
    	mediaPlayer.start();
    }

    public void pause() {
    	mediaPlayer.pause();
    }

    public int getDuration() {
    	return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
    	return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
		mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
    	return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
    	return 0;
    }

    public boolean canPause() {
    	return true;
    }

    public boolean canSeekBackward() {
    	return true;
    }

    public boolean canSeekForward() {
    	return true;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
    	Log.d(TAG, "onPrepared");
    	start();
    }

	@Override
	public void onCompletion(MediaPlayer mp) {
		activePlayButton.setSelected(false);
	}
}