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
	private Paint p;
	private ArrayList<Point> points;
	private Bitmap image;
	private Point imageLocation;
	private BackgroundTask imageMover;
	private final int IMAGE_WIDTH = 150;
	private final int IMAGE_HEIGHT = 150;
	
	private int viewWidth;
	private int viewHeight;
	
	private final int SLEEP_TIME = 1000;
	

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
		
		imageMover = new BackgroundTask();
		imageMover.execute();
	}

	public void onDraw(Canvas c) {
		imageMover.onPostExecute(""); // running too much
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

	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN
				|| e.getAction() == MotionEvent.ACTION_MOVE) {
			Point pt = new Point();
			pt.x = (int) e.getX();
			pt.y = (int) e.getY();
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

	private void moveImage(){
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();
		imageLocation.x += 50;
		if (imageLocation.x + IMAGE_WIDTH > viewWidth){
			imageLocation.x = 0;
		}
	}
	
	private void loadBgAndImages(){
		setBackgroundResource(R.drawable.space);
		image = BitmapFactory
				.decodeResource(getResources(), R.drawable.unicorn);
		image = Bitmap.createScaledBitmap(image, IMAGE_WIDTH, IMAGE_HEIGHT, false);
		imageLocation = new Point();
		imageLocation.x = 0;
		imageLocation.y = 100;
	}
	
	private void setupPen(){
		p = new Paint();
		p.setColor(Color.RED);
		p.setStrokeWidth(10);
	}
	
	// class that will run in the background
	// <Parameters, Progress Result>
	class BackgroundTask extends AsyncTask<String, Void, String> {
		// automatically called by execute
		protected String doInBackground(String... args) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (Exception e) {
			}
			String reply = "";
			return reply; // this gets sent to onPostExecute
		}

		// automatically called when doInBackground is done
		protected void onPostExecute(String result) {
			// update Views in the UI
			moveImage();
			invalidate();
		}
	}

}