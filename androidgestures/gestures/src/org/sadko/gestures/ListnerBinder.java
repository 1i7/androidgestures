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
