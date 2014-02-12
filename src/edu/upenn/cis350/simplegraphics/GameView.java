package edu.upenn.cis350.simplegraphics;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Handles all gameplay functionality. Keeps track of the score and communicates
 * with its parentActivity to update the score displays.
 * 
 * @author Xinran
 * 
 */
public class GameView extends View {
	private GameActivity parentActivity;
	private Paint pen;
	private Bitmap image;
	private Point imageLocation;
	private double imageAngle;
	private int imageVelocity;
	private Integer viewWidth;
	private Integer viewHeight;
	private boolean hasFinishedBgTask;
	private ArrayList<Point> points;	// Cache of user interaction locations
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

		// Set up images and pen
		loadBgAndImages();
		setupPen();
		score = 0;
		deaths = 0;

		points = new ArrayList<Point>();

		// Start to move the image
		new BackgroundTask().execute();
		hasFinishedBgTask = false;

	}

	/**
	 * Load the background image and the moving image
	 */
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

	/**
	 * Set up the pen's properties
	 */
	private void setupPen() {
		pen = new Paint();
		pen.setColor(Color.RED);
		pen.setStrokeWidth(10);
	}

	/**
	 * Set the image's initial location and angle of movement. This method is
	 * called everytime the image is reset.
	 */
	private void setImageStartLocationAndAngle() {
		imageLocation.x = -IMAGE_WIDTH;
		imageLocation.y = (int) Math.round(Math.random() * 200 + 200);
		imageAngle = Math.random() * Math.PI / 3.0 - Math.PI / 6.0;
	}

	public void onDraw(Canvas c) {
		if (hasFinishedBgTask) {
			hasFinishedBgTask = false;
			new BackgroundTask().execute();
		}
		c.drawBitmap(image, imageLocation.x, imageLocation.y, null);

		if (points.isEmpty()) {
			return;
		}
		Point p1 = points.get(0);
		for (Point pt : points) {
			c.drawLine(p1.x, p1.y, pt.x, pt.y, pen);
			p1 = pt;
		}
	}

	/**
	 * Responds to user touch events. If the user touches or moves his finger,
	 * check for image collisions and store the locations. If the user removes
	 * his finger, clear the cache of touch locations. After both, force a
	 * redraw with invalidate().
	 */
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN
				|| e.getAction() == MotionEvent.ACTION_MOVE) {
			int x = (int) e.getX();
			int y = (int) e.getY();

			// Check for image intersection
			if (intersectsImage(x, y)) {
				increaseVelocity();
				setImageStartLocationAndAngle();
				updateScore();
			}

			Point pt = new Point(x, y);
			points.add(pt);
			invalidate();
			return true;
		} else if (e.getAction() == MotionEvent.ACTION_UP) {
			points.clear();
			invalidate();
			return true;
		}
		return false;
	}

	/**
	 * Checks if a specified pair of coordinates overlaps with the current image
	 * location
	 * 
	 * @param x
	 *            - the x coordinate
	 * @param y
	 *            - the y coordinate
	 * @return - whether the given point overlaps with the current image
	 *         location
	 */
	private boolean intersectsImage(int x, int y) {
		if ((imageLocation.x <= x) && (imageLocation.x + IMAGE_WIDTH >= x)
				&& (imageLocation.y <= y)
				&& (imageLocation.y + IMAGE_HEIGHT >= y)) {
			return true;
		}
		return false;
	}

	/**
	 * Increases the image's movement velocity
	 */
	private void increaseVelocity() {
		imageVelocity = imageVelocity * 6 / 5;
	}

	/**
	 * Prompts the parent activity to update the score in the GUI
	 */
	private void updateScore() {
		score++;
		parentActivity.updateScore(score);
		if (score >= 5) {
			endGame(true);
		}
	}

	/**
	 * Prompts the parent activity to update the deaths in the GUI
	 */
	private void updateDeaths() {
		deaths++;
		parentActivity.updateDeaths(deaths);
		if (deaths == 3) {
			endGame(false);
		}
	}

	/**
	 * Ends the activity/game after showing a short won/loss message.
	 * 
	 * @param won
	 *            - input that specifies whether the game was won or lost
	 */
	private void endGame(boolean won) {
		if (won) {
			Toast.makeText(parentActivity, "Congrats, you win!",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(parentActivity, "You lose!", Toast.LENGTH_SHORT)
					.show();
		}
		parentActivity.endGame();
	}

	public void setParent(GameActivity parent) {
		this.parentActivity = parent;
	}

	/**
	 * A thread that runs the background to move the image. Calls invalidate()
	 * to force a screen redraw
	 */
	class BackgroundTask extends AsyncTask<Void, Void, String> {
		// automatically called by execute
		protected String doInBackground(Void... args) {
			hasFinishedBgTask = false;
			Log.i("System.out", "running");
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
			hasFinishedBgTask = true;
		}
	}

	/**
	 * Moves the image. Checks if the images moves off the screen for scoring
	 */
	private void moveImage() {
		if (viewWidth == null || viewHeight == null) {
			viewWidth = this.getWidth();
			viewHeight = this.getHeight();
		}
		imageLocation.x += (int) imageVelocity * Math.cos(imageAngle);
		imageLocation.y += (int) imageVelocity * Math.sin(imageAngle);
		boolean imageOffScreen = (imageLocation.x + IMAGE_WIDTH > viewWidth)
				|| (imageLocation.y <= 0)
				|| (imageLocation.y + IMAGE_HEIGHT > viewHeight);
		if (imageOffScreen) {
			updateDeaths();
			setImageStartLocationAndAngle();
		}
	}

}