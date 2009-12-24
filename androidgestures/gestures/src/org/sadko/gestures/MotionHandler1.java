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

import java.util.Iterator;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.os.Handler;
//import android.util.Log;
//import android.widget.Toast;

public class MotionHandler1 extends MotionHandler {
	SharedPreferences settings;
	public static final String preferencesString = "Gestures.preferences";
	public static final String MOTION_SENSITIVITY_STRING = "Gestures.motion.sensitivity";
	public static final String TIME_INTERVAL_STRING = "Gestures.time.interval"; 
	long needTime = 0;
	public static float MOTION_SENSITIVITY = 0.1f;
	long oldestTime = 0;
	public static long timeBetweenRegistering = 1400;
	int ARRAY_SIZE = 10;
	long lastRegisterTime = 0;
	long[] times = new long[ARRAY_SIZE];
	protected double yaws[] = new double[ARRAY_SIZE];
	protected double rolls[] = new double[ARRAY_SIZE];
	protected double pitchs[] = new double[ARRAY_SIZE];
	int position = 0;
	Cursor c; 
	
	
	@Override
	public void onDestroy() {
		c.close();
		super.onDestroy();
	}
	@Override
	public void addMotion(Motion motion) {
		super.addMotion(motion);
		needTime = (motion.time > needTime ? motion.time : needTime);	
		needTime += 1;
		ARRAY_SIZE = (int) (needTime / 20) + 1;
		yaws = new double[ARRAY_SIZE];
		rolls = new double[ARRAY_SIZE];
		pitchs = new double[ARRAY_SIZE];
		times = new long[ARRAY_SIZE];
	}
	public void onAccuracyChanged(int sensor, int accuracy) {
	}



	public void onSensorChanged(int sensor, float[] values) {

		boolean checkMotion[] = new boolean[motions.size()];
		times[position] = System.currentTimeMillis();
		yaws[position] = values[0] * Math.PI / 180;
		pitchs[position] = values[1] * Math.PI / 180;
		rolls[position] = values[2] * Math.PI / 180;
		int i = position;
		boolean detected = false;
		while (!detected && i != (position + 1) % ARRAY_SIZE) {
			Iterator<Motion> j = motions.iterator();
			int s = 0;
			while (j.hasNext()) {
				Motion m = j.next();
				if (times[position] - times[i] - m.time > 0 && !checkMotion[s]) {
					checkMotion[s] = true;
					double[][] matrix = math(yaws[position], pitchs[position],
							rolls[position], yaws[i], pitchs[i], rolls[i]);
					double ss = 0;
					for (int k = 0; k < 3; k++)
						for (int l = 0; l < 3; l++)
							ss += (matrix[k][l] - m.matrix[k][l])
									* (matrix[k][l] - m.matrix[k][l]);
					if (ss < MOTION_SENSITIVITY
							&& System.currentTimeMillis() - lastRegisterTime > timeBetweenRegistering
							) {
						notifyListeners((int) m.id);
						//Toast.makeText(MotionHandler1.this, "motion!", 500).show();
						lastRegisterTime = System.currentTimeMillis();
						detected = true;
					}
				}
				s++;
			}
			i = (i - 1 + ARRAY_SIZE) % ARRAY_SIZE;
		}
		position = (position + 1) % ARRAY_SIZE;
	}

	@Override
	public void onCreate() {
		settings = getSharedPreferences(preferencesString, 0);
		c = getContentResolver().query(
				MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "A00", "A01", "A02", "A10", "A11", "A12", "A20",
						"A21", "A22", "time", "_id" },
				null, null, null);
		
