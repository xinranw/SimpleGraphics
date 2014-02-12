package edu.upenn.cis350.simplegraphics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * Handles behavior for the home screen. Handles interactions with the
 * GameActivity. Provides methods for GUI buttons.
 * 
 * @author Xinran
 * 
 */
public class MainActivity extends Activity {
	public static final int GameActivity_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public void onNewGameButtonClick(View v) {
		Intent i = new Intent(this, GameActivity.class);
		startActivityForResult(i, GameActivity_ID);
	}

	public void onQuitButtonClick(View v) {
		finish();
	}
}
