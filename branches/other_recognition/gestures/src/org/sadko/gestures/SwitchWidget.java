package org.sadko.gestures;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class SwitchWidget extends AppWidgetProvider {

	//boolean isEnabled = false;
	boolean isFirstTime = true;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//String s = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_PICK;
		Log.i("widget",intent.getAction() + "!!");
		if (intent.getAction().equals(MotionHandler.ACTION_SERVICE_STATE)) {
			boolean isEnabled = intent.getBooleanExtra(
					MotionHandler.STATE_IN_EXTRAS, false);
			RemoteViews updateViews = new RemoteViews(
					context.getPackageName(), isEnabled ? R.layout.widget_on
							: R.layout.widget_off);

			Intent defineIntent = new Intent(
					isEnabled ? MotionHandlerBroadcastReceiver.ACTION_TURN_OFF
							: MotionHandlerBroadcastReceiver.ACTION_TURN_ON);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, defineIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
			ComponentName thisWidget = new ComponentName(context,
					SwitchWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, updateViews);
		}

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("widget", "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	
	@Override
	public void onEnabled(Context context) {
		Log.i("widget", "onEnabled");
		context.sendBroadcast(new Intent(MotionHandlerBroadcastReceiver.ACTION_GET_STATE));
		super.onEnabled(context);
		
	}
	

}
