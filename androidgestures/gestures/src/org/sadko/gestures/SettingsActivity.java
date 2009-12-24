package org.sadko.gestures;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity {
	SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.settings_layout);
		final SeekBar sensitivity = (SeekBar)findViewById(R.id.Sensitivity_bar);
		final SeekBar timeInterval = (SeekBar)findViewById(R.id.Time_bar);
		settings = getSharedPreferences(MotionHandler1.preferencesString, 0);
		sensitivity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				settings.edit().putFloat(MotionHandler1.MOTION_SENSITIVITY_STRING,
						(float)progress / sensitivity.getMax() * 2)
						.commit();
		
			}
		});
		timeInterval.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				settings.edit().putLong(MotionHandler1.TIME_INTERVAL_STRING,
						Math.round(2000 * (1 - (double)progress / timeInterval.getMax())))
						.commit();
			}
		});
		super.onCreate(savedInstanceState);
	}

	

}
