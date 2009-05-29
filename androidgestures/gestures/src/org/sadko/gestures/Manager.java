package org.sadko.gestures;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Manager extends Activity {
	ListView lv;
	Cursor c;
	private static final int ADD_NEW_ID = 0;
	private static final int EXIT_ID = 1;
	private static final int KILL_SERVICE_ID = 2;
	Button startMyService;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD_NEW_ID, 0, "Add new");
		menu.add(0, EXIT_ID, 0, "Exit");
		//menu.add(0, KILL_SERVICE_ID, 0, "Kill handling service");
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_NEW_ID: {
			Intent i = new Intent(Manager.this, MotionEditor.class);
			i.setAction(android.content.Intent.ACTION_MAIN);
			startActivity(i);
			break;
		}
		case EXIT_ID: {
			Manager.this.finish();
			break;
		}
		/*case KILL_SERVICE_ID: {
			//Manager.this.finish();
			lb.mh.killNotification();
			stopService(new Intent(Manager.this, MotionHandler1.class));
			lb=null;
			break;
		}*/
		}
		return super.onMenuItemSelected(featureId, item);
	}

	SimpleCursorAdapter motions;
	int selectedItem = -1;
	ListnerBinder lb = null;
	ServiceConnection con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		startMyService = (Button) findViewById(R.id.service_start);
		startService(new Intent(this,MotionHandler1.class));
		startMyService.setEnabled(false);
		
		/*if(savedInstanceState!=null && savedInstanceState.containsKey("process"))
			startMyService.setText(savedInstanceState.getBoolean("process")?"stop":"start");*/

		lv = (ListView) findViewById(R.id.motions_list);
		fillListView();
		startMyService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					switchService();
					startMyService.setText(isServiceEnabled()? "stop" : "start");
					lb.mh.showNotification();

				}

			

		});
		
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		unbindService(con);
	}

	@Override
	protected void onStart() {
		super.onStart();
		con=new ServiceConnection(){
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				lb = (ListnerBinder) arg1;
				Log.i("handler", lb.mh+"");
				startMyService.setText(lb.mh.isEnabled? "stop" : "start");
				Cursor c = getContentResolver().query(
						MotionsDB.MOTIONS_CONTENT_URI,
						new String[] { "count(_ID)" }, null, null, null);
				c.moveToFirst();
				if(c.getInt(0) == 0) return;
				lb.ms = new MotionListener() {
					public void onMotionRecieved(int motion) {
						Cursor c = getContentResolver().query(
								MotionsDB.TASKS_CONTENT_URI,
								new String[] {
										ActivityColumns.PACK,
										ActivityColumns.ACTIVITY },
								ActivityColumns.MOTION_ID + "="
										+ motion, null, null);
						while (!c.isLast()) {
							c.moveToNext();
							Intent i = new Intent();
							
							i.setClassName(c.getString(0), c
									.getString(1));
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							
							try{
								Log.i("startActivity", "begin");
							lb.mh.startActivity(i);
							
							Log.i("startActivity", "end");
							}catch(Exception e){
								
								Log.i("startActivity", "failed");
								Toast.makeText(lb.mh, "cant't start activity", 1000).show();
								e.printStackTrace();
							}
							
						}
					}
					
				};
				startMyService.setEnabled(true);

			}

			public void onServiceDisconnected(ComponentName arg0) {
				startMyService.setEnabled(false);
				
			}
		};
			
		bindService(new Intent(this,MotionHandler1.class),con,0);
	
	}

	private void fillListView(){
		c = getContentResolver().query(MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "_id", MotionColumns.NAME }, null, null, null);
		startManagingCursor(c);
		motions = new SimpleCursorAdapter(this, R.layout.motions_row, c,
				new String[] { MotionColumns.NAME },
				new int[] { R.id.motion_name });
		lv.setAdapter(motions);
		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(Manager.this, MotionEditor.class);
				i.setAction(android.content.Intent.ACTION_EDIT);
				i.putExtra("id", motions.getItemId(arg2));
				startActivity(i);
			}
		});
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View empty=inflater.inflate(R.layout.start_view, null);
		lv.setEmptyView(empty);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		if(lb!=null)
			outState.putBoolean("process",isServiceEnabled() );
		//unbindService(con);
	}

	boolean isServiceEnabled(){
		//Parcel p = Parcel.obtain();
		//try {
			return lb.mh.isEnabled;//.transact(ListnerBinder.GET_STATUS, null, p, 0);
//		} catch (RemoteException e) {}
	//	return p.readBundle().getBoolean("on/off");
		
	}
	void switchService(){
		//try {
			lb.mh.switchMe();//transact(ListnerBinder.SWITCH_CODE, null, null, 0);
		//} catch (RemoteException e) {}
	}
}
