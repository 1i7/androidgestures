package org.sadko.gestures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class MotionHandler1 extends MotionHandler {
	long needTime=0;
	public static  double MOTION_SENSITIVITY = 0.1;
	long oldestTime=0;
	int ARRAY_SIZE=10;
	long lastRegisterTime=0;
	long[] times=new long[ARRAY_SIZE];
	protected double yaws[]=new double[ARRAY_SIZE];
	protected double rolls[]=new double[ARRAY_SIZE];
	protected double pitchs[]=new double[ARRAY_SIZE];
	int position=0;
	@Override
	public void addMotion(Motion motion) {
		super.addMotion(motion);
		needTime=(motion.time>needTime?motion.time:needTime);
		needTime+=1;
		ARRAY_SIZE=(int) (needTime/20)+1;
		yaws=new double[ARRAY_SIZE];
		rolls=new double[ARRAY_SIZE];
		pitchs=new double[ARRAY_SIZE];
		times=new long[ARRAY_SIZE];
		Log.i("size",ARRAY_SIZE+"");
	}

	public void onAccuracyChanged(int sensor, int accuracy) {}
	
	private void increase(){
		ARRAY_SIZE*=2;
		long[] newt=new long[ARRAY_SIZE];
		System.arraycopy(times, position+1, newt, 0, ARRAY_SIZE/2-position-1);
		System.arraycopy(times, 0, newt, ARRAY_SIZE/2-position, position+1);
		times=newt;
		double[] newy=new double[ARRAY_SIZE];
		System.arraycopy(yaws, position+1, newy, 0, ARRAY_SIZE/2-position-1);
		System.arraycopy(yaws, 0, newy, ARRAY_SIZE/2-position, position+1);
		yaws=newy;
		double[] newr=new double[ARRAY_SIZE];
		System.arraycopy(rolls, position+1, newr, 0, ARRAY_SIZE/2-position-1);
		System.arraycopy(rolls, 0, newr, ARRAY_SIZE/2-position, position+1);
		rolls=newr;
		double[] newp=new double[ARRAY_SIZE];
		System.arraycopy(pitchs, position+1, newp, 0, ARRAY_SIZE/2-position-1);
		System.arraycopy(pitchs, 0, newp, ARRAY_SIZE/2-position, position+1);
		pitchs=newp;
		position=ARRAY_SIZE/2-1;
		Log.i("array", " "+ARRAY_SIZE);
		
	}
	
	public void onSensorChanged(int sensor, float[] values) {
		
		boolean checkMotion[]=new boolean[motions.size()]; 
		times[position]=System.currentTimeMillis();
		yaws[position]=values[0]*Math.PI/180;
		pitchs[position]=values[1]*Math.PI/180;
		rolls[position]=values[2]*Math.PI/180;
		//Log.i("ahuhu"," "+position+" "+times[position]+" ");
		//for(int i=0;i<times.length;i++){Log.i("array",times[i]+" ");}
		//if(times[(position+1)%ARRAY_SIZE]!=0){
			int i=position;
			//Log.i("here i am!111","   ");
			boolean detected=false;
			//boolean timeOut=false;
			while(!detected && i!=(position+1)%ARRAY_SIZE){
				/*if(times[position]-times[i]>=needTime){ 
					timeOut=true;
					Log.i("timeout","111");
					break;
				}*/
				Iterator<Motion> j=motions.iterator();
				int s=0;
				//Log.i("here i am!",i+" ");
				while(j.hasNext()){
					
					Motion m=j.next();
					if(times[position]-times[i]-m.time>0 && !checkMotion[s]){
						checkMotion[s]=true;
						double[][] matrix=math(yaws[position],pitchs[position],rolls[position],yaws[i],pitchs[i],rolls[i]);
						double ss=0; 
						for(int k=0;k<3;k++)
							for(int l=0;l<3;l++)
								ss+=(matrix[k][l]-m.matrix[k][l])*(matrix[k][l]-m.matrix[k][l]);
						//Log.i("ss "+ s," "+ss);
						if(ss<MOTION_SENSITIVITY && System.currentTimeMillis()-lastRegisterTime>1400 && isEnabled){
							notifyListeners((int) m.id);
							lastRegisterTime=System.currentTimeMillis();
							detected=true;
							
						}
						
					}
					s++;
				}
				i=(i-1+ARRAY_SIZE)%ARRAY_SIZE;
			}
				
		//}
		position=(position+1)%ARRAY_SIZE;
	}

	@Override
	public void onCreate() {
		//File f=new File("/sdcard/motions.txt");
		Cursor c=getContentResolver().query(Uri.parse("content://org.sadko.gestures.content/motions"), new String[] {"A00","A01","A02","A10","A11","A12","A20","A21","A22","time","package","activity","_id"}, null,null, null);
		while(!c.isLast()){
			c.moveToNext();
			Motion motion=new Motion();
			for(int i=0;i<3;i++)
				for(int j=0;j<3;j++)
					motion.matrix[i][j]=c.getFloat(i+j*3);
			motion.path=c.getString(10);
			motion.activity=c.getString(11);
			motion.time=c.getLong(9);
			motion.id=c.getLong(12);
			Log.i("motion add", "added motion"+motion.time );
			addMotion(motion);
		}
	//	showNotification();
/*		try {
			Scanner s=new Scanner(f);
			while(s.hasNextDouble()){
				Motion m=new Motion();
				for(int i=0;i<3;i++)
					for(int j=0;j<3;j++)
						m.matrix[i][j]=s.nextDouble();
				m.time=s.nextLong();
				Log.i("motion add", "added motion"+m.time );
				addMotion(m);
			}
			s.close();
			Log.i("end","  ");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		super.onCreate();
	}
	private void showNotification() {
        Intent m_clickIntent = new Intent(); 
        m_clickIntent.setClass( this, StupidActivity.class); 
        m_clickIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | 
        Intent.FLAG_ACTIVITY_NEW_TASK);
        
       

        NotificationManager mNM= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //m_clickIntent.putExtra( "time", ""+((time/60000)>0?((time/60000)+" minutes "):"")+(time%60000)/1000+" seconds"); 
        Notification m_currentNotification = new 
        Notification( R.drawable.icon, "Coffee is ready!", 
        System.currentTimeMillis());
        
        m_currentNotification.setLatestEventInfo( this, 
        "Coffee is ready!", "click here to view a cup", 
        PendingIntent.getActivity(this, 0, m_clickIntent,PendingIntent.FLAG_CANCEL_CURRENT));
       // m_currentNotification.flags=Notification.FLAG_AUTO_CANCEL;
        mNM.notify( 0, 
        m_currentNotification);
        //MotionHandler1.this.
        //PendingIntent.
    }
}
