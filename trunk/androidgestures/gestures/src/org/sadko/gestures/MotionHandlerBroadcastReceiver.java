package org.sadko.gestures;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MotionHandlerBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION_GET_STATE = "get.handler.state";
	public static final String ACTION_TURN_ON = "turn.handler.on";
	public static final String ACTION_TURN_OFF = "turn.handler.off";
	public static final String ACTION_SET_MODE = "motion.handler.set.mode";
	public static final String MODE_KEY = "set.mode";
	MotionHandler myContainer;
	public MotionHandlerBroadcastReceiver(MotionHandler motionHandler) {
		myContainer = motionHandler;
	}
	Intent getMotionHandlerIntent(Context c){
		return new Intent(c,MotionHandler1.class);
	}
	ListnerBinder binder;
	ListnerBinder getHandlerBinder(Context context){
		
		ListnerBinder result = (ListnerBinder) peekService(context, getMotionHandlerIntent(context));
		if (result == null){
			context.startService(getMotionHandlerIntent(context));
			result = (ListnerBinder) peekService(context, getMotionHandlerIntent(context));
		}
		return result;
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Log.i("action received", "ura!");
		if(action.equals(ACTION_GET_STATE)){
			myContainer.throwStateBroadcast();
		}
		if(action.equals(ACTION_SET_MODE)){
			MotionHandler.mode = intent.getIntExtra(MODE_KEY, MotionHandler1.NORMAL_MODE);
			Log.i("set mode", MotionHandler.mode + " ");
		}
		if(action.equals(ACTION_TURN_ON)){
			ListnerBinder binder = getHandlerBinder(context);
			myContainer.turnOn();
		}	
		if(action.equals(ACTION_TURN_OFF)){
			myContainer.turnOff();
		}
	}

}
