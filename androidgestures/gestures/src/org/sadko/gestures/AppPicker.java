package org.sadko.gestures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AppPicker extends Activity {
	int packChosen = 0;
	int actChosen = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picker);
		// int x=R.layout.main;
		PackageManager pm = getPackageManager();

		List<ApplicationInfo> apinfs = pm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);

		List<PackageInfo> apps = pm
				.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		Iterator<PackageInfo> i = apps.iterator();
		//Map<String, Object> m = new HashMap<String, Object>();
		// m.keySet().add("name",)
		// Iterator<ApplicationInfo> i1=apinfs.iterator();
		//List<Map<String, Object>> forView = new ArrayList<Map<String, Object>>();
		final List<AppRow> lst = new ArrayList<AppRow>();
		//final List[] activs = new List[apps.size()];
		//int l = 0;
		while (i.hasNext()) {
			PackageInfo ai = i.next();
			AppRow ar=new AppRow();
			
			// ai.applicationInfo.
			// ActivityManager am=(ActivityManager)
			// getSystemService(ACTIVITY_SERVICE);
			ar.name = ai.applicationInfo.loadLabel(pm);
			ar.icon= pm.getApplicationIcon(ai.applicationInfo);
			ar.packName=ai.packageName;
			lst.add(ar);
			// pm.queryIntentActivities(inte, 0);
			/*
			 * Map<String,Object> tmp=new HashMap<String,Object>(); if(ai!=null
			 * && ai.packageName!=null) lst.add(ai.packageName); ActivityInfo[]
			 * infos=ai.activities; if(infos!=null){ activs[l]=new ArrayList();
			 * for(int k=0;k<infos.length;k++) if(infos[k]!=null &&
			 * infos[k].name!=null) activs[l].add(infos[k].name);
			 * 
			 * }else { activs[l]=new ArrayList(); activs[l].add("nothing"); }
			 * 
			 * forView.add(tmp); l++; //Log.i("pack","the end");
			 */
		}
		final myAdapter ad = new myAdapter(AppPicker.this, R.layout.picker_item, lst);
		/*final ArrayAdapter[] ads = new ArrayAdapter[apps.size()];
		for (int k = 0; k < ads.length; k++) {

			ads[k] = new ArrayAdapter(AppPicker.this,
					android.R.layout.simple_list_item_checked, activs[k]);
		}*/
		//Log.i("length", ads.length + "");
		ListView applications = (ListView) findViewById(R.id.apps);
		//final ListView activities = (ListView) findViewById(R.id.activities);
		applications.setAdapter(ad);
		applications.setItemsCanFocus(false);
		applications.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		/*activities.setItemsCanFocus(false);
		activities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		activities.setAdapter(ads[0]);*/
		applications.setItemChecked(0, true);
		//activities.setItemChecked(0, true);

		/*applications.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//activities.setAdapter(ads[arg2]);
				//activities.setItemChecked(0, true);
				packChosen = arg2;
			}

		});*/
		//activities.setOnItemClickListener(new OnItemClickListener() {

	/*		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				actChosen = arg2;
			}

		});*/
		Button ok = (Button) findViewById(R.id.okay);
		Button finish = (Button) findViewById(R.id.no);
		ok.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//ContentValues cv = new ContentValues();
				//cv.put(ActivityColumns.PACK, lst.get(packChosen));
				//cv.put(ActivityColumns.ACTIVITY, (String) activs[packChosen]
				//		.get(actChosen));
				Intent i = new Intent();
				//i.putExtra("vala", cv);
				//setResult(1, i);
				finish();
			}

		});
		finish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(0);
				finish();

			}

		});
		// setListAdapter(ad);

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

			ImageView typeSelectedImage = (ImageView) destinationTypeLabel
					.findViewById(R.id.app_icon);
			typeSelectedImage.setImageDrawable(((AppRow)lst.get(position)).icon);
			return destinationTypeLabel;
		}

	}

	public class CustomAdapter<E> extends ArrayAdapter<E> {

		private View sentinel;

		public CustomAdapter(Context context, int textViewResourceId,
				List<E> objects) {
			super(context, textViewResourceId, objects);
			sentinel = new TextView(context);
			((TextView) sentinel).setText("Still Loading..");
		}

		@Override 
        public View getView(int position, View convertView, ViewGroup parent) 
{ 
                if(position == 0) 
                        return sentinel;
                
                return super.getView(position, convertView, parent); 
        }		public void finish() {
			// called when async list computation finishes
			remove(getItem(0));
		}

	}

}
