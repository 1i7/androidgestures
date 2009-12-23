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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sadko.about.AboutActivity;

public class Manager extends Activity {
	ListView lv;
	Cursor c;
	private static final int ADD_NEW_ID = 0;
	private static final int EXIT_ID = 1;
	private static final int ABOUT_ID = 2;
	public static boolean isServiceStarted = false;
	ImageButton startMyService;
	TextView serviceState;
	static MotionHandlerHelper mMotionHandlerHelper;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD_NEW_ID, 0, "Add new").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, ABOUT_ID, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, EXIT_ID, 0, "Exit").setIcon(android.R.drawable.ic_menu_revert);
		
		// menu.add(0, KILL_SERVICE_ID, 0, "Kill handling service");
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_NEW_ID: {
			Intent i = new Intent(Manager.this, MotionEditor.class);
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
	int selectedItem = -1;
		@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout01);
		layout.setBackgroundColor(Color.TRANSPARENT);
		startMyService = (ImageButton) findViewById(R.id.start_stop_service);
		startService(new Intent(this, MotionHandler1.class));
		//startMyService.setEnabled(false);
		//startMyService.setTextSize(20);
		serviceState = (TextView) findViewById(R.id.text_about_service_state);
		mMotionHandlerHelper = new MotionHandlerHelper(this){
			@Override
			public void OnGestureRegistered(long id) {

			}

			@Override
			public void OnStateReceived(boolean isEnabled) {
			}
			
		
		};

		/*
		 * if(savedInstanceState!=null &&
		 * savedInstanceState.containsKey("process"))
		 * startMyService.setText(savedInstanceState
		 * .getBoolean("process")?"stop":"start");
		 */

		startMyService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchBanner();
				switchService();
			}

			
		});

	}
		private void switchBanner() {
			// TODO Auto-generated method stub
			if (isServiceStarted){
				isServiceStarted = false;
				startMyService.setBackgroundResource(R.drawable.banner_up);
			} else {
				isServiceStarted = true;
				startMyService.setBackgroundResource(R.drawable.banner_down);
			}
		}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		/*
		con = new ServiceConnection() {
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				lb = (ListnerBinder) arg1;
				//Log.i("handler", lb.mh + "");
				startMyService.setImageResource(lb.mh.isEnabled ? R.drawable.banner : R.drawable.banner);
				serviceState.setText("Gestures service is"
						+ (lb.mh.isEnabled ? " running" : " idle"));
				Cursor c = getContentResolver().query(
						MotionsDB.MOTIONS_CONTENT_URI,
						new String[] { "count(_ID)" }, null, null, null);
				c.moveToFirst();
				if (c.getInt(0) == 0 && lb.mh.isEnabled)
					startMyService.setEnabled(true);
				if (c.getInt(0) == 0)
					return;
				c.close();
				lb.ms = new MotionListener() {
					public void onMotionRecieved(int motion) {
						Cursor c = getContentResolver().query(
								MotionsDB.TASKS_CONTENT_URI,
								new String[] { ActivityColumns.PACK,
										ActivityColumns.ACTIVITY },
								ActivityColumns.MOTION_ID + "=" + motion, null,
								null);
						c.moveToFirst();
						while (!c.isAfterLast()) {
							if (c.getString(0) != null
									&& c.getString(1) != null) {
								Intent i = new Intent();

								i.setClassName(c.getString(0), c.getString(1));
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								try {
									//Log.i("startActivity", "begin");
									lb.mh.startActivity(i);

									//Log.i("startActivity", "end");
								} catch (Exception e) {

									//Log.i("startActivity", "failed");
									Toast.makeText(lb.mh,
											"cant't start activity", 1000)
											.show();
									e.printStackTrace();
								}
							}

							c.moveToNext();

						}
						c.close();
					}

				};
				startMyService.setEnabled(true);

			}

			public void onServiceDisconnected(ComponentName arg0) {
				startMyService.setEnabled(false);

			}
		};

		bindService(new Intent(this, MotionHandler1.class), con, 0);
		*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*Cursor c = getContentResolver().query(MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "count(_ID)" }, null, null, null);
		c.moveToFirst();
		if (lb == null || lb.mh == null)
			return;
		if (c.getInt(0) == 0 && lb.mh.isEnabled)
			startMyService.setEnabled(true);
		if (c.getInt(0) == 0 && !lb.mh.isEnabled)
			startMyService.setEnabled(false);*/

	}


	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// c.close();
		super.onSaveInstanceState(outState);
	/*	if (lb != null)
			outState.putBoolean("process", isServiceEnabled());*/
		// unbindService(con);
	}

	@Override
	protected void onDestroy() {
		stopManagingCursor(c);
		if (c != null){
			c.close();
		}
		super.onDestroy();
	}

	/*boolean isServiceEnabled() {
		// Parcel p = Parcel.obtain();
		// try {
		return lb.mh.isEnabled;// .transact(ListnerBinder.GET_STATUS, null, p,
								// 0);
		// } catch (RemoteException e) {}
		// return p.readBundle().getBoolean("on/off");

	}*/

	public static void switchService() {
		// try {
		mMotionHandlerHelper.switchService();// transact(ListnerBinder.SWITCH_CODE, null, null, 0);
		// } catch (RemoteException e) {}
	}
}
