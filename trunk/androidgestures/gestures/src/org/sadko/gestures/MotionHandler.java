package org.sadko.gestures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



//import org.openintents.hardware.SensorManagerSimulator;
//import org.openintents.provider.Hardware;

//import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public abstract class MotionHandler extends Service implements SensorListener{
	public static final int START_STOP=359;
	protected List<Motion> motions;
	boolean isEnabled=true;
	SensorManager mgr;
	public void addMotion(Motion motion){
		motions.add(motion);
	}
	
	protected List<ListnerBinder> listners=new ArrayList<ListnerBinder>();
	
	MotionHandler(){
		motions=new ArrayList<Motion>();
	}
	protected void deleteAllMotions(){
		motions.clear();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		//Log.i("serv","binding");
		if(arg0.getAction()!=null && arg0.getAction().equals("CONTROL")) return new Binder(){

			@Override
			protected boolean onTransact(int code, Parcel data, Parcel reply,
					int flags) throws RemoteException {
				isEnabled=!isEnabled;
				Log.i("binder","here!");
				return super.onTransact(code, data, reply, flags);
			}};
		if(listners.isEmpty()){
			//Hardware.mContentResolver=getContentResolver();
			//mgr=new SensorManagerSimulator((SensorManager)getSystemService(SENSOR_SERVICE));
			//SensorManagerSimulator.connectSimulator();
			mgr=(SensorManager)getSystemService(SENSOR_SERVICE);
			mgr.registerListener(this, SensorManager.SENSOR_ORIENTATION,SensorManager.SENSOR_DELAY_UI);
		}
		ListnerBinder lb=new ListnerBinder();
		lb.mh=this;
		listners.add(lb);
		return lb;
	}
	protected void notifyListeners(int motion){
		Iterator<ListnerBinder> iter=listners.iterator();
		while(iter.hasNext()){
			ListnerBinder lb=iter.next();
			if(lb.ms!=null) lb.ms.onMotionRecieved(motion);
		}
	}
	public static double[][] math(double yaw2, double pitch2, double roll2, double yaw,
    		double pitch, double roll ){

    	double[][]ans=new double[3][3];
    	ans[0][0] =  (double) (Math.sin(roll2)*Math.cos(pitch2)*Math.sin(roll)*Math.cos(pitch)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch2)*Math.cos(yaw)*Math.cos(roll)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(yaw)*Math.cos(roll)+Math.cos(yaw2)*Math.cos(roll2)*Math.cos(yaw)*Math.cos(roll)-Math.cos(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch));
    	ans[0][1] =  (double) (Math.sin(roll2)*Math.cos(pitch2)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(pitch)*Math.sin(yaw)*Math.sin(yaw2)*Math.sin(pitch2)-Math.cos(pitch)*Math.cos(yaw)*Math.sin(yaw2)*Math.cos(roll2)-Math.sin(roll2)*Math.cos(pitch)*Math.cos(yaw)*Math.cos(yaw2)*Math.sin(pitch2)+Math.cos(pitch)*Math.sin(yaw)*Math.cos(yaw2)*Math.cos(roll2) );
    	ans[0][2] =  (double) (-Math.sin(roll2)*Math.cos(pitch2)*Math.cos(pitch)*Math.cos(roll)-Math.sin(pitch2)*Math.sin(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)-Math.sin(pitch2)*Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)-Math.sin(yaw2)*Math.cos(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.cos(yaw2)*Math.cos(roll2)*Math.cos(yaw)*Math.sin(roll)+Math.sin(yaw2)*Math.cos(roll2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(pitch2)*Math.cos(yaw2)*Math.sin(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(yaw2)*Math.sin(roll2)*Math.sin(roll)*Math.sin(yaw)+Math.cos(yaw2)*Math.cos(roll2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw));
    	ans[1][0] = (double)(-Math.cos(pitch2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(yaw)*Math.cos(roll)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(pitch2)*Math.sin(roll)*Math.cos(pitch));
    	ans[1][1]= (double)(Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(pitch)*Math.sin(yaw)+Math.cos(pitch2)*Math.cos(yaw2)*Math.cos(pitch)*Math.cos(yaw)+Math.sin(pitch2)*Math.sin(pitch));
    	ans[1][2]= (double)(Math.cos(pitch2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.cos(pitch2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)+Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)-Math.cos(pitch2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(pitch2)*Math.cos(pitch)*Math.cos(roll));
    	ans[2][0]= (double)(-Math.cos(pitch2)*Math.cos(roll2)*Math.sin(roll)*Math.cos(pitch)+Math.sin(roll2)*Math.cos(yaw2)*Math.cos(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(yaw)*Math.cos(roll)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(yaw)*Math.cos(roll)-Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.cos(yaw)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)*Math.sin(pitch)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.cos(roll));
    	ans[2][1]=(double)(-Math.cos(pitch2)*Math.cos(roll2)*Math.sin(pitch)-Math.sin(roll2)*Math.cos(pitch)*Math.cos(yaw)*Math.sin(yaw2)+Math.cos(pitch)*Math.sin(yaw)*Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)+Math.sin(roll2)*Math.cos(pitch)*Math.sin(yaw)*Math.cos(yaw2)+Math.cos(pitch)*Math.cos(yaw)*Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2));
    	ans[2][2]=(double)(Math.cos(pitch2)*Math.cos(roll2)*Math.cos(pitch)*Math.cos(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.cos(yaw)*Math.sin(roll)+Math.sin(roll2)*Math.cos(yaw2)*Math.cos(yaw)*Math.sin(roll)-Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(roll)*Math.sin(yaw)-Math.sin(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.cos(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.cos(yaw)+Math.sin(pitch2)*Math.cos(roll2)*Math.sin(yaw2)*Math.sin(pitch)*Math.cos(roll)*Math.sin(yaw)+Math.sin(roll2)*Math.sin(yaw2)*Math.sin(roll)*Math.sin(yaw));
     	return ans;
    	 
    }
	protected void showNotification() {
		if(!isEnabled) {
			killNotification();
			return;
		}
		Intent m_clickIntent = new Intent();
		m_clickIntent.setClass(this, Manager.class);
		m_clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification m_currentNotification = new Notification(R.drawable.icon2,
				"Service is running", System.currentTimeMillis());
		m_currentNotification.setLatestEventInfo(this, "Service is running",
				"click here to change state", PendingIntent.getActivity(this, 0,
						m_clickIntent, PendingIntent.FLAG_CANCEL_CURRENT));
		mNM.notify(0, m_currentNotification);

	}
	protected void killNotification(){
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(0);
	}
	public void switchMe() {
		if(isEnabled)
			mgr.unregisterListener(this);
		else
			mgr.registerListener(this, SensorManager.SENSOR_ORIENTATION,SensorManager.SENSOR_DELAY_UI);
		isEnabled= !isEnabled;
		// TODO Auto-generated method stub
		
	}
}
