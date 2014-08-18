package com.example.firsttask;

import static com.example.firsttask.PlayerStateEnum.IDLE;
import static com.example.firsttask.PlayerStateEnum.PAUSED;
import static com.example.firsttask.PlayerStateEnum.PLAYING;
import static com.example.firsttask.PlayerStateEnum.PREPARING;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.firsttask.StatedSoundService.OnStateChangeListener;
import com.example.firsttask.StatedSoundService.PlayerBinder;

public class HomeActivity extends Activity implements OnStateChangeListener, OnBufferingUpdateListener {

	private StatedSoundService soundService;
	private Button playButton;
	private ProgressDialog progressDialog;

	private final ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			soundService.setOnStateChangeListener(null);
			soundService.setOnBufferingUpdateListener(null);
			soundService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			soundService = ((PlayerBinder) binder).getService();
			updateContent(soundService.getState());

			soundService.setOnStateChangeListener(HomeActivity.this);
			soundService.setOnBufferingUpdateListener(HomeActivity.this);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_home_activity);
		initButton();
		initProgressDialog();
		setTitle(getResources().getText(IDLE.getNameId()));

		Intent serviceIntent = new Intent(this, StatedSoundService.class);
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);
		startService(serviceIntent);
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getText(PREPARING.getNameId()));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		progressDialog.setCancelable(false);
	}

	private void initButton() {
		playButton = (Button) findViewById(R.id.home_button);
		playButton.setEnabled(false);
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerStateEnum state = soundService.getState();
				if (state == PLAYING) {
					soundService.pause();
				} else if (state == PAUSED) {
					soundService.play();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	private void updateContent(PlayerStateEnum state) {
		playButton.setEnabled(state.isReadyToPlay());
		if (state.isReadyToPlay()) {
			Resources resources = getResources();
			if (state == PLAYING) {
				playButton.setText(resources.getText(R.string.pause_text));
			} else if (state == PAUSED) {
				playButton.setText(resources.getText(R.string.play_text));
			}
		} else if (state == PREPARING) {
			progressDialog.show();
		}
		setTitle(getResources().getText(state.getNameId()));
    }

	@Override
	public void onStateChange(PlayerStateEnum state) {
		updateContent(state);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer player, int percent) {
		progressDialog.setProgress(percent);
		if (percent == progressDialog.getMax()) {
			soundService.setOnBufferingUpdateListener(null);
			progressDialog.dismiss();
		}
	}
}
