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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
//import android.content.res.Configuration;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
//import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MotionEditor extends Activity {
	ContentValues motionValues = new ContentValues();
	ContentValues activityValues = new ContentValues();
	String action;
	long motionId=0;
	long taskId;
	Button launch;
	String appActivity;


	// Drawable appIcon;
	String appName;
	String appPackage;
	Spinner spinner;
	Button testGesture;
	Button saveExit;
	boolean discarding = false;
	List<String> launchers;
	private static final int RECORD_REQEST_CODE = 1;
	private static final int PICK_APP_REQUEST_CODE = 2;
	ActivityInfo[] activs;
	//String oldAppName;
	boolean needOn=false;
	ServiceConnection con;
	ListnerBinder lb;
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			if (!discarding)
				saveGesture();
		}
		if(needOn)lb.mh.switchMe();
		lb.mh.deleteListener(lb.ms);
		unbindService(con);

	}
	private void sortActivs(){
		final PackageManager pm=getPackageManager();
		TreeSet<ActivityInfo> tmp=new TreeSet<ActivityInfo>(new Comparator<ActivityInfo>(){
		
			public int compare(ActivityInfo object1, ActivityInfo object2) {
				boolean firstIsLauncher=launchers.contains(object1.name);
				boolean secondIsLauncher=launchers.contains(object2.name);
				if(firstIsLauncher && !secondIsLauncher) return -1;
				if(!firstIsLauncher && secondIsLauncher) return 1;
				CharSequence label1= object1.loadLabel(pm);
				CharSequence label2= object2.loadLabel(pm);
				if(label1==null) return 1;
				if(label2==null) return -1;
				if( label1.toString().compareTo(label2.toString())!=0) return label1.toString().compareTo(label2.toString());
				else  return object1.name.compareTo(object2.name);
				//return object1.name.compareTo(object2.name);
				
			}
			
		});
		for(int i=0;i<activs.length;i++)
			if(activs[i]!=null) tmp.add(activs[i]);
		activs=new ActivityInfo[tmp.size()];
		activs=tmp.toArray(activs);
		
		
	}
	void saveGesture() {
		// saving name of motion
		motionValues.put(MotionColumns.NAME,
				((EditText) findViewById(R.id.NameInput)).getText().toString());
		if (action.equals(android.content.Intent.ACTION_EDIT)) {
			// push motion to db
			getContentResolver().update(MotionsDB.MOTIONS_CONTENT_URI,
					motionValues, "_ID=" + motionId, null);
			activityValues.put(ActivityColumns.ACTIVITY, appActivity);
			// push task to this motion
			getContentResolver().update(MotionsDB.TASKS_CONTENT_URI,
					activityValues, "_ID=" + taskId, null);
		} else {
			// push motion to db
			motionId = ContentUris.parseId(getContentResolver().insert(
					MotionsDB.MOTIONS_CONTENT_URI, motionValues));
			//if(app)
			activityValues.put(ActivityColumns.ACTIVITY, appActivity);
			activityValues.put(ActivityColumns.MOTION_ID, motionId);
			taskId=ContentUris.parseId(getContentResolver().insert(MotionsDB.TASKS_CONTENT_URI,
					activityValues));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		//outState.putParcelable("motionValues",motionValues);
		//outState.putParcelable("activityValues",activityValues);
		saveGesture();
		action=android.content.Intent.ACTION_EDIT;
		outState.putString("action",action);
		outState.putLong("motionId",motionId);
		/*outState.putLong("taskId",taskId);
		outState.putString("appActivity",appActivity);
		outState.putString("appName",appName);
		outState.putString("appPackage",appPackage);*/
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1)
			if (resultCode == RECORD_REQEST_CODE) {
				ContentValues v = data
						.getParcelableExtra(Recorder.RESULT_CONTENT_VALUES_NAME);
				motionValues.putAll(v);
				testGesture.setEnabled(true);

			}
		if (requestCode == PICK_APP_REQUEST_CODE) {
			if (resultCode == 1) {
				ContentValues v = data
						.getParcelableExtra(AppPicker.RESULT_CONTENT_VALUES_NAME);
				if (v.containsKey(ActivityColumns.ACTIVITY))
					activityValues.putAll(v);
				appPackage = v.getAsString(ActivityColumns.PACK);

				makeSpinner();
				PackageInfo pi;
				try {
					pi = getPackageManager().getPackageInfo(appPackage,
							PackageManager.GET_ACTIVITIES);
					activs=new ActivityInfo[pi.activities.length];
					System.arraycopy(pi.activities, 0, activs, 0, pi.activities.length);
					sortActivs();
					appActivity = activs[0].name;
				} catch (NameNotFoundException e) {
					Toast.makeText(this,
							"cannot retrieve activities from " + appPackage,
							500).show();
				}

				setPickedApp();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	private void initElements(){
		//initialize spinner
		spinner = (Spinner) findViewById(R.id.activity_spinner);
		spinner.setEnabled(false);
		//set big font to name of app
		((TextView) findViewById(R.id.app_name_in_edit)).setTextSize(20);
		
		//initialize record button
		ImageButton record = (ImageButton) findViewById(R.id.Record);
		record.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				bindService(new Intent(MotionEditor.this,MotionHandler1.class), new ServiceConnection(){

					public void onServiceConnected(ComponentName arg0,
							IBinder arg1) {
						ListnerBinder lb=(ListnerBinder) arg1;
						if(lb.mh.isEnabled)
							lb.mh.switchMe();
						unbindService(this);
					}

					public void onServiceDisconnected(ComponentName name) {
						// TODO Auto-generated method stub
						
					}
					
				}, 0);
				Intent i = new Intent(MotionEditor.this, Recorder.class);
				startActivityForResult(i, RECORD_REQEST_CODE);
			}

		});
		
		//initialize discard button
		Button discard = (Button) findViewById(R.id.discard);
		discard.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				discarding = true;
				finish();
			}

		});
		
		//init test button
		testGesture = (Button) findViewById(R.id.test_gesture);
		testGesture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MotionEditor.this,
						TestGestureActivity.class);
				saveGesture();
				action=android.content.Intent.ACTION_EDIT;
				//if (action.equals(android.content.Intent.ACTION_EDIT)
					//	&& motionValues.size() == 0) {
					i.putExtra("motion_id", motionId);

				//} else {
					//i.putExtra("motion", motionValues);
				//}
				startActivity(i);
			}

		});
		
		//init launch now button
		launch = (Button) findViewById(R.id.launch_now);
		launch.setEnabled(false);
		launch.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent();
				try {
					i.setClassName(appPackage, appActivity);
					startActivity(i);
				} catch (Exception e) {
					Toast.makeText(
							MotionEditor.this,
							"cannot start activity\n" + appPackage + "\n"
									+ appActivity, 1000).show();
				}
			}
		});
		//init save and exit button
		saveExit = (Button) findViewById(R.id.sSandE);
		saveExit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});
		//init pick app button
		ImageButton pick = (ImageButton) findViewById(R.id.pick);
		pick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MotionEditor.this, AppPicker.class);
				startActivityForResult(i, PICK_APP_REQUEST_CODE);
			}
		});
		//init delete button
		Button delete = (Button) findViewById(R.id.delete);
		if (!action.equals(android.content.Intent.ACTION_EDIT))
			delete.setVisibility(android.widget.Button.INVISIBLE);
		delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_YES_NO_MESSAGE);
			}

		});
		Button fromApp=(Button)findViewById(R.id.from_app);
		fromApp.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				PackageManager pm=getPackageManager();
				try {
					((EditText) findViewById(R.id.NameInput)).setText(pm
							.getApplicationLabel(pm.getApplicationInfo(appPackage,
									0)));
				} catch (NameNotFoundException e) {
					
				}
				
			}
			
		});

		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		//get action of start
		action = getIntent().getAction();
		//if(action)
		//GET ALL LAUNCHERS
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
		Iterator<ResolveInfo> launchersList= getPackageManager().queryIntentActivities(intent, 0).iterator();
		launchers=new ArrayList<String>();
		while(launchersList.hasNext()){
			launchers.add(launchersList.next().activityInfo.name);
		}
		
		if(savedInstanceState!=null){
			if(savedInstanceState.containsKey("action")){
				action=savedInstanceState.getString("action");
				motionId=savedInstanceState.getLong("motionId");
			}
		}
				
		//if we edit motion we should get what we are going to edit		
		if (action.equals(android.content.Intent.ACTION_EDIT)) {
			initElements();
			if(motionId==0)
				motionId = getIntent().getExtras().getLong("id");
			Cursor taskCursor = getContentResolver().query(MotionsDB.TASKS_CONTENT_URI,
					null, ActivityColumns.MOTION_ID + "=" + motionId, null,
					null);
			taskCursor.moveToFirst();
			//PackageManager pm = getPackageManager();
			//retrieve app package and activity
			try {
				appPackage = taskCursor.getString(taskCursor
						.getColumnIndex(ActivityColumns.PACK));
				appActivity = taskCursor.getString(taskCursor
						.getColumnIndex(ActivityColumns.ACTIVITY));
			} catch (CursorIndexOutOfBoundsException e) {
				//Log.e("error", "with cursor");
			}
			taskId = taskCursor.getLong(taskCursor.getColumnIndex(ActivityColumns._ID));
			//bound spinner and app representations to retrieved info
			makeSpinner();
			setPickedApp();
			
			Cursor c = getContentResolver().query(
					ContentUris.withAppendedId(MotionsDB.MOTIONS_CONTENT_URI,
							motionId),
					new String[] { MotionColumns.NAME, MotionColumns.TIME },
					null, null, null);

			c.moveToFirst();
			//set name of motion in edit text
			if (c.getString(0) != null)
				((EditText) findViewById(R.id.NameInput)).setText(c
						.getString(0));
			//if motion was recorded let to test it
			if (!c.isNull(1))
				testGesture.setEnabled(true);
			
			c.close();
			taskCursor.close();
		}else{
			if(savedInstanceState!=null){
				/*motionValues=savedInstanceState.getParcelable("motionValues");
				activityValues=savedInstanceState.getParcelable("activityValues");
				action=savedInstanceState.getString("action");
				motionId=savedInstanceState.getLong("motionId");
				taskId=savedInstanceState.getLong("taskId");
				appActivity=savedInstanceState.getString("appActivity");
				appName=savedInstanceState.getString("appName");
				appPackage=savedInstanceState.getString("appPackage");*/
				
			}
			initElements();
		}
		




	}

	static final int DIALOG_YES_NO_MESSAGE = 999;
	//delete or no dialog
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(MotionEditor.this).setTitle(
					"are you sure to remove").setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							// User clicked OK so do some stuff
							long id = motionId;
							getContentResolver()
									.delete(
											Uri.withAppendedPath(
													MotionColumns.CONTENT_URI,
													"" + id), null, null);
							MotionEditor.this.discarding=true;
							MotionEditor.this.finish();
							// c.requery();

						}
					}).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							// User clicked Cancel so do some stuff

							// System.out.println("cancel clicked.");
						}
					}).create();
		}
		return null;
	}

	@Override
	protected void onResume() {
		con=new ServiceConnection(){
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				lb= (ListnerBinder) arg1;
				if(lb.mh.isEnabled){
					needOn=true;
					lb.mh.switchMe();
				}
			}

			public void onServiceDisconnected(ComponentName arg0) {
				
				
			}
		};
			
		bindService(new Intent(this,MotionHandler1.class),con,0);
		
		super.onResume();
	}
	private void setPickedApp() {
		//decide should we replace name of motion or not and other stuff
		boolean nameWasAsPickedApp = false;
		EditText motionName = ((EditText) findViewById(R.id.NameInput));
		if (motionName.getText().toString().equals(""))
			nameWasAsPickedApp = true;
		//
		PackageManager pm = getPackageManager();
		try {
			ImageButton pickButton = ((ImageButton) findViewById(R.id.pick));
			pickButton.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
			pickButton.setImageDrawable(pm.getApplicationIcon(appPackage));
			TextView appNameLabel = ((TextView) findViewById(R.id.app_name_in_edit));
			if (appNameLabel.getText().toString().equals(
					motionName.getText().toString()))
				nameWasAsPickedApp = true;
			((TextView) findViewById(R.id.app_name_in_edit)).setText(pm
					.getApplicationLabel(pm.getApplicationInfo(appPackage, 0)));
			PackageInfo pi = pm.getPackageInfo(appPackage,
					PackageManager.GET_ACTIVITIES);
			activs=new ActivityInfo[pi.activities.length];
			System.arraycopy(pi.activities, 0, activs, 0, pi.activities.length);
			sortActivs();
			int i = 0;
			for (int j = 0; j < activs.length; j++) {
				if (activs[j].name.equals(appActivity))
					i = j;
			}
			spinner.setSelection(i);
			if (nameWasAsPickedApp)
				((EditText) findViewById(R.id.NameInput)).setText(pm
						.getApplicationLabel(pm.getApplicationInfo(appPackage,
								0)));
		} catch (NameNotFoundException e) {
		}
	}

	private void makeSpinner() {
		PackageManager pm = getPackageManager();
		try {
			final PackageInfo pi = pm.getPackageInfo(appPackage,
					PackageManager.GET_ACTIVITIES);
			activs=new ActivityInfo[pi.activities.length];
			System.arraycopy(pi.activities, 0, activs, 0, pi.activities.length);

			sortActivs();
			spinner.setAdapter(new MySpinnerAdapter(activs));
			spinner.setEnabled(true);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					appActivity = activs[arg2].name;
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					//do nothing
				}
			});
			//when activity list is ready we can launch
			launch.setEnabled(true);
		} catch (NameNotFoundException e) {
			//hence activity doesn't exist
		}
	}

	public class MySpinnerAdapter implements SpinnerAdapter {
		ActivityInfo[] currentAppActivities;
		PackageManager pm = getPackageManager();

		public MySpinnerAdapter(ActivityInfo[] groups) {
			this.currentAppActivities = groups;

		}

		public RelativeLayout getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 30);

			RelativeLayout lay = new RelativeLayout(MotionEditor.this);
			lay.setLayoutParams(lp);
			// Center the text vertically
			lay.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			//lay.setPadding(36, 0, 0, 0);
			return lay;
		}

		public Object getItem(int position) {
			return currentAppActivities[position];
		}

		public int getCount() {
			return currentAppActivities.length;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View groupItem = null;
			//create new element if doesn't exist 
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				groupItem = inflater.inflate(R.layout.spinner_item,
						null);
			} else {
				groupItem = convertView;
			}
			//set activity name 
			TextView activityName = (TextView) groupItem
					.findViewById(R.id.activity_name);
			activityName.setText(currentAppActivities[position].loadLabel(pm));
			activityName.setTextSize(15);
			//check is it launcher and draw proper image
			boolean isLauncher = false;
			//Iterator<ResolveInfo> lst = launchers.iterator();
			/*while (lst.hasNext()) {
				ResolveInfo info = lst.next();
				if (info.activityInfo.name.equals(currentAppActivities[position].name))
					isLauncher = true;
			}*/
			isLauncher=launchers.contains(currentAppActivities[position].name);
			if (isLauncher)
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.launcher);

			else
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.not_launcher);

			return groupItem;
		}

		public boolean hasStableIds() {
			return true;
		}

		public long getItemId(int arg0) {

			return arg0;
		}

		public View getDropDownView(int position, View convertView,
				ViewGroup arg2) {
			//create new element
			View groupItem = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) spinner.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				groupItem = inflater.inflate(R.layout.activity_picker_item,
						null);
			} else {
				groupItem = convertView;
			}
			//get views
			TextView activityName = (TextView) groupItem
					.findViewById(R.id.activity_name);
			TextView pack = (TextView) groupItem.findViewById(R.id.app_package);
			//if any activity labels coincide add package information 
			boolean needPackName = false;
			CharSequence label = currentAppActivities[position].loadLabel(pm);
			for (int i = 0; i < currentAppActivities.length; i++) {
				if (i != position && currentAppActivities[i].loadLabel(pm).equals(label))
					needPackName = true;
			}
			//if need pack set the name
			if (needPackName) {
				pack.setText(currentAppActivities[position].name.toString());
			} else
				pack.setText(" "); //if there is nothing bugs occur %)
			//set activity name
			activityName.setText(label);
			activityName.setTextSize(15);
			//Iterator<ResolveInfo> lst = launchers.iterator();
			//set image about launcher
			boolean isLauncher = false;
			isLauncher=launchers.contains(currentAppActivities[position].name);
			/*while (lst.hasNext()) {
				ResolveInfo info = lst.next();
				if (info.activityInfo.name.equals(currentAppActivities[position].name))
					isLauncher = true;
			}*/
			//((ImageView) groupItem.findViewById(R.id.info_img)).set
			if (isLauncher)
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.launcher);
			
			else
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.not_launcher);
						
			// ((ImageView)groupItem.findViewById(R.id.info_img`))
			
			return groupItem;

		}

		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return SpinnerAdapter.IGNORE_ITEM_VIEW_TYPE;
		}

		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}
	}
}
