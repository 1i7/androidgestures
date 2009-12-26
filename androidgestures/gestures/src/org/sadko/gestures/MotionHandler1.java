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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
//import android.util.Log;
//import android.widget.Toast;

public class MotionHandler1 extends MotionHandler {
	SharedPreferences settings;
	public static final String PREFERENCE_STRING = "Gestures.preferences";
	public static final String MOTION_SENSITIVITY_STRING = "Gestures.motion.sensitivity";
	public static final String PERIOD_STRING = "Gestures.time.period";
	public static final float MAX_SENSITIVITY = 4;
	public static final float MAX_PERIOD = 500;
	public static float SENSITIVITY = 0.1f;
	public static float SENSITIVITY_DEFAULT = 2.0f;
	public static long PERIOD = 100;
	public static long PERIOD_DEFAULT = 100;
	long needTime = 0;
	long oldestTime = 0;
	public static final long timeBetweenRegistering = 1500;
	int ARRAY_SIZE = 10;
	long lastRegisterTime = 0;
	long lastRegisteredGestureId = 0;
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
		double norm = 0;
		for (int i = 0; i < 3; ++i)
			for(int j = 0; j < 3; ++j)
				norm += (motion.matrix[i][j]) *(motion.matrix[i][j]);
		Log.i("motion norm", norm + "");
		needTime += 1;
		
		ARRAY_SIZE = (int) (needTime / 20) + 1;
		yaws = new double[ARRAY_SIZE];
		rolls = new double[ARRAY_SIZE];
		pitchs = new double[ARRAY_SIZE];
		times = new long[ARRAY_SIZE];
	}


	@Override
	public void onCreate() {
		settings = getSharedPreferences(PREFERENCE_STRING, 0);
		loadPreferences();
		c = getContentResolver().query(
				MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "A00", "A01", "A02", "A10", "A11", "A12", "A20",
						"A21", "A22", MotionColumns.TIME, MotionColumns._ID,
						MotionColumns.ISACTIVE },
				MotionColumns.ISACTIVE + " = " + 1, null, null);
		//android.content.Intent.
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
				c.requery();
				if(isEnabled)
					mgr.unregisterListener(MotionHandler1.this);
				deleteAllMotions();
				c.moveToFirst();
				while (!c.isAfterLast()) {		
					Motion motion = new Motion();
					for (int i = 0; i < 3; i++)
						for (int j = 0; j < 3; j++)
							motion.matrix[i][j] = c.getFloat(c.getColumnIndex("A"+i+""+j));
					motion.time = c.getLong(c.getColumnIndex(MotionColumns.TIME));
					motion.id = c.getLong(c.getColumnIndex(MotionColumns._ID));
					addMotion(motion);
					c.moveToNext();
				}
				if(isEnabled)
					mgr.registerListener(MotionHandler1.this, 
							mgr.getSensorList(Sensor.TYPE_ORIENTATION).get(0), 
							SensorManager.SENSOR_DELAY_UI);
				super.onChange(selfChange);
			}
			
		});
			super.onCreate();
	}


	boolean isOnSharedPreferencesRegistered = false;
	private void loadPreferences(){
		if(! isOnSharedPreferencesRegistered)
			settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
						String key) {
					Log.i("key", key +"!");
					if (key.equals(MOTION_SENSITIVITY_STRING) || key.equals(PERIOD_STRING))
						MotionHandler1.this.loadPreferences();
				
				}
			});
		isOnSharedPreferencesRegistered = true;
		SENSITIVITY = settings.getFloat(MOTION_SENSITIVITY_STRING, SENSITIVITY_DEFAULT);
		PERIOD = settings.getLong(PERIOD_STRING, PERIOD_DEFAULT);
		Log.i("preferences", "SENSITIVITY = " + SENSITIVITY);
	}
	private void savePreferences(){
		settings.edit().putFloat(MOTION_SENSITIVITY_STRING, (float) SENSITIVITY)
		.putLong(PERIOD_STRING, PERIOD).commit();
	}
	public void changeSensitivity(float d) {
		SENSITIVITY = d;
		savePreferences();
	}
	public void changePeriod(long d) {
		PERIOD = d;
		savePreferences();
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	long lastAcceptedTime = 0;
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.timestamp / 1000000 - lastAcceptedTime < PERIOD)
			return;
		//Log.i("sensor", event.values[0] + " " + event.values[1]+ " " + event.values[2] + " "+lastAcceptedTime + " " +MOTION_SENSITIVITY);
		lastAcceptedTime = event.timestamp / 1000000;
		boolean checkMotion[] = new boolean[motions.size()];
		
		times[position] = lastAcceptedTime;
		yaws[position] = event.values[0] * Math.PI / 180;
		pitchs[position] = event.values[1] * Math.PI / 180;
		rolls[position] = event.values[2] * Math.PI / 180;
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
					if (ss < SENSITIVITY
							&& (lastAcceptedTime - lastRegisterTime > timeBetweenRegistering
							|| m.id != lastRegisteredGestureId)
							) {
						
						lastRegisteredGestureId = m.id; 
						lastRegisterTime = lastAcceptedTime;
						detected = true;
						notifyListeners(m.id);
						Log.i("mhandler", "caught");
					}
					Log.i("sensor", ss+"");
				}
				s++;
			}
			i = (i - 1 + ARRAY_SIZE) % ARRAY_SIZE;
		}
		position = (position + 1) % ARRAY_SIZE;	
	}
}