		while (c.getCount()!=0 && !c.isLast()) {
			
			c.moveToNext();
			Motion motion = new Motion();
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					motion.matrix[i][j] = c.getFloat(c.getColumnIndex("A"+i+""+j));
			motion.time = c.getLong(c.getColumnIndex(MotionColumns.TIME));
			motion.id = c.getLong(c.getColumnIndex(MotionColumns._ID));
			addMotion(motion);
		}
		getContentResolver().registerContentObserver(MotionsDB.MOTIONS_CONTENT_URI, true, new ContentObserver(new Handler(){
			
		}){
	        @Override 
	        public boolean deliverSelfNotifications() { 
	            return true; 
	        }
			@Override
			public void onChange(boolean selfChange) {
				/*Cursor c = getContentResolver().query(
						MotionsDB.MOTIONS_CONTENT_URI,
						new String[] { "A00", "A01", "A02", "A10", "A11", "A12", "A20",
								"A21", "A22", "time", "_id" },
						null, null, null);*/
				//Log.i("i am called","ugu!!");
				c.requery();
				if(isEnabled)mgr.unregisterListener(MotionHandler1.this);
				deleteAllMotions();
				c.moveToFirst();
				while (!c.isAfterLast()) {
					
					Motion motion = new Motion();
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							motion.matrix[i][j] = c.getFloat(c.getColumnIndex("A"+i+""+j));
					motion.time = c.getLong(c.getColumnIndex(MotionColumns.TIME));
					motion.id = c.getLong(c.getColumnIndex(MotionColumns._ID));
					//Log.i("motion",motion.time+" "+motion.matrix[0][0]);
					addMotion(motion);
					c.moveToNext();
				}
				if(isEnabled)mgr.registerListener(MotionHandler1.this,SensorManager.SENSOR_ORIENTATION,SensorManager.SENSOR_DELAY_UI);
				super.onChange(selfChange);
			}
			
		});
		/*c.registerContentObserver(new ContentObserver(new Handler(){
			
		}){

			@Override
			public void onChange(boolean selfChange) {
				/*Cursor c = getContentResolver().query(
						MotionsDB.MOTIONS_CONTENT_URI,
						new String[] { "A00", "A01", "A02", "A10", "A11", "A12", "A20",
								"A21", "A22", "time", "_id" },
						null, null, null);
				Log.i("i am called","ugu!!");
				if(isEnabled)mgr.unregisterListener(MotionHandler1.this);
				deleteAllMotions();
				while (!c.isLast()) {
					c.moveToNext();
					Motion motion = new Motion();
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							motion.matrix[i][j] = c.getFloat(c.getColumnIndex("A"+i+""+j));
					motion.time = c.getLong(c.getColumnIndex(MotionColumns.TIME));
					motion.id = c.getLong(c.getColumnIndex(MotionColumns._ID));
					addMotion(motion);
				}
				if(isEnabled)mgr.registerListener(MotionHandler1.this,SensorManager.SENSOR_ORIENTATION,SensorManager.SENSOR_DELAY_UI);
				super.onChange(selfChange);
			}
			
		});*/
		//showNotification();

		super.onCreate();
	}

	@Override
	public void switchMe() {
		super.switchMe();
		
	}
	boolean isOnSharedPreferencesRegistered = false;
	private void loadPreferences(){
		if(! isOnSharedPreferencesRegistered)
			settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
						String key) {
					if (key.equals(MOTION_SENSITIVITY_STRING) && key.equals(TIME_INTERVAL_STRING))
						MotionHandler1.this.loadPreferences();
				
				}
			});
		isOnSharedPreferencesRegistered = true;
		MOTION_SENSITIVITY = settings.getFloat(MOTION_SENSITIVITY_STRING, 0.1f);
		timeBetweenRegistering = settings.getLong(TIME_INTERVAL_STRING, 1000);
	}
	private void savePreferences(){
		settings.edit().putFloat(MOTION_SENSITIVITY_STRING, (float) MOTION_SENSITIVITY)
		.putLong(TIME_INTERVAL_STRING, timeBetweenRegistering).commit();
	}
	public void changeSensitivity(float d) {
		MOTION_SENSITIVITY = d;
		savePreferences();
	}
	public void changeTimeInterval(long d) {
		timeBetweenRegistering = d;
		savePreferences();
	}
	

}
