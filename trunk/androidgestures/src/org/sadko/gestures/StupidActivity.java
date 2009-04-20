package org.sadko.gestures;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class StupidActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		ServiceConnection svc=new ServiceConnection(){

			public void onServiceConnected(ComponentName name, IBinder service) {
				try {
					Log.i("sConnection", "i am here");
					service.transact(MotionHandler.START_STOP, null, null, 0);
					StupidActivity.this.unbindService(this);
					StupidActivity.this.finish();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}

			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
			}
			
		};
		Intent j=new Intent(this,MotionHandler1.class);
		j.setAction("CONTROL");
		Log.i("sConnection", "i am here");
		bindService(j,svc,0);
	}
	

}
