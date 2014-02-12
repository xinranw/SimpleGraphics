package edu.upenn.cis350.simplegraphics;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private GameView gv;
	private TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gv = (GameView) findViewById(R.id.gameView);
		gv.setParent(this);
		
		tv = (TextView) findViewById(R.id.scoreboard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void updateScore(int newScore){
		tv.setText( " " + newScore);
	}
}
