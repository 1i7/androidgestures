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

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("widget", "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_off);
		
		Intent defineIntent = new Intent(
		MotionHandlerBroadcastReceiver.ACTION_TURN_ON);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, defineIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
		ComponentName thisWidget = new ComponentName(context, SwitchWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thisWidget, updateViews);
		
		super.onEnabled(context);
		
	}
	

}
