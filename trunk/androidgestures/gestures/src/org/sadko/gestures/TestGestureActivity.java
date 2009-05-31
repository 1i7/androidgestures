package org.sadko.gestures;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestGestureActivity extends Activity {

	
	long motionId;
	int recievedMotions=0;
	TextView count;
	boolean needOff=false;
	ServiceConnection con;
	ListnerBinder lb;
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
		Intent i=getIntent();
		Bundle b=i.getExtras();
		ContentValues cv=new ContentValues();
		for(int j=0;j<3;j++)
			for(int k=0;k<3;k++){
				
				cv.put(MotionColumns.MATRIX[j][k], b.getDouble(MotionColumns.MATRIX[j][k]));
				//cv.put(MotionColumns.NAME, "inner temporary motion");
				
			}
		motionId=ContentUris.parseId(getContentResolver().insert(MotionsDB.MOTIONS_CONTENT_URI, cv));
	}

	@Override
	protected void onPause() {
		if(needOff)lb.mh.switchMe();
		unbindService(con);
		getContentResolver().delete(ContentUris.withAppendedId(MotionsDB.MOTIONS_CONTENT_URI, motionId), null, null);
		super.onPause();
	}

	@Override
	protected void onResume() {
		con=new ServiceConnection(){
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				lb= (ListnerBinder) arg1;
				if(!lb.mh.isEnabled){
					needOff=true;
					lb.mh.switchMe();
				}
				lb.ms = new MotionListener() {
					public void onMotionRecieved(int motion) {
						if(motion==motionId){
							recievedMotions++;
							count.setText(recievedMotions+"");
						}
						
					}
					
				};
				

			}

			public void onServiceDisconnected(ComponentName arg0) {
				
				
			}
		};
			
		bindService(new Intent(this,MotionHandler1.class),con,0);
		super.onResume();
	}
	

}
