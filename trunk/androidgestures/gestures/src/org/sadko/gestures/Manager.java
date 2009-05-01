package org.sadko.gestures;


import android.app.Activity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Manager extends Activity {
	ListView lv;
	Cursor c;
	SimpleCursorAdapter motions;
	int selectedItem=-1;
	ListnerBinder lb=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		c = getContentResolver().query(
				MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "_id", MotionColumns.NAME },
				null, null, null); 
		startManagingCursor(c);
		motions = new SimpleCursorAdapter(this,
				 android.R.layout.simple_list_item_single_choice, c, new String[] { MotionColumns.NAME},
				new int[] { android.R.id.text1});
		lv = (ListView) findViewById(R.id.ListView01);
		lv.setAdapter(motions);
        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onClick(View v) {
				Log.i("list",""+((ListView)lv).getCheckedItemPosition());
			}

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedItem=arg2;
				
			}

		});
		
		Button addNew = (Button) findViewById(R.id.add_new);
		Button delete = (Button) findViewById(R.id.delete);
		Button modify = (Button) findViewById(R.id.modify);
		Button exit = (Button) findViewById(R.id.exit);
		final Button startMyService = (Button) findViewById(R.id.service_start);
		exit.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Manager.this.finish();
			}
			
		});
		startMyService.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(lb==null){
				ServiceConnection sc=new ServiceConnection(){
		        	
					public void onServiceConnected(ComponentName arg0, IBinder arg1) {
						lb=(ListnerBinder) arg1;

						lb.ms=new MotionListener(){

							public void onMotionRecieved(int motion) {
								Cursor c=getContentResolver().query(MotionsDB.TASKS_CONTENT_URI, new String [] {"package","activity"}, ActivityColumns.MOTION_ID+"="+motion, null, null);
								while(!c.isLast()){
									c.moveToNext();
									Intent i=new Intent();
									i.setClassName(c.getString(0),c.getString(1));
									startActivity(i);
								}
							}
							
						};
					}
					public void onServiceDisconnected(ComponentName arg0) {}
		        };
		        Intent i=new Intent(Manager.this, MotionHandler1.class);
		        bindService(i,sc,Context.BIND_AUTO_CREATE);
		        startMyService.setText("stop service");
				}
				else {
					try {
						Parcel p=Parcel.obtain();
						lb.transact(ListnerBinder.ENABLE_DISABLE, null, p, 0);
						startMyService.setText(p.readBundle().getBoolean("on/off")?"stop":"start");
						lb.mh.showNotification();
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
					//lb.mh.isEnabled
				}
		        
		    }
			
			
		});
		addNew.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i=new Intent(Manager.this, MotionEditor.class);
				i.setAction(android.content.Intent.ACTION_MAIN);
				
				startActivity(i);

			}

		});
		delete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(DIALOG_YES_NO_MESSAGE);

			}

		});
		modify.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent i=new Intent(Manager.this, MotionEditor.class);
				i.setAction(android.content.Intent.ACTION_EDIT);
				i.putExtra("id", motions.getItemId(selectedItem));
			//	Log.i("mana",""+i.getExtras().get)
				startActivity(i);
				
			}
			
		});
		
	}
	
	static final int DIALOG_YES_NO_MESSAGE = 999;

	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(Manager.this).setTitle("are you sure to remove")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// User clicked OK so do some stuff
									Log.i("selected", lv.getSelectedItemPosition()+ "!!!");
									long id=motions.getItemId(selectedItem);
									getContentResolver().delete(Uri.withAppendedPath(MotionColumns.CONTENT_URI, ""+id), null, null);
									c.requery();
									
								}
							}).setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									// User clicked Cancel so do some stuff

									System.out.println("cancel clicked.");
								}
							}).create();
		}
		return null;
	}
}
