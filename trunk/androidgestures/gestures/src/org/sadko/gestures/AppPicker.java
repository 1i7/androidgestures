package org.sadko.gestures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AppPicker extends ListActivity {
	int packChosen = 0;
	int actChosen = 0;
	public static final String RESULT_CONTENT_VALUES_NAME="org.sadko.gestures.AppPicker/val"; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picker);
		// int x=R.layout.main;
		final PackageManager pm = getPackageManager();
		//RelativeLayout rl=(RelativeLayout) findViewById(R.id.item_layout);
		//rl.s
		Intent i=new Intent();
		i.setAction(android.content.Intent.ACTION_MAIN);
		i.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
		Iterator<ResolveInfo> iter=pm.queryIntentActivities(i,0).iterator();

		final List<AppRow> lst = new ArrayList<AppRow>();
		while (iter.hasNext()) {
			ResolveInfo ai = iter.next();
			AppRow ar=new AppRow();
			ar.name = ai.activityInfo.applicationInfo.loadLabel(pm);
			ar.icon= pm.getApplicationIcon(ai.activityInfo.applicationInfo);
			ar.packName=ai.activityInfo.packageName;
			lst.add(ar);
		}
		final myAdapter ad = new myAdapter(AppPicker.this, R.layout.picker_item, lst);
		ListView applications = getListView();
		applications.setAdapter(ad);
		applications.setItemsCanFocus(false);
		applications.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		applications.setItemChecked(0, true);
		applications.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i=new Intent();
				i.setAction(android.content.Intent.ACTION_MAIN);
				i.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
				Iterator<ResolveInfo> iter=pm.queryIntentActivities(i,0).iterator();
				String needPackageName=lst.get(arg2).packName;
				while(iter.hasNext()){
					ResolveInfo info=iter.next();
					if(info.activityInfo.packageName.equals(needPackageName)){
						ContentValues cv = new ContentValues();
						cv.put(ActivityColumns.PACK, needPackageName);
						cv.put(ActivityColumns.ACTIVITY,info.activityInfo.name);
						Intent intent = new Intent();
						intent.putExtra(RESULT_CONTENT_VALUES_NAME, cv);
						setResult(1, intent);
						finish();
					}
					finish();
				}

			}

		});


	}
	private class AppRow{
		Drawable icon;
		CharSequence name;
		String packName;
}
	class myAdapter extends ArrayAdapter {
		List lst;
		public myAdapter(Context context, int textViewResourceId, List objects) {
			super(context, textViewResourceId, objects);
			lst=objects;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View destinationTypeLabel = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				destinationTypeLabel = inflater.inflate(R.layout.picker_item,null);
			} else {
				destinationTypeLabel = convertView;
			}

			//String destinationType = (String) getItem(position);
			TextView destinationTypeText = (TextView) destinationTypeLabel
					.findViewById(R.id.app_name);
			destinationTypeText.setText(((AppRow)lst.get(position)).name);
			destinationTypeText.setTextSize(20);
			ImageView typeSelectedImage = (ImageView) destinationTypeLabel
					.findViewById(R.id.app_icon);
			typeSelectedImage.setImageDrawable(((AppRow)lst.get(position)).icon);
			return destinationTypeLabel;
		}

	}

}
