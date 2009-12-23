package org.sadko.gestures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MotionHandlerBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION_GET_STATE = "get_handler_state";
	public static final String ACTION_TURN_ON = "turn_handler_on";
	public static final String ACTION_TURN_OFF = "turn_handler_off";
	Intent getMotionHandlerIntent(Context c){
		return new Intent(c,MotionHandler1.class);
	}
	ListnerBinder getHandlerBinder(Context context){
		return (ListnerBinder) peekService(context, getMotionHandlerIntent(context));
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Log.i("action received", "ura!");
		if(action.equals(ACTION_GET_STATE)){
			ListnerBinder binder = getHandlerBinder(context);
			if(binder == null)
				return;
			binder.mh.throwStateBroadcast();
		}
		if(action.equals(ACTION_TURN_ON)){
			ListnerBinder binder = getHandlerBinder(context);
			if(binder == null)
				return;
			binder.mh.turnOn();
		}	
		if(action.equals(ACTION_TURN_OFF)){
			ListnerBinder binder = getHandlerBinder(context);
			if(binder == null)
				return;
			binder.mh.turnOff();
		}
	}

}
