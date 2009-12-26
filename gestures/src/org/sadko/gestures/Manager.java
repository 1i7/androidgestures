/*
h  * Copyright (C) 2007 The Android Open Source Project
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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sadko.about.AboutActivity;

public class Manager extends Activity {
	ListView lv;
	private static final int ADD_NEW_ID = 0;
	private static final int SETTINNGS_ID = 3;
	private static final int EXIT_ID = 1;
	private static final int ABOUT_ID = 2;
	public static boolean isServiceStarted = false;
	ImageButton startMyService;
	TextView serviceState;
	MotionHandlerHelper mMotionHandlerHelper;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SETTINNGS_ID, 0, "Settings").setIcon(
				android.R.drawable.ic_menu_set_as);
		menu.add(0, ABOUT_ID, 0, "About").setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(0, EXIT_ID, 0, "Exit").setIcon(
				android.R.drawable.ic_menu_revert);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case SETTINNGS_ID: {
			Intent i = new Intent(Manager.this, SettingsActivity.class);
			i.setAction(android.content.Intent.ACTION_MAIN);
			startActivity(i);
			break;
		}
		case EXIT_ID: {
			Manager.this.finish();
			break;
		}
		case ABOUT_ID: {
			final Intent intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			break;
		}
			/*
			 * case KILL_SERVICE_ID: { //Manager.this.finish();
			 * lb.mh.killNotification(); stopService(new Intent(Manager.this,
			 * MotionHandler1.class)); lb=null; break; }
			 */
		}
		return super.onMenuItemSelected(featureId, item);
	}

	SimpleCursorAdapter motions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout01);
		layout.setBackgroundColor(Color.TRANSPARENT);
		startMyService = (ImageButton) findViewById(R.id.start_stop_service);
		startService(new Intent(this, MotionHandler1.class));
		serviceState = (TextView) findViewById(R.id.text_about_service_state);
		mMotionHandlerHelper = new MotionHandlerHelper(this){
			@Override
			public void OnStateReceived(boolean isEnabled) {
				setBannerEnabled(isEnabled);
			}
		
		};
		
		startMyService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchService();
			}
		});
	}

	private void setBannerEnabled(boolean isEnabled) {
		startMyService.setBackgroundResource(isEnabled ? R.drawable.banner_up
				: R.drawable.banner_down);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		mMotionHandlerHelper.unregisterAsReceiver();
		super.onDestroy();
	}


	void switchService() {
		mMotionHandlerHelper.switchService();
	}
}
