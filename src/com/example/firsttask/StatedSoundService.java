package com.example.firsttask;

import static com.example.firsttask.PlayerStateEnum.IDLE;
import static com.example.firsttask.PlayerStateEnum.PAUSED;
import static com.example.firsttask.PlayerStateEnum.PLAYING;
import static com.example.firsttask.PlayerStateEnum.PREPARING;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class StatedSoundService extends Service implements OnPreparedListener, OnCompletionListener {

	private static final String FILE_PATH = "http://freedownloads.last.fm/download/195571279/Stay.mp3";

	private PlayerBinder binder = new PlayerBinder();

	private MediaPlayer mediaPlayer = null;
	private PlayerStateEnum currentState = IDLE;
	private OnStateChangeListener onStateChangeListener;

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public void onDestroy() {
		changeState(IDLE);
		mediaPlayer.setOnPreparedListener(null);
		mediaPlayer.setOnCompletionListener(null);
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mediaPlayer != null && currentState == IDLE) {
			try {
				changeState(PREPARING);
				mediaPlayer.setDataSource(FILE_PATH);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				throw new RuntimeException("Exception while get source " + FILE_PATH);
			}
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		changeState(PAUSED);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		changeState(PAUSED);
		stopSelf();
	}

	public PlayerStateEnum getState() {
		return currentState;
	}

	private void changeState(PlayerStateEnum newState) {
		if (currentState != newState) {
			currentState = newState;
			if (onStateChangeListener != null) {
				onStateChangeListener.onStateChange(currentState);
			}
		}
	}

	public void play() throws IllegalStateException {
		if (mediaPlayer != null && currentState == PAUSED) {
			changeState(PLAYING);
			mediaPlayer.start();
		}
	}

	public void pause() throws IllegalStateException {
		if (mediaPlayer != null && currentState == PLAYING) {
			changeState(PAUSED);
			mediaPlayer.pause();
		}
	}


	public void setOnStateChangeListener(OnStateChangeListener listener) {
		this.onStateChangeListener = listener;
	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		if (mediaPlayer != null) {
			mediaPlayer.setOnBufferingUpdateListener(listener);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class PlayerBinder extends Binder {

		public StatedSoundService getService() {
			return StatedSoundService.this;
		}
	}

	public interface OnStateChangeListener {
		void onStateChange(PlayerStateEnum state);
	}
}
