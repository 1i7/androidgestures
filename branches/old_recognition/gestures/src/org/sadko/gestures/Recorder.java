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
import android.hardware.SensorListener;
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
	public static final double THRESHOLD_DEVIATION = 3.0; 
	public static double[][] math(double yaw2, double pitch2, double roll2,
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

	}

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
		instruction.setText("1. Press start to start recording the gesture\n2. Make a short movement with your phone in a space and remember the gesture)");
		instruction2.setText("3. Press stop when finished");
		start = (Button) findViewById(R.id.Button01);
		start.setTextSize(50);
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
		mSensorManager = (SensorManager)getSystemService( SENSOR_SERVICE);//(SensorManager)getSystemService(SENSOR_SERVICE);
		//mSensorManager.connectSimulator();
		//Hardware.mContentResolver=getContentResolver();
		//mSensorManager= new SensorManagerSimulator((SensorManager)
		//getSystemService(SENSOR_SERVICE));
		//SensorManagerSimulator.connectSimulator();
		mSensorManager.registerListener(r,
				SensorManager.SENSOR_ORIENTATION,
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
	
	private class recordListener implements SensorListener {
		int ARRAY_SIZE = 10;

		double[][] maxMatrix;
		long[] times = new long[ARRAY_SIZE];
		protected double yaws[] = new double[ARRAY_SIZE];
		protected double rolls[] = new double[ARRAY_SIZE];
		protected double pitchs[] = new double[ARRAY_SIZE];
		int position = 0;

		public void onAccuracyChanged(int sensor, int accuracy) {
		}

		private void increase() {
			ARRAY_SIZE *= 2;
			long[] newt = new long[ARRAY_SIZE];
			System.arraycopy(times, 0, newt, 0, position + 1);
			times = newt;
			double[] newy = new double[ARRAY_SIZE];
			System.arraycopy(yaws, 0, newy, 0, position + 1);
			yaws = newy;
			double[] newr = new double[ARRAY_SIZE];
			System.arraycopy(rolls, 0, newr, 0, position + 1);
			rolls = newr;
			double[] newp = new double[ARRAY_SIZE];
			System.arraycopy(pitchs, 0, newp, 0, position + 1);
			pitchs = newp;

		}

		public void onSensorChanged(int sensor, float[] values) {
			if (stage == RECORD) {
				if (position == ARRAY_SIZE - 1)
					increase();
				yaws[position] = values[0] * Math.PI / 180;
				rolls[position] = values[2] * Math.PI / 180;
				pitchs[position] = values[1] * Math.PI / 180;
				times[position] = System.currentTimeMillis();
			//	Log.i("sens " + position, "" + yaws[position] + "\t"
				//		+ pitchs[position] + "\t" + rolls[position] + "\t"
					//	+ times[position] + "\n");
				position++;
			}
			if (stage == END) {
				double[][] matrix;
				long time = 0;
				double matrixNorm = 0;
				maxMatrix = new double[3][3];
				maxMatrix[1][1] = maxMatrix[2][2] = maxMatrix[0][0] = 1;
				double maxMatrixNorm = 0;
				for (int i = 1; i < position; i++) {
					matrix = math(yaws[i], pitchs[i], rolls[i], yaws[0],
							pitchs[0], rolls[0]);
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

				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
						val.put(MotionColumns.MATRIX[i][j], maxMatrix[i][j]);
				val.put(MotionColumns.TIME, time);
				
				rez.putExtra(RESULT_CONTENT_VALUES_NAME, val);
				//Log.i("rec", "i am here");
				setResult(1, rez);
				mSensorManager.unregisterListener(this);
				Recorder.this.finish();
				position = 0;
				stage = BEGIN;

			}

		}

		private void showNotification() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
