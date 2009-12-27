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
package org.sadko.gestures;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Recorder extends Activity {
	private boolean needOn=false;
	//private ServiceConnection con;
	//private ListnerBinder lb;
	public static final String RESULT_CONTENT_VALUES_NAME="org.sadko.gestures.Recorder/val";
	public static final double THRESHOLD_DEVIATION = 1.0; 
	/*public static double[][] math(double yaw2, double pitch2, double roll2,
			double yaw, double pitch, double roll) {

		double[][] ans = new double[3][3];
		ans[0][0] = (double) (Math.sin(roll2) * Math.cos(pitch2)
				* Math.sin(roll) * Math.cos(pitch) + Math.sin(roll2)
				* Math.sin(yaw2) * Math.sin(pitch2) * Math.sin(roll)
				* Math.sin(yaw) * Math.sin(pitch) + Math.sin(roll2)
				* Math.cos(yaw2) * Math.sin(pitch2) * Math.sin(yaw)
				* Math.cos(roll) + Math.sin(roll2) * Math.cos(yaw2)
				* Math.sin(pitch2) * Math.sin(roll) * Math.cos(yaw)
				* Math.sin(pitch) + Math.sin(yaw2) * Math.cos(roll2)
				* Math.sin(roll) * Math.cos(yaw) * Math.sin(pitch)
				- Math.sin(roll2) * Math.sin(yaw2) * Math.sin(pitch2)
				* Math.cos(yaw) * Math.cos(roll) + Math.sin(yaw2)
				* Math.cos(roll2) * Math.sin(yaw) * Math.cos(roll)
				+ Math.cos(yaw2) * Math.cos(roll2) * Math.cos(yaw)
				* Math.cos(roll) - Math.cos(yaw2) * Math.cos(roll2)
				* Math.sin(roll) * Math.sin(yaw) * Math.sin(pitch));
		ans[0][1] = (double) (Math.sin(roll2) * Math.cos(pitch2)
				* Math.sin(pitch) - Math.sin(roll2) * Math.cos(pitch)
				* Math.sin(yaw) * Math.sin(yaw2) * Math.sin(pitch2)
				- Math.cos(pitch) * Math.cos(yaw) * Math.sin(yaw2)
				* Math.cos(roll2) - Math.sin(roll2) * Math.cos(pitch)
				* Math.cos(yaw) * Math.cos(yaw2) * Math.sin(pitch2) + Math
				.cos(pitch)
				* Math.sin(yaw) * Math.cos(yaw2) * Math.cos(roll2));
		ans[0][2] = (double) (-Math.sin(roll2) * Math.cos(pitch2)
				* Math.cos(pitch) * Math.cos(roll) - Math.sin(pitch2)
				* Math.sin(roll2) * Math.sin(yaw2) * Math.cos(yaw)
				* Math.sin(roll) - Math.sin(pitch2) * Math.sin(roll2)
				* Math.sin(yaw2) * Math.sin(pitch) * Math.cos(roll)
				* Math.sin(yaw) - Math.sin(yaw2) * Math.cos(roll2)
				* Math.sin(pitch) * Math.cos(roll) * Math.cos(yaw)
				+ Math.cos(yaw2) * Math.cos(roll2) * Math.cos(yaw)
				* Math.sin(roll) + Math.sin(yaw2) * Math.cos(roll2)
				* Math.sin(roll) * Math.sin(yaw) - Math.sin(pitch2)
				* Math.cos(yaw2) * Math.sin(roll2) * Math.sin(pitch)
				* Math.cos(roll) * Math.cos(yaw) + Math.sin(pitch2)
				* Math.cos(yaw2) * Math.sin(roll2) * Math.sin(roll)
				* Math.sin(yaw) + Math.cos(yaw2) * Math.cos(roll2)
				* Math.sin(pitch) * Math.cos(roll) * Math.sin(yaw));
		ans[1][0] = (double) (-Math.cos(pitch2) * Math.sin(yaw2)
				* Math.sin(roll) * Math.sin(yaw) * Math.sin(pitch)
				+ Math.cos(pitch2) * Math.sin(yaw2) * Math.cos(yaw)
				* Math.cos(roll) - Math.cos(pitch2) * Math.cos(yaw2)
				* Math.sin(roll) * Math.cos(yaw) * Math.sin(pitch)
				- Math.cos(pitch2) * Math.cos(yaw2) * Math.sin(yaw)
				* Math.cos(roll) + Math.sin(pitch2) * Math.sin(roll)
				* Math.cos(pitch));
		ans[1][1] = (double) (Math.cos(pitch2) * Math.sin(yaw2)
				* Math.cos(pitch) * Math.sin(yaw) + Math.cos(pitch2)
				* Math.cos(yaw2) * Math.cos(pitch) * Math.cos(yaw) + Math
				.sin(pitch2)
				* Math.sin(pitch));
		ans[1][2] = (double) (Math.cos(pitch2) * Math.sin(yaw2)
				* Math.sin(pitch) * Math.cos(roll) * Math.sin(yaw)
				+ Math.cos(pitch2) * Math.sin(yaw2) * Math.cos(yaw)
				* Math.sin(roll) + Math.cos(pitch2) * Math.cos(yaw2)
				* Math.sin(pitch) * Math.cos(roll) * Math.cos(yaw)
				- Math.cos(pitch2) * Math.cos(yaw2) * Math.sin(roll)
				* Math.sin(yaw) - Math.sin(pitch2) * Math.cos(pitch)
				* Math.cos(roll));
		ans[2][0] = (double) (-Math.cos(pitch2) * Math.cos(roll2)
				* Math.sin(roll) * Math.cos(pitch) + Math.sin(roll2)
				* Math.cos(yaw2) * Math.cos(yaw) * Math.cos(roll)
				+ Math.sin(roll2) * Math.sin(yaw2) * Math.sin(roll)
				* Math.cos(yaw) * Math.sin(pitch) - Math.sin(pitch2)
				* Math.cos(roll2) * Math.cos(yaw2) * Math.sin(yaw)
				* Math.cos(roll) + Math.sin(roll2) * Math.sin(yaw2)
				* Math.sin(yaw) * Math.cos(roll) - Math.sin(pitch2)
				* Math.cos(roll2) * Math.sin(yaw2) * Math.sin(roll)
				* Math.sin(yaw) * Math.sin(pitch) - Math.sin(pitch2)
				* Math.cos(roll2) * Math.cos(yaw2) * Math.sin(roll)
				* Math.cos(yaw) * Math.sin(pitch) - Math.sin(roll2)
				* Math.cos(yaw2) * Math.sin(roll) * Math.sin(yaw)
				* Math.sin(pitch) + Math.sin(pitch2) * Math.cos(roll2)
				* Math.sin(yaw2) * Math.cos(yaw) * Math.cos(roll));
		ans[2][1] = (double) (-Math.cos(pitch2) * Math.cos(roll2)
				* Math.sin(pitch) - Math.sin(roll2) * Math.cos(pitch)
				* Math.cos(yaw) * Math.sin(yaw2) + Math.cos(pitch)
				* Math.sin(yaw) * Math.sin(pitch2) * Math.cos(roll2)
				* Math.sin(yaw2) + Math.sin(roll2) * Math.cos(pitch)
				* Math.sin(yaw) * Math.cos(yaw2) + Math.cos(pitch)
				* Math.cos(yaw) * Math.sin(pitch2) * Math.cos(roll2)
				* Math.cos(yaw2));
		ans[2][2] = (double) (Math.cos(pitch2) * Math.cos(roll2)
				* Math.cos(pitch) * Math.cos(roll) + Math.sin(roll2)
				* Math.cos(yaw2) * Math.sin(pitch) * Math.cos(roll)
				* Math.sin(yaw) + Math.sin(pitch2) * Math.cos(roll2)
				* Math.sin(yaw2) * Math.cos(yaw) * Math.sin(roll)
				+ Math.sin(roll2) * Math.cos(yaw2) * Math.cos(yaw)
				* Math.sin(roll) - Math.sin(pitch2) * Math.cos(roll2)
				* Math.cos(yaw2) * Math.sin(roll) * Math.sin(yaw)
				- Math.sin(roll2) * Math.sin(yaw2) * Math.sin(pitch)
				* Math.cos(roll) * Math.cos(yaw) + Math.sin(pitch2)
				* Math.cos(roll2) * Math.cos(yaw2) * Math.sin(pitch)
				* Math.cos(roll) * Math.cos(yaw) + Math.sin(pitch2)
				* Math.cos(roll2) * Math.sin(yaw2) * Math.sin(pitch)
				* Math.cos(roll) * Math.sin(yaw) + Math.sin(roll2)
				* Math.sin(yaw2) * Math.sin(roll) * Math.sin(yaw));
		return ans;

	}*/

	Button start;
	static final int BEGIN = 0;
	static final int RECORD = 1;
	static final int END = 2;
	private static final int DIALOG_BAD_GESTURE = 0;
	recordListener r = new recordListener();
	SensorManager mSensorManager;
	int stage = BEGIN;
	SeekBar sb;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		TextView instruction=(TextView)findViewById(R.id.instruction_record);
		TextView instruction2=(TextView)findViewById(R.id.instruction_record_2);
		start = (Button) findViewById(R.id.Button01);
		start.setTextSize(50);
		start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (stage) {
				case BEGIN: {
					stage = RECORD;
					start.setBackgroundResource(R.drawable.record_gest_button_down);
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
		mSensorManager = (SensorManager)getSystemService( SENSOR_SERVICE);//(SensorManager)getSystemService(SENSOR_SERVICE);
		//mSensorManager.connectSimulator();
		//Hardware.mContentResolver=getContentResolver();
		//mSensorManager= new SensorManagerSimulator((SensorManager)
		//getSystemService(SENSOR_SERVICE));
		//SensorManagerSimulator.connectSimulator();
		mSensorManager.registerListener(r,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(r,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//if(needOn)lb.mh.switchMe();
		//lb.mh.deleteListener(lb.ms);
		//unbindService(con);

	}
	@Override
	protected void onResume() {

		super.onResume();
	}
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_BAD_GESTURE:
			return new AlertDialog.Builder(this).setTitle(
					"Bad gesture").setMessage(R.string.bad_gesture_text).setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();

						}
					}).create();
		}
		return null;
	}
	
	private class recordListener implements SensorEventListener {
		int ARRAY_SIZE = 10;

		float[][] maxMatrix;
		long[] times = new long[ARRAY_SIZE];
		//protected double yaws[] = new double[ARRAY_SIZE];
		//protected double rolls[] = new double[ARRAY_SIZE];
		//protected double pitchs[] = new double[ARRAY_SIZE];
		protected float magnetics[][] = new float[ARRAY_SIZE][3];
		protected float accels[][] = new float[ARRAY_SIZE][3];
		protected float accels_last[] = new float[3];
		int position_magnetic = 0;
		

		public void onAccuracyChanged(int sensor, int accuracy) {
		}

		private void increase() {
			ARRAY_SIZE *= 2;
			long[] newt = new long[ARRAY_SIZE];
			System.arraycopy(times, 0, newt, 0, position_magnetic + 1);
			times = newt;
			float[][] newm = new float[ARRAY_SIZE][3];
			System.arraycopy(magnetics, 0, newm, 0, position_magnetic + 1);
			magnetics = newm;
			float[][] newa = new float[ARRAY_SIZE][3];
			System.arraycopy(accels, 0, newa, 0, position_magnetic + 1);
			accels = newa;

		}
		boolean flag = false;
		public void onSensorChanged(SensorEvent event) {
			if (stage == RECORD) {
				if (position_magnetic == ARRAY_SIZE - 1 )
					increase();
				if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
					accels_last[0] = event.values[0];
					accels_last[1] = event.values[1];
					accels_last[2] = event.values[2];
					flag = true;
				}
				if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
					magnetics[position_magnetic][0] = event.values[0];
					magnetics[position_magnetic][1] = event.values[1];
					magnetics[position_magnetic][2] = event.values[2];
					accels[position_magnetic][0] = accels_last[0];
					accels[position_magnetic][1] = accels_last[1];
					accels[position_magnetic][2] = accels_last[2];
					times[position_magnetic] = event.timestamp / 1000000;
					if(flag != false){
						position_magnetic++;
						flag = false;
					}
				}

			}
			if (stage == END) {
				float[][] matrix;
				long time = 0;
				double matrixNorm = 0;
				maxMatrix = new float[3][3];
				maxMatrix[1][1] = maxMatrix[2][2] = maxMatrix[0][0] = 1;
				double maxMatrixNorm = 0;
				for (int i = 1; i < position_magnetic; i++) {
					matrix = math_2(accels[0], magnetics[0], accels[i], magnetics[i]);
					matrixNorm = (matrix[0][0] - 1) * (matrix[0][0] - 1)
							+ matrix[0][1] * matrix[0][1] + matrix[0][2]
							* matrix[0][2] + matrix[1][0] * matrix[1][0]
							+ (matrix[1][1] - 1) * (matrix[1][1] - 1)
							+ matrix[1][2] * matrix[1][2] + matrix[2][0]
							* matrix[2][0] + matrix[2][1] * matrix[2][1]
							+ (matrix[2][2] - 1) * (matrix[2][2] - 1);
					if (matrixNorm > maxMatrixNorm) {
						maxMatrix = matrix;
						time = times[i] - times[0];
						maxMatrixNorm = matrixNorm;
					}
				}
				if(maxMatrixNorm < THRESHOLD_DEVIATION){
					Log.i("recorded norm", matrixNorm + "");
					showDialog(DIALOG_BAD_GESTURE);
					stage = BEGIN;
					return;
				}
				ContentValues val = new ContentValues();
				Intent rez = new Intent();

				for (int i = 0; i < 3; i++){
					for (int j = 0; j < 3; j++)
						val.put(MotionColumns.MATRIX[i][j], maxMatrix[i][j]);
					Log.i("recorder", "matrix " + maxMatrix[i][0] + " "+ maxMatrix[i][1] + " "+ maxMatrix[i][2] + " ");
				}
				val.put(MotionColumns.TIME, time);
				
				rez.putExtra(RESULT_CONTENT_VALUES_NAME, val);
				//Log.i("rec", "i am here");
				setResult(1, rez);
				mSensorManager.unregisterListener(this);
				Recorder.this.finish();
				position_magnetic = 0;
				
				stage = BEGIN;

			}

		}

		private void showNotification() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static float[][] math_2(float accels_init[], float[] magnetic_init, float[]accels, float[] magnetic){
		for(int i = 0; i < 3; ++i)
			Log.i("math2args", accels_init[i]+" "+magnetic_init[i]+" "+accels[i]+ " " + accels_init[i]);
		float [] R = new float[9];
		float [] I = new float[9];
		float [] R_init = new float[9];
		float [] I_init = new float[9];
		float[][] result = new float[3][3];
		 if(!SensorManager.getRotationMatrix(R, I, accels, magnetic))
			 Log.i("aaa!!", "pzdc!");
		if(!SensorManager.getRotationMatrix(R_init, I_init, accels_init, magnetic_init))
			Log.i("aaa!!", "pzzzdc!");
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 3; ++j){
				result[i][j] = 0;
				for(int k = 0; k < 3; ++k)
					result[i][j] += R_init[3 * k + i] * R[3 * k + j];
				Log.i("math2", result[i][j] + " ");
			}
		return result;
	}
}
