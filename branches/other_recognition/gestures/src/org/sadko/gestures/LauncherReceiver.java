package org.sadko.gestures;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class LauncherReceiver extends MotionHandlerHelper {


	 
			@Override
			public void OnGestureRegistered(long id) {
				
				Log.i("manager", "received gesture with id " + id);
				Cursor tasksForGesture = mContext.getContentResolver().query(
						MotionsDB.TASKS_CONTENT_URI,
						new String[] { ActivityColumns.PACK,
								ActivityColumns.ACTIVITY },
						ActivityColumns.MOTION_ID + "=" + id, null, null);
				tasksForGesture.moveToFirst();
				while (!tasksForGesture.isAfterLast()) {
					if (tasksForGesture.getString(0) != null
							&& tasksForGesture.getString(1) != null) {
						Intent i = new Intent();
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						i.setClassName(tasksForGesture.getString(0),
								tasksForGesture.getString(1));
						try {
							mContext.startActivity(i);
						} catch (Exception e) {
							Toast.makeText(mContext, "cant't start activity",
									1000).show();
							e.printStackTrace();
						}
					}
					tasksForGesture.moveToNext();
				}
				tasksForGesture.close();
			}



}
