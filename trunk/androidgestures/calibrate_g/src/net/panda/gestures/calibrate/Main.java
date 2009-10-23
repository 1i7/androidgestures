package net.panda.gestures.calibrate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements SensorEventListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final TextView gPlace = (TextView) findViewById(R.id.Put_g_here);
        final Button b = (Button) findViewById(R.id.start_calibration);
        final SensorManager m =(SensorManager) getSystemService(SENSOR_SERVICE);
        b.setText("begin");
        b.setOnClickListener(new OnClickListener(){
        	boolean beginned = false;
			public void onClick(View arg0) {
				if(!beginned){
					b.setText("end");
					beginned = true;
					m.registerListener(Main.this, m.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_FASTEST);
				}
				else{
					
					m.unregisterListener(Main.this);
					gPlace.setText(Math.sqrt(sum/steps)+"");
					SharedPreferences p = Main.this.getSharedPreferences("gConstant", MODE_WORLD_READABLE);
					p.edit().putFloat("g", (float) Math.sqrt(sum/steps)).commit();
				}
			}
        	
        });

       
        
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	double sum = 0;
	int steps = 0;
	public void onSensorChanged(SensorEvent event) {
		sum+=(event.values[0])*(event.values[0])+
		(event.values[1])*(event.values[1])+
		(event.values[2])*(event.values[2]);
		steps++;
	}
}