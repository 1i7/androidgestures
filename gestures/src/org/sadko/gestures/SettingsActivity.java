package org.sadko.gestures;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class SettingsActivity extends Activity {
	SharedPreferences settings;
	SeekBar sensitivity;
	SeekBar period;
	Button discard;
	Button saveAndExit;
	Button whatIsIt;
	boolean isDiscarding;
	private static final int DIALOG_ABOUT = 0; 
	@Override
	protected void onStart() {
		isDiscarding = false;
		super.onStart();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		sensitivity = (SeekBar)findViewById(R.id.Sensitivity_bar);
		whatIsIt = (Button) findViewById(R.id.about_settings_button);
		whatIsIt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ABOUT);
				
			}
		});
		period = (SeekBar)findViewById(R.id.Time_bar);
		loadSettings();
		discard = (Button) findViewById(R.id.Discard);
		saveAndExit = (Button) findViewById(R.id.Save);
		discard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isDiscarding = true;
				SettingsActivity.this.finish();
			}
		});
		saveAndExit.setOnClickListener(new OnClickListener() {
			@Override
			
			public void onClick(View v) {
				isDiscarding = false;
				SettingsActivity.this.finish();
			}
		});
		
	}
	
	@Override
	protected void onStop() {
		if (!isDiscarding){
			saveSettings();
		}
		super.onStop();
	}
	
	private void loadSettings(){
		settings = getSharedPreferences(MotionHandler1.PREFERENCE_STRING, 0); 
		sensitivity.setProgress(Math.round( (1 - (settings
				.getFloat(MotionHandler1.MOTION_SENSITIVITY_STRING,
						MotionHandler1.SENSITIVITY_DEFAULT) - 
				MotionHandler1.MIN_SENSITIVITY) / MotionHandler1.MAX_SENSITIVITY ) * 
				sensitivity.getMax()));
		period.setProgress(Math.round( (((float)settings
				.getLong(MotionHandler1.PERIOD_STRING, MotionHandler1.PERIOD_DEFAULT) - MotionHandler1.MIN_PERIOD) / 
					(MotionHandler1.MAX_PERIOD  - MotionHandler1.MIN_PERIOD))* 
					period.getMax()));
	}

	private void saveSettings(){
		settings.edit().putFloat(MotionHandler1.MOTION_SENSITIVITY_STRING,
					(1 - (float)sensitivity.getProgress() / sensitivity.getMax()) * 
					(MotionHandler1.MAX_SENSITIVITY - MotionHandler1.MIN_SENSITIVITY) + MotionHandler1.MIN_SENSITIVITY)
				.putLong(MotionHandler1.PERIOD_STRING,
						Math.round(MotionHandler1.MIN_PERIOD + (MotionHandler1.MAX_PERIOD - MotionHandler1.MIN_PERIOD)* ((double)period.getProgress() / 
								period.getMax())))
				.commit();
		MotionHandler1.SENSITIVITY = settings.getFloat(MotionHandler1.MOTION_SENSITIVITY_STRING, MotionHandler1.SENSITIVITY_DEFAULT);
		MotionHandler1.PERIOD = settings.getLong(MotionHandler1.PERIOD_STRING, MotionHandler1.PERIOD_DEFAULT);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ABOUT){
			return new AlertDialog.Builder(this).setTitle(R.string.about_settings_title)
			.setMessage(R.string.about_settings).setNeutralButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			}).create();
		}
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	

}
