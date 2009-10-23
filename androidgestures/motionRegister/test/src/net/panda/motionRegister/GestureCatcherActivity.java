package net.panda.motionRegister;

//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;

//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class GestureCatcherActivity extends Activity {
	SensorManagerSimulator mSensorManager;
	int counter = 0;
	SeekBar bar;
	TextView counterRepres;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		Gesture g = (Gesture) i.getSerializableExtra("gesture");
		final GestureCatcher gc = new GestureCatcher(g);
		gc.container = this;
		GestureCatcher.GRAVITY = getSharedPreferences("gConstant", 0).getFloat("g", 0);
		Log.i("gravity set to be", GestureCatcher.GRAVITY+"");
		//mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
		
	    mSensorManager.connectSimulator();
		mSensorManager.registerListener(gc,
				SensorManager.SENSOR_ORIENTATION | SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_UI);
		//mSensorManager.
		setContentView(R.layout.main);
		bar = (SeekBar) findViewById(R.id.SeekBar);
		counterRepres = (TextView) findViewById(R.id.Counter);
		bar.setMax(100);
		bar.setProgress(70);
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gc.SENSITIVITY = (double)progress / 100;
				
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	void increase(){
		counter++;
		counterRepres.setText(""+counter);
	}
	

}
