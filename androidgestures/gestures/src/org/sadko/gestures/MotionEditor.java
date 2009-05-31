package org.sadko.gestures;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class MotionEditor extends Activity {
	ContentValues motionValues = new ContentValues();
	ContentValues activityValues = new ContentValues();
	String action;
	long motionId;
	long taskId;
	Button launch;
	String appActivity;
	// Drawable appIcon;
	String appName;
	String appPackage;
	Spinner spinner;
	Button testGesture;
	Button saveExit;
	List<ResolveInfo> launchers;
	private static final int RECORD_REQEST_CODE = 1;
	private static final int PICK_APP_REQUEST_CODE = 2;

	boolean nameWasAsPickedApp = false;
	String oldAppName;

	@Override
	protected void onPause() {
		super.onPause();
		if(isFinishing()){
			saveGesture();
		}
	}
	void saveGesture(){
		PackageManager pm = getPackageManager();
		if (!(((EditText) findViewById(R.id.NameInput)).getText()
				.toString().equals("") || (oldAppName != null && ((EditText) findViewById(R.id.NameInput))
				.getText().toString().equals(oldAppName))))
			motionValues.put(MotionColumns.NAME,
					((EditText) findViewById(R.id.NameInput)).getText()
							.toString());
		else {
			//Log.i("naming", "tuta");
			try {
				//Log.i("nameWasPA", nameWasAsPickedApp + "");
				motionValues.put(MotionColumns.NAME, pm
						.getApplicationLabel(
								pm.getApplicationInfo(appPackage, 0))
						.toString());
			} catch (NameNotFoundException e1) {
				motionValues.put(MotionColumns.NAME, "no name");
			}
		}

		if (action.equals(android.content.Intent.ACTION_EDIT)) {
			if (motionValues.size() > 0)
				getContentResolver().update(
						MotionsDB.MOTIONS_CONTENT_URI, motionValues,
						"_ID=" + motionId, null);
			// if(activityValues.size()>0){

			try {

				activityValues
						.put(
								ActivityColumns.ACTIVITY,
								pm.getPackageInfo(appPackage,
										PackageManager.GET_ACTIVITIES).activities[(int) spinner
										.getSelectedItemId()].name);
				Log.i("chosen", activityValues
						.getAsString(ActivityColumns.ACTIVITY)
						+ "^)");
			} catch (NameNotFoundException e) {
				// Log.e("error","no name!");
				// Toast.makeText(MotionEditor.this, "An error ocuured",
				// 1000).show();
				// e.printStackTrace();
			}
			if (activityValues.size() > 0)
				getContentResolver().update(
						MotionsDB.TASKS_CONTENT_URI, activityValues,
						"_ID=" + taskId, null);
			// }
		} else {
			motionId = ContentUris.parseId(getContentResolver().insert(
					MotionsDB.MOTIONS_CONTENT_URI, motionValues));
			if (activityValues.size() > 0) {

				try {
					activityValues
							.put(
									ActivityColumns.ACTIVITY,
									pm
											.getPackageInfo(
													appPackage,
													PackageManager.GET_ACTIVITIES).activities[(int) spinner
											.getSelectedItemId()].name);
				} catch (NameNotFoundException e) {
					Toast.makeText(MotionEditor.this,
							"An error ocuured", 1000).show();
					// e.printStackTrace();
				}
			}
			activityValues.put(ActivityColumns.MOTION_ID, motionId);
			getContentResolver().insert(MotionsDB.TASKS_CONTENT_URI,
					activityValues);
		}
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
				setPickedApp();
				makeSpinner();

				// ((TextView)findViewById(R.id.app_name_in_edit))
			}
		}
		// super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		;
		intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
		launchers = getPackageManager().queryIntentActivities(intent, 0);
		spinner = (Spinner) findViewById(R.id.activity_spinner);
		spinner.setEnabled(false);
		((TextView) findViewById(R.id.app_name_in_edit)).setTextSize(20);
		ImageButton record = (ImageButton) findViewById(R.id.Record);
		action = getIntent().getAction();
		Button discard = (Button) findViewById(R.id.discard);
		discard.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}

		});
		testGesture=(Button)findViewById(R.id.test_gesture);
		testGesture.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				//Bundle b=new Bundle();
				/*for(int j=0;j<3;j++)
					for(int k=0;k<3;k++)
						b.putDouble(MotionColumns.MATRIX[j][k], motionValues.getAsDouble(MotionColumns.MATRIX[j][k]));
				b.putDouble(MotionColumns.TIME, motionValues.getAsLong(MotionColumns.TIME));*/
				Intent i=new Intent(MotionEditor.this,TestGestureActivity.class);
				if(action.equals(android.content.Intent.ACTION_EDIT) && motionValues.size()==0){
					i.putExtra("motion_id", motionId);
					
				}else{
				i.putExtra("motion", motionValues);
				}
				
				//i.putExtras(b);
				
				startActivity(i);
				
				
			}
			
		});
		launch = (Button) findViewById(R.id.launch_now);
		launch.setEnabled(false);
		launch.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent i = new Intent();
				PackageManager pm = getPackageManager();
				try {
					i
							.setClassName(
									appPackage,
									pm.getPackageInfo(appPackage,
											PackageManager.GET_ACTIVITIES).activities[(int) spinner
											.getSelectedItemId()].name);
					startActivity(i);
				} catch (Exception e) {
					Toast.makeText(MotionEditor.this,
							"cannot start this activity", 1000).show();
				}

			}

		});
		if (action.equals(android.content.Intent.ACTION_EDIT)) {
			motionId = getIntent().getExtras().getLong("id");
			Cursor c1 = getContentResolver().query(MotionsDB.TASKS_CONTENT_URI,
					null, ActivityColumns.MOTION_ID + "=" + motionId, null,
					null);
			c1.moveToFirst();
			PackageManager pm = getPackageManager();
			//Iterator<PackageInfo> iter = pm.getInstalledPackages(
			//		PackageManager.GET_ACTIVITIES).iterator();
			try{
			appPackage = c1.getString(c1.getColumnIndex(ActivityColumns.PACK));
			appActivity = c1.getString(c1
					.getColumnIndex(ActivityColumns.ACTIVITY));
			}catch(CursorIndexOutOfBoundsException e){
				
			}
			setPickedApp();
			makeSpinner();
			taskId = c1.getLong(c1.getColumnIndex(ActivityColumns.MOTION_ID));
			Cursor c = getContentResolver().query(
					ContentUris.withAppendedId(MotionsDB.MOTIONS_CONTENT_URI,
							motionId),
					new String[] { MotionColumns.NAME, MotionColumns.TIME}, null, null, null);
			
			c.moveToFirst();
			try {
				oldAppName = pm.getApplicationLabel(
						pm.getApplicationInfo(appPackage, 0)).toString();

			} catch (NameNotFoundException e) {
				oldAppName = null;
			}
			if(c.getString(0)!=null)
				((EditText) findViewById(R.id.NameInput)).setText(c.getString(0));
			if(!c.isNull(1)) testGesture.setEnabled(true);

			c.close();
			c1.close();
				
		}

		record.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MotionEditor.this, Recorder.class);
				startActivityForResult(i, RECORD_REQEST_CODE);
			}

		});

		saveExit= (Button) findViewById(R.id.sSandE);
		saveExit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {


				//Log.i("spin", spinner.getSelectedItemId() + "");
				saveGesture();
				finish();
			}

		});
		ImageButton pick = (ImageButton) findViewById(R.id.pick);
		pick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MotionEditor.this, AppPicker.class);
				startActivityForResult(i, PICK_APP_REQUEST_CODE);
			}
		});
		Button delete = (Button) findViewById(R.id.delete);
		if (!action.equals(android.content.Intent.ACTION_EDIT))
			delete.setVisibility(android.widget.Button.INVISIBLE);
		delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_YES_NO_MESSAGE);
			}

		});

	}

	static final int DIALOG_YES_NO_MESSAGE = 999;

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

	private void setPickedApp() {
		PackageManager pm = getPackageManager();
		try {
			ImageButton pickButton = ((ImageButton) findViewById(R.id.pick));
			// pickButton.setAdjustViewBounds(true);
			pickButton.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
			// pickButton.setMaxHeight(45);
			pickButton.setImageDrawable(pm.getApplicationIcon(appPackage));

			((TextView) findViewById(R.id.app_name_in_edit)).setText(pm
					.getApplicationLabel(pm.getApplicationInfo(appPackage, 0)));
			PackageInfo pi = pm.getPackageInfo(appPackage,
					PackageManager.GET_ACTIVITIES);
			int i = 1;
			for (int j = 0; j < pi.activities.length; j++) {
				if (pi.activities[j].name.equals(appActivity))
					i = j;
			}
			// Log.i("activit",appActivity+") "+i);
			spinner.setSelection(i);
			// spinner.
			// Log.i("activit",appActivity+") "+i+" "+spinner.getSelectedItemPosition());

		} catch (NameNotFoundException e) {
		}
	}

	private void makeSpinner() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(appPackage,
					PackageManager.GET_ACTIVITIES);
			spinner.setAdapter(new MySpinnerAdapter(pi.activities));
			spinner.setEnabled(true);

			launch.setEnabled(true);

		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

	}

	public class MySpinnerAdapter implements SpinnerAdapter {
		ActivityInfo[] groups;
		// List<ActivityInfo[]> children;
		PackageManager pm = getPackageManager();

		public MySpinnerAdapter(ActivityInfo[] groups) {
			this.groups = groups;

		}

		public RelativeLayout getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			RelativeLayout lay = new RelativeLayout(MotionEditor.this);
			lay.setLayoutParams(lp);
			// Center the text vertically
			lay.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			lay.setPadding(36, 0, 0, 0);
			return lay;
		}

		public Object getItem(int groupPosition) {
			return groups[groupPosition];
		}

		public int getCount() {
			return groups.length;
		}

		public View getView(int groupPosition, View convertView,
				ViewGroup parent) {
			View groupItem = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				groupItem = inflater.inflate(R.layout.activity_picker_item,
						null);
				// groupItem.setPadding(36, 0, 0, 0);
			} else {
				groupItem = convertView;
			}

			// String destinationType = (String) getItem(position);
			TextView destinationTypeText = (TextView) groupItem
					.findViewById(R.id.app_name);
			destinationTypeText.setText(groups[groupPosition].loadLabel(pm));
			destinationTypeText.setTextSize(15);

			boolean isLauncher = false;
			Iterator<ResolveInfo> lst = launchers.iterator();
			while (lst.hasNext()) {
				ResolveInfo info = lst.next();
				if (info.activityInfo.name.equals(groups[groupPosition].name))
					isLauncher = true;
			}
			// Log.i("achtung!!!", groups[groupPosition].name);
			// TextView pack=(TextView)
			// groupItem.findViewById(R.id.app_package);
			// pack.setText(groups[groupPosition].name);
			if (isLauncher)
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.iphone);

			else
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.iphone_icons);

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
			View groupItem = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) spinner.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				groupItem = inflater.inflate(R.layout.activity_picker_item,
						null);
				// groupItem.setPadding(36, 0, 0, 0);
			} else {
				groupItem = convertView;
			}

			// String destinationType = (String) getItem(position);
			TextView destinationTypeText = (TextView) groupItem
					.findViewById(R.id.app_name);
			// destinationTypeText.setHeight(30);
			boolean needPackName = false;
			CharSequence label = groups[position].loadLabel(pm);
			for (int i = 0; i < groups.length; i++) {
				if (groups[i].loadLabel(pm).equals(label) && position != i)
					needPackName = true;
			}
			if (needPackName) {
				TextView pack = (TextView) groupItem
						.findViewById(R.id.app_package);
				pack.setText(groups[position].name);
			}
			destinationTypeText.setText(groups[position].loadLabel(pm));
			destinationTypeText.setTextSize(15);
			Iterator<ResolveInfo> lst = launchers.iterator();
			boolean isLauncher = false;
			while (lst.hasNext()) {
				ResolveInfo info = lst.next();
				if (info.activityInfo.name.equals(groups[position].name))
					isLauncher = true;
			}
			if (isLauncher)
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.iphone);
			else
				((ImageView) groupItem.findViewById(R.id.info_img))
						.setImageResource(R.drawable.iphone_icons);
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
