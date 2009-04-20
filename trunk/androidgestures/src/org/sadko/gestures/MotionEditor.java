package org.sadko.gestures;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MotionEditor extends Activity {
	ContentValues values=new ContentValues();
	String action;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1)
			if(resultCode==1){
				ContentValues v=data.getParcelableExtra("val");
				values.putAll(v);
			}
		if(requestCode==17){
			if(resultCode==1){
				ContentValues v=data.getParcelableExtra("vala");
				values.putAll(v);
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
			long id=getIntent().getExtras().getLong("id");
			Cursor c=getContentResolver().query(Uri.withAppendedPath(MotionColumns.CONTENT_URI, id+""), new String[] {MotionColumns.NAME,MotionColumns.PACK,MotionColumns.ACTIVITY},null, null, null);
			;
			
			c.moveToFirst();
			((EditText)findViewById(R.id.EditText01)).setText(c.getString(0));
			
		}
			
		record.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent i=new Intent(MotionEditor.this,Recorder.class);
				startActivityForResult(i,1);
			}
			
		});
		Button saveExit=(Button) findViewById(R.id.sSandE);
		saveExit.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				values.put(MotionColumns.NAME, ((EditText)findViewById(R.id.EditText01)).getText().toString());
				Uri ur= getContentResolver().insert(Uri.withAppendedPath(MotionsDB.CONTENT_URI, "motions"), values);
				
				finish();
			}
			
		});
		Button pick=(Button)findViewById(R.id.pick);
		pick.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent i=new Intent(MotionEditor.this,AppPicker.class);
				
				//i.setClassName("com.android.development", "com.android.development.AppPicker");
				startActivityForResult(i,17);


			}
		});
		

	}

}
