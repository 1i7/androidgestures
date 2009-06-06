package org.sadko.gestures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AppPicker extends ListActivity implements Runnable {
	int packChosen = 0;
	int actChosen = 0;
	ProgressDialog pd;
	List<PackageInfo> apps;
	MyExpandableListAdapter adapter;
	public static final String RESULT_CONTENT_VALUES_NAME="org.sadko.gestures.AppPicker/val";
	MyExpandableListAdapter buildAdapter(){
		final PackageManager pm = getPackageManager();
		//Intent launcher=new Intent();
		//launcher.setAction(android.content.Intent.ACTION_MAIN);
		//launcher.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
		apps=pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		Iterator<PackageInfo> appsIter=apps.iterator();
		final List<ActivityInfo[]> activities = new ArrayList<ActivityInfo[]>();
		int k=0;
		while(appsIter.hasNext()){			
			activities.add(k,appsIter.next().activities);
			k++;
		}
		//final PackageManager pm=getPackageManager();
		TreeSet<PackageInfo> apssSet=new TreeSet<PackageInfo>(new Comparator<PackageInfo>(){

			public int compare(PackageInfo object1, PackageInfo object2) {
				PackageInfo arg1=(PackageInfo)object1;
				PackageInfo arg2=(PackageInfo)object2;
				if(arg1.applicationInfo.loadLabel(pm)==null) return -1;
				if(arg2.applicationInfo.loadLabel(pm)==null) return 1;
				return arg1.applicationInfo.loadLabel(pm).toString().compareTo( arg2.applicationInfo.loadLabel(pm).toString());
			}
			
		});
		apssSet.addAll(apps);
		apps=new ArrayList<PackageInfo>(apssSet);
		MyExpandableListAdapter mAdapter=new MyExpandableListAdapter(apps);
		return mAdapter;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picker);
		final PackageManager pm = getPackageManager();
		
		ListView applications = getListView();//this.getListView()
		
		//applications.setAdapter(mAdapter);
		applications.setItemsCanFocus(false);
		applications.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		applications.setItemChecked(0, true);
		applications.setOnItemClickListener(new OnItemClickListener(){



			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i=new Intent();
				i.setAction(android.content.Intent.ACTION_MAIN);
				i.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
				Iterator<ResolveInfo> iter=pm.queryIntentActivities(i,0).iterator();
				String needPackageName=apps.get(arg2).packageName;
				ContentValues cv = new ContentValues();
				cv.put(ActivityColumns.PACK, needPackageName);
				Intent intent = new Intent();
				while(iter.hasNext()){
					ResolveInfo info=iter.next();
					if(info.activityInfo.packageName.equals(needPackageName)){
						cv.put(ActivityColumns.ACTIVITY,info.activityInfo.name);
						Log.i("add app", needPackageName+"/"+info.activityInfo.name);
						intent.putExtra(RESULT_CONTENT_VALUES_NAME, cv);
						setResult(1, intent);
						finish();
						return;
					}
					//finish();
				}
				intent.putExtra(RESULT_CONTENT_VALUES_NAME, cv);
				setResult(1, intent);
				finish();
			}
			
		});
		pd = ProgressDialog.show(this, "Working..", "loading application list", true,
	                false);
		Thread thread=new Thread(this);
		thread.start();
	}/*
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

	}*/
    public class MyExpandableListAdapter extends BaseAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        List<PackageInfo> groups;
        //List<ActivityInfo[]> children;
        PackageManager pm=getPackageManager();
        public MyExpandableListAdapter(List<PackageInfo> groups){
        	this.groups=groups;
        	//this.children=children;
        }
   /*     public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition)[childPosition];
        }*/

        /*public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }*/

      /*  public int getChildrenCount(int groupPosition) {
        	try{
            return children.get(groupPosition).length;
        	}catch(NullPointerException e){
        		return 0;
        	}
        }*/

        public RelativeLayout getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);

            RelativeLayout lay = new RelativeLayout(AppPicker.this);
            lay.setLayoutParams(lp);
            // Center the text vertically
            lay.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            lay.setPadding(36, 0, 0, 0);
            return lay;
        }


      /*  public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
			View childItem = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				childItem = inflater.inflate(R.layout.picker_item,null);
				childItem.setPadding(72, 0, 0, 0);
			} else {
				childItem = convertView;
			}
            TextView tv=(TextView) childItem.findViewById(R.id.app_name);
            tv.setText(children.get(groupPosition)[childPosition].loadLabel(pm));
            ImageView iv=(ImageView)childItem.findViewById(R.id.app_icon);
            iv.setImageDrawable(children.get(groupPosition)[childPosition].loadIcon(pm));
			return childItem;
        }*/

        public Object getItem(int groupPosition) {
            return groups.get(groupPosition);
        }

        public int getCount() {
            return groups.size();
        }

        /*public long getItemId(int groupPosition) {
            return groupPosition;
        }*/

        public View getView(int groupPosition, View convertView,
                ViewGroup parent) {
			View groupItem = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				groupItem = inflater.inflate(R.layout.picker_item,null);
				//groupItem.setPadding(36, 0, 0, 0);
			} else {
				groupItem = convertView;
			}

			//String destinationType = (String) getItem(position);
			TextView destinationTypeText = (TextView) groupItem
					.findViewById(R.id.app_name);
			CharSequence label=groups.get(groupPosition).applicationInfo.loadLabel(pm);
			if(label==null || label.toString().equals(""))
				destinationTypeText.setText(groups.get(groupPosition).packageName);
			else 
				destinationTypeText.setText(label);
			destinationTypeText.setTextSize(20);
			ImageView typeSelectedImage = (ImageView) groupItem
					.findViewById(R.id.app_icon);
			
			typeSelectedImage.setImageDrawable(pm.getApplicationIcon(groups.get(groupPosition).applicationInfo));
			return groupItem;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
	/*	public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
	/*	public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}*/
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
	/*	public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}*/

    }
	public void run() {
		adapter=buildAdapter();
		handler.sendEmptyMessage(0);
		
	}
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            pd.dismiss();
            getListView().setAdapter(adapter);
 
        }
    };
}
