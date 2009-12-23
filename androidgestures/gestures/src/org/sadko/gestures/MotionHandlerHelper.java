package org.sadko.gestures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MotionHandlerHelper extends BroadcastReceiver {
	Context mContext;
	boolean lastState;

	public MotionHandlerHelper(Context context) {
		mContext = context;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MotionHandler.ACTION_SERVICE_STATE);
		intentFilter.addAction(MotionHandler.ACTION_GESTURE_REGISTERED);
		context.registerReceiver(this, intentFilter);
		getState();
		Log.i("mhh","call");
	}
	public void switchService(){
		if(lastState)
			turnOff();
		else
			turnOn();
	}
	public void turnOn(){
		mContext.sendBroadcast(
				new Intent(MotionHandlerBroadcastReceiver.
						ACTION_TURN_ON));
		Log.i("mhh","call on");
	}
	public void turnOff(){
		mContext.sendBroadcast(
				new Intent(MotionHandlerBroadcastReceiver.
						ACTION_TURN_OFF));
		Log.i("mhh","call off");
	}
	public void getState(){
		mContext.sendBroadcast(
				new Intent(MotionHandlerBroadcastReceiver.
						ACTION_GET_STATE));
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(MotionHandler.ACTION_SERVICE_STATE)){
			Log.i("mhh","state received");
			lastState = intent.getBooleanExtra(MotionHandler.STATE_IN_EXTRAS, false);
			OnStateReceived(lastState);
		}
		if(action.equals(MotionHandler.ACTION_GESTURE_REGISTERED)){
			long id =intent.getLongExtra(MotionHandler.GESTUIRE_ID_IN_EXTRAS, -1);
			OnGestureRegistered(id);
		}
		Log.i("mhh",action);
	}
	public void OnStateReceived(boolean isEnabled){
	}
	public void OnGestureRegistered(long id){
		
	}
}
