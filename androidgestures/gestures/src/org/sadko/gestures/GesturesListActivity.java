package org.sadko.gestures;


import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GesturesListActivity extends ListActivity{
	private static final int ADD_NEW_ID = 0;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD_NEW_ID, 0, "Add new").setIcon(
				android.R.drawable.ic_menu_add);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_NEW_ID: {
			Intent i = new Intent(GesturesListActivity.this, MotionEditor.class);
			i.setAction(android.content.Intent.ACTION_MAIN);
			startActivity(i);
			break;
		}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	Cursor gesturesFromDatabase;
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestures_library_list_layout);
		lv = getListView();
		fillListView();
	}
	
	private void fillListView() {
		gesturesFromDatabase = getContentResolver().query(MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "_id", MotionColumns.NAME, MotionColumns.MODIFIED_DATE, MotionColumns.ISACTIVE }, null, null, null);
		startManagingCursor(gesturesFromDatabase);
		/*gesturesFromDatabase.registerContentObserver(new ContentObserver(new Handler() {

		}) {
		

			@Override
			public void onChange(boolean selfChange) {
				if (gesturesFromDatabase.getCount() == 0 && !lb.mh.isEnabled)
					startMyService.setEnabled(false);
				else
					startMyService.setEnabled(true);

			}

		});*/
		final MySimpleAdapter motions = new MySimpleAdapter(this,gesturesFromDatabase);

		lv.setAdapter(motions);
		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(GesturesListActivity.this, MotionEditor.class);
				i.setAction(android.content.Intent.ACTION_EDIT);
				i.putExtra("id", motions.getItemId(arg2));
				startActivity(i);
			}
		});
		Button addFirst = (Button) findViewById(R.id.add_first);
		addFirst.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent i = new Intent(GesturesListActivity.this, MotionEditor.class);
				i.setAction(android.content.Intent.ACTION_MAIN);
				startActivity(i);
			}

		});

	}
	
	class MySimpleAdapter extends CursorAdapter {
		public MySimpleAdapter(Context context, Cursor c) {
			super(context, c);
		}
		PackageManager mPackageManager = GesturesListActivity.this.getPackageManager();

		void fillData(Cursor cursor, View view){
			TextView name = (TextView) view.findViewById(R.id.gesture_name);
			TextView dateTextView = (TextView) view.findViewById(R.id.modified_date);
			Date mDate = new Date();
			Long date = cursor.getLong(cursor.getColumnIndex(MotionColumns.MODIFIED_DATE));
			if (date != null){
				mDate.setTime(date);
				dateTextView.setText(mDate.toLocaleString());
			}
			dateTextView.setTextColor(Color.BLACK);
			name.setTextSize(20);
			name.setTextColor(Color.BLACK);
			name.setText(cursor.getString(cursor.getColumnIndex(MotionColumns.NAME)));
			ImageView isActive = (ImageView) view.findViewById(R.id.activated_indicator);
			boolean isactive = cursor.getInt(cursor.getColumnIndex(MotionColumns.ISACTIVE)) == 0 ? false : true;
			isActive.setImageResource(isactive ? R.drawable.widget_on : R.drawable.widget_off);
		}
		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			fillData(arg2, arg0);
			
		}
		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			LayoutInflater inflater = (LayoutInflater)arg0
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View result = inflater.inflate(R.layout.library_item,null);
			result.setBackgroundResource(R.drawable.background_lib);
			fillData(arg1, result);
			return result;
			
		}
	}
}
