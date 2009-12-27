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

import java.util.ArrayList;
import java.util.List;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class MotionHandler extends Service implements SensorEventListener{
	public static final int START_STOP=359;
	public static final String ACTION_GESTURE_REGISTERED = "gesture.registered";
	public static final String DEBUG_ACTION_GESTURE_REGISTERED = "debug.gesture.registered";
	public static final String ACTION_SERVICE_STATE = "gestures.handler.state";
	public static final String STATE_IN_EXTRAS = "state";
	public static final String GESTUIRE_ID_IN_EXTRAS = "gesture";
	public static int mode = 0;
	public static final int NORMAL_MODE = 0;
	public static final int DEBUG_MODE = 1;
	protected List<Motion> motions;
	
	boolean isEnabled=false;
	MotionHandlerBroadcastReceiver controller;
	SensorManager mgr;
	
	public void addMotion(Motion motion){
		motions.add(motion);
		//Log.i("motion","added!");
		//android.content.Intent.
	}
	

	protected List<ListnerBinder> listners=new ArrayList<ListnerBinder>();
	
	MotionHandler(){
		motions=new ArrayList<Motion>();
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(controller);
		super.onDestroy();
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}
	protected void deleteAllMotions(){
		motions.clear();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("service", "on bind");
		if(listners.isEmpty()){
			if(isEnabled)
				if(motions.size() > 0){
					mgr.registerListener(this, 
							mgr.getSensorList(Sensor.TYPE_ORIENTATION).get(0), 
							SensorManager.SENSOR_DELAY_UI);
					mgr.registerListener(this, 
							mgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0), 
							SensorManager.SENSOR_DELAY_UI);
				}
		}
		ListnerBinder lb=new ListnerBinder();
		lb.mh=this;
		return lb;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);


		mgr = (SensorManager)getSystemService( SENSOR_SERVICE);
		//Log.i("service", "started");
		//displayWidget();
		
		
	}

	@Override
	public void onCreate() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(MotionHandlerBroadcastReceiver.ACTION_GET_STATE);
		iFilter.addAction(MotionHandlerBroadcastReceiver.ACTION_TURN_OFF);
		iFilter.addAction(MotionHandlerBroadcastReceiver.ACTION_TURN_ON);
		iFilter.addAction(MotionHandlerBroadcastReceiver.ACTION_SET_MODE);
		iFilter.addAction(android.content.Intent.ACTION_SCREEN_OFF);
		controller = new MotionHandlerBroadcastReceiver(this);
		registerReceiver(controller, iFilter);
		throwStateBroadcast();
		super.onCreate();
	}
	private void displayWidget() {
		RemoteViews updateViews = new RemoteViews(getPackageName(), isEnabled ? R.layout.widget_on : R.layout.widget_off);
		
		Intent defineIntent = new Intent(isEnabled ?
				MotionHandlerBroadcastReceiver.ACTION_TURN_OFF :
			MotionHandlerBroadcastReceiver.ACTION_TURN_ON);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, defineIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
		ComponentName thisWidget = new ComponentName(this, SwitchWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, updateViews);
	}

	protected void notifyListeners(long motion){
		if(mode == NORMAL_MODE){
			Intent intent = new Intent(ACTION_GESTURE_REGISTERED);
			intent.putExtra(GESTUIRE_ID_IN_EXTRAS, new Long(motion));
			sendBroadcast(intent);
			return;
		}
		if(mode == DEBUG_MODE){
			Intent intent = new Intent(DEBUG_ACTION_GESTURE_REGISTERED);
			intent.putExtra(GESTUIRE_ID_IN_EXTRAS, new Long(motion));
			sendBroadcast(intent);
			Log.i("mode debug"," gesture registered");
			return;
		}
		
	}
	/*public static double[][] math(double yaw2, double pitch2, double roll2, double yaw,
    		double pitch, double roll ){

    	double[][]ans=new double[3][3];
    	ans[0][0] =  (double) (Math.sin(roll2)*Math.cos(pitch2)*Math.sin(roll)*Math.cos(pitch)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch2)*Math.cos(yaw)*Math.cos(roll)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(yaw)*Math.cos(roll)+Math.cos(yaw2)*Math.cos(roll2)*Math.cos(yaw)*Math.cos(roll)-Math.cos(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch));
    	ans[0][1] =  (double) (Math.sin(roll2)*Math.cos(pitch2)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(pitch)*Math.sin(yaw)*Math.sin(yaw2)*Math.sin(pitch2)-Math.cos(pitch)*Math.cos(yaw)*Math.sin(yaw2)*Math.cos(roll2)-Math.sin(roll2)*Math.cos(pitch)*Math.cos(yaw)*Math.cos(yaw2)*Math.sin(pitch2)+Math.cos(pitch)*Math.sin(yaw)*Math.cos(yaw2)*Math.cos(roll2) );
    	ans[0][2] =  (double) (-Math.sin(roll2)*Math.cos(pitch2)*Math.cos(pitch)*Math.cos(roll)-Math.sin(pitch2)*Math.sin(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)-Math.sin(pitch2)*Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)-Math.sin(yaw2)*Math.cos(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.cos(yaw2)*Math.cos(roll2)*Math.cos(yaw)*Math.sin(roll)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(pitch2)*Math.cos(yaw2)*Math.sin(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(yaw2)*Math.sin(roll2)*Math.sin(roll)*Math.sin(yaw)+Math.cos(yaw2)*Math.cos(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw));
    	ans[1][0] = (double)(-Math.cos(pitch2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(yaw)*Math.cos(roll)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(pitch2)*Math.sin(roll)*Math.cos(pitch));
    	ans[1][1]= (double)(Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(pitch)*Math.sin(yaw)+Math.cos(pitch2)*Math.cos(yaw2)*Math.cos(pitch)*Math.cos(yaw)+Math.sin(pitch2)*Math.sin(pitch));
    	ans[1][2]= (double)(Math.cos(pitch2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)+Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(pitch2)*Math.cos(pitch)*Math.cos(roll));
    	ans[2][0]= (double)(-Math.cos(pitch2)*Math.cos(roll2)*Math.sin(roll)*Math.cos(pitch)+Math.sin(roll2)*Math.cos(yaw2)*Math.cos(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(yaw)*Math.cos(roll)-Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.cos(roll));
    	ans[2][1]=(double)(-Math.cos(pitch2)*Math.cos(roll2)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(pitch)*Math.cos(yaw)*Math.sin(yaw2)+Math.cos(pitch)*Math.sin(yaw)*Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)+Math.sin(roll2)*Math.cos(pitch)*Math.sin(yaw)*Math.cos(yaw2)+Math.cos(pitch)*Math.cos(yaw)*Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2));
    	ans[2][2]=(double)(Math.cos(pitch2)*Math.cos(roll2)*Math.cos(pitch)*Math.cos(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.cos(yaw)*Math.sin(roll)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw));
     	return ans;
    	 
    }*/
	public static float[][] math_2(float accels_init[], float[] magnetic_init, float[]accels, float[] magnetic){
		float [] R = new float[9];
		float [] I = new float[9];
		float [] R_init = new float[9];
		float [] I_init = new float[9];
		float[][] result = new float[3][3];
		SensorManager.getRotationMatrix(R, I, accels, magnetic);
		SensorManager.getRotationMatrix(R_init, I_init, accels_init, magnetic_init);
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 3; ++j){
				result[i][j] = 0;
				for(int k = 0; k < 3; ++k)
					result[i][j] += R_init[3 * k + i] * R[3 * k + j];
			}
		return result;
	}
	protected void showNotification() {
		Intent m_clickIntent = new Intent();
		m_clickIntent.setClass(this, MyTabActivity.class);
		m_clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification m_currentNotification = new Notification(R.drawable.icon2,
				"Gestures service is running", System.currentTimeMillis());
		m_currentNotification.setLatestEventInfo(this, "Gestures Service is running",
				"click here to change state", PendingIntent.getActivity(this, 0,
						m_clickIntent, PendingIntent.FLAG_CANCEL_CURRENT));
		mNM.notify(0, m_currentNotification);

	}
	protected void killNotification(){
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(0);
	}
	public void switchMe() {
		if(isEnabled){
			mgr.unregisterListener(this);
			killNotification();
		}
		else{
			if(motions.size() > 0){
				mgr.registerListener(this, 
						mgr.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), 
						SensorManager.SENSOR_DELAY_UI);
				mgr.registerListener(this, 
					mgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0), 
					SensorManager.SENSOR_DELAY_UI);
			}
			showNotification();
		}
		isEnabled= !isEnabled;
		//displayWidget();
		throwStateBroadcast();
		// TODO Auto-generated method stub
		
	}

	public void throwStateBroadcast() {
		//Log.i("1", "4");
		Intent intent = new Intent(ACTION_SERVICE_STATE);
		intent.putExtra(STATE_IN_EXTRAS, isEnabled);
		sendBroadcast(intent);
	}

	public void turnOn() {
		//Log.i("1", "2");
		if (!isEnabled){
			switchMe();
		}
		
	}

	public void turnOff() {
		//Log.i("1", "3");
		if (isEnabled){
			switchMe();
		}
	}
}

