package org.sadko.gestures;

import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MotionEditor extends Activity {
	ContentValues motionValues=new ContentValues();
	ContentValues activityValues=new ContentValues();
	String action;
	long motionId;
	long taskId;
	//Drawable appIcon;
	String appName;
	String appPackage;
	private static final int RECORD_REQEST_CODE=1;
	private static final int PICK_APP_REQUEST_CODE=2;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1)
			if(resultCode==RECORD_REQEST_CODE){
				ContentValues v=data.getParcelableExtra(Recorder.RESULT_CONTENT_VALUES_NAME);
				motionValues.putAll(v);
			}
		if(requestCode==PICK_APP_REQUEST_CODE){
			if(resultCode==1){
				ContentValues v=data.getParcelableExtra(AppPicker.RESULT_CONTENT_VALUES_NAME);
				if(v.containsKey(ActivityColumns.ACTIVITY))
				activityValues.putAll(v);
			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		
		Button record=(Button) findViewById(R.id.Record);
		action=getIntent().getAction();
		if(action.equals(android.content.Intent.ACTION_EDIT)){
			motionId=getIntent().getExtras().getLong("id");
			Cursor c1=getContentResolver().query(MotionsDB.TASKS_CONTENT_URI, null, ActivityColumns.MOTION_ID+"="+motionId, null, null);
			c1.moveToFirst();
			PackageManager pm=getPackageManager();
			Iterator<PackageInfo> iter=pm.getInstalledPackages(PackageManager.GET_ACTIVITIES).iterator();
			while(iter.hasNext()){
				PackageInfo info=iter.next();
				if(info.packageName.equals(c1.getString(c1.getColumnIndex(ActivityColumns.PACK)))){
					((ImageView)findViewById(R.id.iconka)).setImageDrawable(pm.getApplicationIcon(info.applicationInfo));
				}
			}
			taskId=c1.getLong(c1.getColumnIndex(ActivityColumns.MOTION_ID));
			Cursor c=getContentResolver().query(ContentUris.withAppendedId(MotionsDB.MOTIONS_CONTENT_URI, motionId), new String[] {MotionColumns.NAME,MotionColumns.MATRIX[0][0]},null, null, null);
			;
			
			c.moveToFirst();
			((EditText)findViewById(R.id.EditText01)).setText(c.getString(0));
			
			if(c.getString(c.getColumnIndex(MotionColumns.MATRIX[0][0]))!=null)
				record.setText("Record another gesture");
			
		}
			
		record.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent i=new Intent(MotionEditor.this,Recorder.class);
				startActivityForResult(i,RECORD_REQEST_CODE);
			}
			
		});
		Button saveExit=(Button) findViewById(R.id.sSandE);
		saveExit.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				motionValues.put(MotionColumns.NAME, ((EditText)findViewById(R.id.EditText01)).getText().toString());
				if(action.equals(android.content.Intent.ACTION_EDIT)){
					if(motionValues.size()>0)getContentResolver().update(MotionsDB.MOTIONS_CONTENT_URI, motionValues, "_ID="+motionId, null);
					if(activityValues.size()>0)getContentResolver().update(MotionsDB.TASKS_CONTENT_URI, activityValues, "_ID="+taskId, null);
					
					}
				else{
					
					motionId=ContentUris.parseId(getContentResolver().insert(MotionsDB.MOTIONS_CONTENT_URI, motionValues));
					activityValues.put(ActivityColumns.MOTION_ID, motionId);
					getContentResolver().insert(MotionsDB.TASKS_CONTENT_URI, activityValues);
					
				}
				
				finish();
			}
			
		});
		Button pick=(Button)findViewById(R.id.pick);
		pick.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent i=new Intent(MotionEditor.this,AppPicker.class);
				startActivityForResult(i,PICK_APP_REQUEST_CODE);


			}
		});
		Button delete=(Button)findViewById(R.id.delete);
		if(!action.equals(android.content.Intent.ACTION_EDIT))
			delete.setVisibility(android.widget.Button.INVISIBLE);
		delete.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			showDialog(DIALOG_YES_NO_MESSAGE);
			}
		
		});
							

	}
	static final int DIALOG_YES_NO_MESSAGE = 999;
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(MotionEditor.this).setTitle("are you sure to remove")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// User clicked OK so do some stuff
									long id=motionId;
									getContentResolver().delete(Uri.withAppendedPath(MotionColumns.CONTENT_URI, ""+id), null, null);
									MotionEditor.this.finish();
									//c.requery();
									
								}
							}).setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// User clicked Cancel so do some stuff

									//System.out.println("cancel clicked.");
								}
							}).create();
		}
		return null;
	}
	private void setPickedApp(){
		PackageManager pm=getPackageManager();
		try {
			((ImageView)findViewById(R.id.iconka)).setImageDrawable(pm.getApplicationIcon(appPackage));
			
		} catch (NameNotFoundException e) {}
	}
}
