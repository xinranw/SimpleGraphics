package edu.upenn.cis350.simplegraphics;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	private MainActivity parentActivity;
	
	private Paint p;
	private ArrayList<Point> points;
	private Bitmap image;
	private Point imageLocation;
	private double imageAngle;
	private int imageVelocity;
	private int viewWidth;
	private int viewHeight;
	private boolean hasFinished;
	private int score;
	private int deaths;

	private final int IMAGE_WIDTH = 150;
	private final int IMAGE_HEIGHT = 150;
	private final int SLEEP_TIME = 50;

	public GameView(Context context) {
		super(context);
		init();
	}

	public GameView(Context context, AttributeSet as) {
		super(context, as);
		init();
	}

	private void init() {
		setWillNotDraw(false);
		loadBgAndImages();
		setupPen();

		points = new ArrayList<Point>();

		new BackgroundTask().execute();
		hasFinished = false;
		score = 0;
		deaths = 0;
	}

	public void onDraw(Canvas c) {
		if (hasFinished) {
			hasFinished = false;
			new BackgroundTask().execute();
		}
		c.drawBitmap(image, imageLocation.x, imageLocation.y, null);

		if (points.isEmpty()) {
			return;
		}
		Point p1 = points.get(0);
		for (Point pt : points) {
			c.drawLine(p1.x, p1.y, pt.x, pt.y, p);
			p1 = pt;
		}
	}

	public void setParent(MainActivity parent){
		this.parentActivity = parent;
	}
	
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN
				|| e.getAction() == MotionEvent.ACTION_MOVE) {
			int x = (int) e.getX();
			int y = (int) e.getY();
			if (intersectsImage(x, y)){
				increaseVelocity();
				setImageStartLocationAndAngle();
				updateScore();
			}
				
			Point pt = new Point();
			pt.x = x;
			pt.y = y;
			points.add(pt);
			invalidate(); // force redraw
			return true;
		} else if (e.getAction() == MotionEvent.ACTION_UP) {
			points.clear();
			invalidate();
			return true;
		}
		return false;
	}

	private void loadBgAndImages() {
		setBackgroundResource(R.drawable.space);
		image = BitmapFactory
				.decodeResource(getResources(), R.drawable.unicorn);
		image = Bitmap.createScaledBitmap(image, IMAGE_WIDTH, IMAGE_HEIGHT,
				false);
		imageLocation = new Point();
		setImageStartLocationAndAngle();
		imageVelocity = 20;
	}

	private void setImageStartLocationAndAngle() {
		imageLocation.x = -IMAGE_WIDTH;
		imageLocation.y = (int) Math.round(Math.random() * 200 + 200);
		imageAngle = Math.random() * Math.PI / 3.0 - Math.PI / 6.0;
	}

	private void setupPen() {
		p = new Paint();
		p.setColor(Color.RED);
		p.setStrokeWidth(10);
	}

	private void moveImage() {
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();
		imageLocation.x += (int) imageVelocity * Math.cos(imageAngle);
		imageLocation.y += (int) imageVelocity * Math.sin(imageAngle);
		if (imageLocation.x + IMAGE_WIDTH > viewWidth) {
			deaths++;
			setImageStartLocationAndAngle();
		}
	}

	private boolean intersectsImage(int x, int y) {
		if ((imageLocation.x <= x) && (imageLocation.x + IMAGE_WIDTH >= x)
				&& (imageLocation.y <= y)
				&& (imageLocation.y + IMAGE_HEIGHT >= y)) {
			return true;
		}
		return false;
	}
	
	private void updateScore(){
		score++;
		parentActivity.updateScore(score);
	}
	
	private void increaseVelocity(){
		imageVelocity = imageVelocity * 6 / 5;
	}

	// class that will run in the background
	// <Parameters, Progress, Result>
	class BackgroundTask extends AsyncTask<Void, Void, String> {
		// automatically called by execute
		protected String doInBackground(Void... args) {
			hasFinished = false;
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
			}
			return ""; // this gets sent to onPostExecute
		}

		// automatically called when doInBackground is done
		protected void onPostExecute(String result) {
			// update Views in the UI
			moveImage();
			invalidate();
			hasFinished = true;
		}
	}

}