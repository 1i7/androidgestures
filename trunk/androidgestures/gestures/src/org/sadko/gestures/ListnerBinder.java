package org.sadko.gestures;


import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

public class ListnerBinder extends Binder {
	MotionListener ms;
	MotionHandler mh;
	public static final int GET_STATUS=1;
	public static final int SWITCH_CODE = 0;
	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply,
			int flags) throws RemoteException {
		switch(code){
		case GET_STATUS:{
			Bundle b=new Bundle();
			b.putBoolean("on/off", mh.isEnabled);
			reply.writeBundle(b);
		}
		case SWITCH_CODE:{
			mh.switchMe();
		}
		}
		//Log.i("binder","here!");
		return super.onTransact(code, data, reply, flags);
	}


	

}
