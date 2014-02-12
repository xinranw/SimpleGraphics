package edu.upenn.cis350.simplegraphics;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class GameActivity extends Activity {
	private GameView gv;
	private TextView score;
	private TextView deaths;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		gv = (GameView) findViewById(R.id.gameView);
		gv.setParent(this);
		
		score = (TextView) findViewById(R.id.score);
		deaths = (TextView) findViewById(R.id.deaths);
	}

	public void updateScore(int newScore){
		score.setText( " " + newScore);
	}
	
	public void updateDeaths(int newDeathScore){
		deaths.setText(" " + newDeathScore);
	}
	
	public void endGame(){
		finish();
	}
}
