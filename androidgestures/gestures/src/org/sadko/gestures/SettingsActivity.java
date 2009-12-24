package org.sadko.gestures;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.settings_layout);
		final SeekBar sensitivity = (SeekBar)findViewById(R.id.sensitivity_bar);
		final SeekBar timeInterval = (SeekBar)findViewById(R.id.time_interval_bar);
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
				MotionHandler1.changeSensitivity( (double)progress / sensitivity.getMax() * 2);
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
				MotionHandler1.changeTimeInterval(Math.round(2000 * (1 - (double)progress / timeInterval.getMax())));
			}
		});
		super.onCreate(savedInstanceState);
	}

	

}
