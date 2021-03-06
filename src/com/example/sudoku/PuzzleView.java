package com.example.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View {
	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private final Rect selRect = new Rect();
	private static final String TAG = "Sudoku";
	private final Game game;
	Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint hint_or_selected = new Paint();
	Rect r = new Rect();
	Paint background = new Paint();
	Paint dark = new Paint();
	Paint hilite = new Paint();
	Paint light = new Paint();
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x * width + width), (int) (y * height + height));
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		int widthCopy = getWidth();
		int heightCopy = getHeight();
		
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, widthCopy, heightCopy, background);
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		light.setColor(getResources().getColor(R.color.puzzle_light));
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * height, widthCopy, i * height, light);
			canvas.drawLine(0, i * height + 1, widthCopy, i * height + 1, hilite);
			canvas.drawLine(i * width, 0, i * width, heightCopy, light);
			canvas.drawLine(i * width + 1, 0, i * width + 1, heightCopy, hilite);
		}
		for (int i = 0; i < 9; i++) {
			if (i % 3 != 0)
				continue;
			canvas.drawLine(0, i * height, widthCopy, i * height, dark);
			canvas.drawLine(0, i * height + 1, widthCopy, i * height + 1, hilite);
			canvas.drawLine(i * width, 0, i * width, heightCopy, dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1,heightCopy, hilite);
		}
		foreground.setColor(getResources().getColor( R.color.puzzle_foreground) );
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = foreground.getFontMetrics();
		float x = width / 2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
			}
		}
		if (Prefs.getHints(getContext())) {
			int c[] = { 
					getResources().getColor(R.color.puzzle_hint_0), 
					getResources().getColor(R.color.puzzle_hint_1), 
					getResources().getColor(R.color.puzzle_hint_2), 
			};
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					int movesleft = 9 - game.getUsedTiles(i, j).length;
					if (movesleft < c.length) {
						getRect(i, j, r);
						hint_or_selected.setColor(c[movesleft]);
						canvas.drawRect(r, hint_or_selected);
					}
				}
			}
		}
		Log.d(TAG, "selRect=" + selRect);
		hint_or_selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, hint_or_selected);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event="
		+ event);
		switch (keyCode) {
			// D-PAD INPUT
			case KeyEvent.KEYCODE_DPAD_UP: select(selX, selY - 1);	break;
			case KeyEvent.KEYCODE_DPAD_DOWN: select(selX, selY + 1); break;
			case KeyEvent.KEYCODE_DPAD_LEFT: select(selX - 1, selY); break;
			case KeyEvent.KEYCODE_DPAD_RIGHT: select(selX + 1, selY); break;
			// KEYBOARD INPUT
			case KeyEvent.KEYCODE_0:
			case KeyEvent.KEYCODE_SPACE: setSelectedTile(0); break;
			case KeyEvent.KEYCODE_1: setSelectedTile(1); break;
			case KeyEvent.KEYCODE_2: setSelectedTile(2); break;
			case KeyEvent.KEYCODE_3: setSelectedTile(3); break;
			case KeyEvent.KEYCODE_4: setSelectedTile(4); break;
			case KeyEvent.KEYCODE_5: setSelectedTile(5); break;
			case KeyEvent.KEYCODE_6: setSelectedTile(6); break;
			case KeyEvent.KEYCODE_7: setSelectedTile(7); break;
			case KeyEvent.KEYCODE_8: setSelectedTile(8); break;
			case KeyEvent.KEYCODE_9: setSelectedTile(9); break;
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_CENTER: game.showKeypadOrError(selX, selY); break;
			
			default: return super.onKeyDown(keyCode, event);
		}
		return true;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		select((int) (event.getX() / width), (int) (event.getY() / height));
		game.showKeypadOrError(selX, selY);
		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}
	
	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}
	
	public void setSelectedTile(int tile) {
		if (game.setTileIfValid(selX, selY, tile)) {
			invalidate();// may change hints
		} else {
			// Number is not valid for this tile
			Log.d(TAG, "setSelectedTile: invalid: " + tile);
			startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
		}		
	}
}