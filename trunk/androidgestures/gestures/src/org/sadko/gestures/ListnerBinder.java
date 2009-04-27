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
	public static final int ENABLE_DISABLE=1;
	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply,
			int flags) throws RemoteException {
		if(code==ENABLE_DISABLE)
			mh.isEnabled=!mh.isEnabled;
		Bundle b=new Bundle();
		b.putBoolean("on/off", mh.isEnabled);
		reply.writeBundle(b);
		//Log.i("binder","here!");
		return super.onTransact(code, data, reply, flags);
	}


	

}
