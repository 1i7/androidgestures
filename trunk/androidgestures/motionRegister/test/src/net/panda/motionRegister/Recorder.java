package net.panda.motionRegister;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */

//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;
//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;
//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;
//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;
//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;
//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;



//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class Recorder extends Activity {
	private boolean needOn = false;
	private ServiceConnection con;
	// private ListnerBinder lb;
	public static final String RESULT_CONTENT_VALUES_NAME = "org.sadko.gestures.Recorder/val";
	long timeIntervalMillis = 100;
	Button start;
	static final int BEGIN = 0;
	static final int RECORD = 1;
	static final int END = 2;
	recordListener r = new recordListener();
	SensorManager mSensorManager;
	int stage = BEGIN;
	SeekBar sb;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		start = (Button) findViewById(R.id.Button01);
		start.setTextSize(50);
		float [] R = new float[9];
		float [] I = new float[9];
		float [] gravity = new float [3];
		gravity[2] = SensorManager.GRAVITY_EARTH;
		float [] geomagnetic = new float[3];
		geomagnetic[1] = SensorManager.MAGNETIC_FIELD_EARTH_MAX;
		SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				Log.i("matrix", R[i*3+j]+" !");
			}
		}
		
		start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (stage) {
				case BEGIN: {
					stage = RECORD;
					start.setText("Stop");
					break;
				}
				case RECORD: {
					stage = END;
					start.setText("Start");
					break;
				}
				}

			}
		});
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		 //mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);

//	        mSensorManager.connectSimulator();
		mSensorManager.registerListener(r, SensorManager.SENSOR_ORIENTATION
				| SensorManager.SENSOR_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_UI);
	}

	// @Override
	/*
	 * protected void onPause() { super.onPause(); if(needOn)lb.mh.switchMe();
	 * lb.mh.deleteListener(lb.ms); unbindService(con);
	 * 
	 * }
	 */
	// @Override
	/*
	 * protected void onResume() { con=new ServiceConnection(){ public void
	 * onServiceConnected(ComponentName arg0, IBinder arg1) { lb=
	 * (ListnerBinder) arg1; if(lb.mh.isEnabled){ needOn=true; lb.mh.switchMe();
	 * }
	 * 
	 * }
	 * 
	 * public void onServiceDisconnected(ComponentName arg0) {
	 * 
	 * 
	 * } };
	 * 
	 * bindService(new Intent(this,MotionHandler1.class),con,0);
	 * 
	 * super.onResume(); }
	 */
	private class recordListener implements SensorListener {
		int ARRAY_SIZE = 10;

		// double[][] maxMatrix;
		long[] times = new long[ARRAY_SIZE];
		protected double accelerations[][] = new double[ARRAY_SIZE][3];
		// /protected double ay[] = new double[ARRAY_SIZE];
		// protected double az[] = new double[ARRAY_SIZE];
		int position = 0;

		public void onAccuracyChanged(int sensor, int accuracy) {
		}

		private void increase() {
			ARRAY_SIZE *= 2;
			long[] newt = new long[ARRAY_SIZE];
			System.arraycopy(times, 0, newt, 0, position + 1);
			times = newt;
			double[][] newaccs = new double[ARRAY_SIZE][3];
			System.arraycopy(accelerations, 0, newaccs, 0, position + 1);
			accelerations = newaccs;
			/*
			 * double[] newr = new double[ARRAY_SIZE]; System.arraycopy(ay, 0,
			 * newr, 0, position + 1); ay = newr; double[] newp = new
			 * double[ARRAY_SIZE]; System.arraycopy(az, 0, newp, 0, position +
			 * 1); az = newp;
			 */

		}

		private double yaw;
		private double pitch;
		private double roll;
		private final double TOO_SMALL_ACCELS = 0.0001;

		public void onSensorChanged(int sensor, float[] values) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				yaw = values[0] * Math.PI / 180;
				pitch = values[1] * Math.PI / 180;
				roll = values[2] * Math.PI / 180;
				Log.i("ORIENT", "" + values[0] + ' ' + values[1] + ' '
						+ values[2]);
				return;
			}
			if (stage == RECORD) {
				if (position == ARRAY_SIZE - 1)
					increase();
				GestureCatcher.substractG(values, accelerations[position], yaw,
						pitch, roll);
				/*if (accelerations[position][0] * accelerations[position][0]
						+ accelerations[position][1]
						* accelerations[position][1]
						+ accelerations[position][2]
						* accelerations[position][2] < TOO_SMALL_ACCELS)
					return;*/
				times[position] = System.currentTimeMillis();
				Log.i("accels", "" + values[0] + ' ' + values[1] + ' '
						+ values[2]);
				Log.i("sens " + position, "" + accelerations[position][0]
						+ "\t" + accelerations[position][1] + "\t"
						+ accelerations[position][2] + "\t" + times[position]
						+ "\n");
				position++;
			}
			if (stage == END) {
				// double[][] matrix;
				// long time = 0;
				// double matrixNorm = 0;
				// maxMatrix = new double[3][3];
				// maxMatrix[1][1] = maxMatrix[2][2] = maxMatrix[0][0] = 1;
				// double maxMatrixNorm = 0;
				Gesture g = new Gesture();
				g.timeIntervalMillis = timeIntervalMillis;
				Log.i("size",
						((times[position] - times[0]) / timeIntervalMillis)
								+ " ");
				g.readingsSequence = new double[(int) ((times[position - 1] - times[0]) / timeIntervalMillis)][3];
				g.readingsSequence[0] = accelerations[0];
				int lastWrittenPosition = 0;
				int positionInGesture = 0;
				for (int i = 1; i < position; i++) {
					if (times[i] - times[lastWrittenPosition] >= timeIntervalMillis) {
						g.readingsSequence[positionInGesture++] = accelerations[i];
						lastWrittenPosition = i;
					}

				}
				double[][] newReads = new double[positionInGesture + 1][3];
				System.arraycopy(g.readingsSequence, 0, newReads, 0,
						positionInGesture + 1);
				g.readingsSequence = newReads;
				// g.save();
				Intent intent = new Intent(Recorder.this,
						GestureCatcherActivity.class);
				intent.putExtra("gesture", g);
				startActivity(intent);
				mSensorManager.unregisterListener(this);
				Recorder.this.finish();
				position = 0;
				stage = BEGIN;

			}

		}

	}
}
