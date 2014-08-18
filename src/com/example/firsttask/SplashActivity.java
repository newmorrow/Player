package com.example.firsttask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class SplashActivity extends Activity implements OnClickListener {

	private static final int SPLASH_TIME = 2000;

	private final Handler handler = new Handler();
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			startHomeActivity();
		}
	};

	private boolean wasShown = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_splash_activity);

		if (savedInstanceState == null) {
			View contentView = findViewById(android.R.id.content);
			contentView.setOnClickListener(this);
		} else { //In case of recreating - don't show splash again
			startHomeActivity();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!wasShown) {
			wasShown = true;
			handler.postDelayed(runnable, SPLASH_TIME);
		} else { //In case of resume after pause - don't show splash again
			startHomeActivity();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}

	@Override
	public void onClick(View v) {
		startHomeActivity();
	}

	private void startHomeActivity() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		SplashActivity.this.finish();
	}
}
