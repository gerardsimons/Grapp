package nl.gregoorvandiepen.grapp;

import java.io.IOException;
import nl.gregoorvandiepen.grapp.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;

public class DashboardActivity extends Activity implements OnPreparedListener, MediaController.MediaPlayerControl {
    private static final String TAG = "MainActivity";

    public static final String AUDIO_FILE_NAME = "audioFileName";

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    private ProgressBar streamProgress;

    //private final String testMP3 = "http://www.simons-software.com/grapp/audio/test.mp3";
    //private final String testMP3 = "http://gregoorvandiepen.nl/grapp/audio/test.mp3";
    private final String testMP3 = "http://vandiepen.org:81/grapp/node-server/grappen/0edae502408c6c13aea49acbcc3c2ebfd7fd7472.3gp";

    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_main);

	loadMediaPlayer();

	//OVerride 
	mediaController = new MediaController(this) {
	    @Override
	    public void hide() {
		//Do not hide.
	    }
	};

	streamProgress = (ProgressBar) findViewById(R.id.streamProgress);

	mediaController.show();
    }

    private void loadMediaPlayer() {
	mediaPlayer = new MediaPlayer();
	try {
	    mediaPlayer.setDataSource(testMP3);
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
    }

    @Override
    protected void onStop() {
	super.onStop();
	mediaController.hide();
	mediaPlayer.stop();
	mediaPlayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	//the MediaController will hide after 3 seconds - tap the screen to make it appear again
	mediaController.show();
	return false;
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
	mediaController.setMediaPlayer(this);
	mediaController.setAnchorView(findViewById(R.id.mediaControllerRoot));

	handler.post(new Runnable() {
	    public void run() {
		mediaController.setEnabled(true);
		mediaController.show(0);

		streamProgress.setVisibility(View.GONE);
	    }
	});
    }

    @Override
    public int getAudioSessionId() {
	// TODO Auto-generated method stub
	return 0;
    }
}