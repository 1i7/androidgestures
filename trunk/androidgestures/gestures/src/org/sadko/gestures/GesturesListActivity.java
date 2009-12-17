package org.sadko.gestures;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GesturesListActivity extends ListActivity{
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
				new String[] { "_id", MotionColumns.NAME }, null, null, null);
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
		final MySimpleAdapter motions = new MySimpleAdapter(this, R.layout.motions_row, gesturesFromDatabase,
				new String[] { MotionColumns.NAME },
				new int[] { R.id.motion_name });

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
	
	class MySimpleAdapter extends SimpleCursorAdapter {
		public MySimpleAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) super.getView(position, convertView,
					parent);
			tv.setTextSize(30);
			tv.setPadding(0, 3, 0, 3);
			return tv;
		}
	}
}
