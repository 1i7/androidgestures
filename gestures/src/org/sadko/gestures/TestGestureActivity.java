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
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;

public class TestGestureActivity extends Activity {

	
	long motionId;
	int recievedMotions=0;
	TextView count;
	boolean needOff=false;
	boolean needDelete=true;
	MotionHandlerHelper helper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_gesture);
		Button ok=(Button)findViewById(R.id.tested);
		((TextView)findViewById(R.id.text)).setTextSize(40);
		count=((TextView)findViewById(R.id.match_count));
		count.setTextSize(40);
		count.setText("0");
		ok.setTextSize(50);
		ok.setHeight(100);
		ok.setWidth(100);
		ok.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				finish();
			}
		});
		if(getIntent().getExtras().containsKey("motion"))
			motionId=ContentUris.parseId(getContentResolver().insert(MotionsDB.MOTIONS_CONTENT_URI, (ContentValues)getIntent().getParcelableExtra("motion")));
		else{
			needDelete=false;
			motionId=getIntent().getLongExtra("motion_id", 0);
		}
		helper= new MotionHandlerHelper(this){

			

			@Override
			public void OnDebugGestureRegistered(long id) {
				if (id == motionId)
					increase();
				super.OnDebugGestureRegistered(id);
			}
			
		};
	}

	@Override
	protected void onPause() {
		helper.unregisterAsReceiver();
		Intent set_normal = new Intent(MotionHandlerBroadcastReceiver.ACTION_SET_MODE);
		set_normal.putExtra(MotionHandlerBroadcastReceiver.MODE_KEY, MotionHandler1.NORMAL_MODE);
		sendBroadcast(set_normal);
		helper.turnOff();
		if(needDelete)
				getContentResolver().delete(ContentUris.withAppendedId(MotionsDB.MOTIONS_CONTENT_URI, motionId), null, null);
		super.onPause();
	}
	
	private void increase(){
		++recievedMotions;
		count.setText(recievedMotions + "");
	}
	
	@Override
	protected void onResume() {
		helper.registerAsReceiver();
		Intent set_debug = new Intent(MotionHandlerBroadcastReceiver.ACTION_SET_MODE);
		set_debug.putExtra(MotionHandlerBroadcastReceiver.MODE_KEY, MotionHandler1.DEBUG_MODE);
		sendBroadcast(set_debug);
		helper.turnOn();
		super.onResume();
	}
	

}
