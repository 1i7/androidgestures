package org.sadko.gestures;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class SettingsActivity extends Activity {
	SharedPreferences settings;
	SeekBar sensitivity;
	SeekBar timeInterval;
	Button discard;
	Button saveAndExit;
	boolean isDiscarding;
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
		timeInterval = (SeekBar)findViewById(R.id.Time_bar);
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
		sensitivity.setProgress(Math.round( (1 - settings
				.getFloat(MotionHandler1.MOTION_SENSITIVITY_STRING, 0.9f) / MotionHandler1.MAX_SENSITIVITY )* 
					sensitivity.getMax()));
		timeInterval.setProgress(Math.round( (1 - (float)settings
				.getLong(MotionHandler1.TIME_INTERVAL_STRING, 1400) / 
					MotionHandler1.MAX_TIME_INTERVAL )* 
					timeInterval.getMax()));
	}

	private void saveSettings(){
		settings.edit().putFloat(MotionHandler1.MOTION_SENSITIVITY_STRING,
					(1 - (float)sensitivity.getProgress() / sensitivity.getMax()) * MotionHandler1.MAX_SENSITIVITY)
				.putLong(MotionHandler1.TIME_INTERVAL_STRING,
						Math.round(MotionHandler1.MAX_TIME_INTERVAL * (1 - (double)timeInterval.getProgress() / 
						timeInterval.getMax())))
				.commit();
		MotionHandler1.MOTION_SENSITIVITY = settings.getFloat(MotionHandler1.MOTION_SENSITIVITY_STRING, 0.9f);
	}

	

}
