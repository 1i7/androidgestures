package org.sadko.gestures;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MyTabActivity extends TabActivity {
	TabHost tabhost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.menu);

		final TabHost tabHost = getTabHost();
		//LayoutInflater.from(this).inflate(R.layout.arg, tabHost.getTabContentView(), true);

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(getString(R.string.main_screen_label))
                .setContent(new Intent(this, Manager.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(getString(R.string.lib_label))
                .setContent(new Intent (this, GesturesListActivity.class)));
         /*tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("Other")
                .setContent(R.id.view3));*/
		/*gotoDevices = (Button) findViewById(R.id.devices_in_myRange);
		gotoDevices.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, NearbyDevicesActivity.class));
				
			}
			
		});*/
	}
	

}


