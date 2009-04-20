package org.sadko.gestures;


import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.TextView;

public class ListnerBinder extends Binder {
	MotionListener ms;

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
			throws RemoteException {

		return super.onTransact(code, data, reply, flags);
	}
	


	

}
