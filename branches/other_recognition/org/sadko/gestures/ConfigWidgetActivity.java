package org.sadko.gestures;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class ConfigWidgetActivity extends Activity {
	@Override
	public void onCreate (Bundle b){
		super.onCreate(b);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		int mAppWidgetId = 0;
		if (extras != null) {
		    mAppWidgetId = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		sendBroadcast(new Intent(MotionHandlerBroadcastReceiver.ACTION_GET_STATE));
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_off);
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}
