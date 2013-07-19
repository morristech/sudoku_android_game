package com.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			// insert continue button action
		
			case R.id.new_button:
				openNewGameDialog();
				break;
		
			case R.id.about_button:
				Intent i = new Intent(this, About.class);
				startActivity(i);
				break;
			
			case R.id.exit_button:
				finish();
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, Prefs.class));
				return true;
		}
		return false;
	}
	
	private static final String TAG = "New Game Option";
	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.new_game_title)
			.setItems(R.array.difficulty,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface,int i) {
					startGame(i);
				}
			})
			.show();
	}
	private void startGame(int i) {
		if(i == 0) {
			Log.d(TAG, "Easy");
		} else if( i == 1 ) {
			Log.d(TAG, "Medium");
		} else if( i == 2 ) {
			Log.d(TAG, "Hard");
		}  
	}
	
}
