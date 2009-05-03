package org.sadko.gestures;

import java.util.Iterator;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MotionHandler1 extends MotionHandler {
	long needTime = 0;// максимальное время движения(из записанных)
	public static double MOTION_SENSITIVITY = 0.1;
	long oldestTime = 0;
	public static long timeBetweenRegistering = 1400;
	int ARRAY_SIZE = 10;
	long lastRegisterTime = 0;
	long[] times = new long[ARRAY_SIZE];
	protected double yaws[] = new double[ARRAY_SIZE];
	protected double rolls[] = new double[ARRAY_SIZE];
	protected double pitchs[] = new double[ARRAY_SIZE];
	int position = 0;

	@Override
	public void addMotion(Motion motion) {
		super.addMotion(motion);
		needTime = (motion.time > needTime ? motion.time : needTime);
		// это чтобы в массиве хватало полей для улавливания движения
		needTime += 1;
		ARRAY_SIZE = (int) (needTime / 20) + 1;
		yaws = new double[ARRAY_SIZE];
		rolls = new double[ARRAY_SIZE];
		pitchs = new double[ARRAY_SIZE];
		times = new long[ARRAY_SIZE];
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
	}



	public void onSensorChanged(int sensor, float[] values) {

		boolean checkMotion[] = new boolean[motions.size()];
		times[position] = System.currentTimeMillis();
		yaws[position] = values[0] * Math.PI / 180;
		pitchs[position] = values[1] * Math.PI / 180;
		rolls[position] = values[2] * Math.PI / 180;
		int i = position;
		boolean detected = false;
		while (!detected && i != (position + 1) % ARRAY_SIZE) {
			Iterator<Motion> j = motions.iterator();
			int s = 0;
			while (j.hasNext()) {
				Motion m = j.next();
				if (times[position] - times[i] - m.time > 0 && !checkMotion[s]) {
					checkMotion[s] = true;
					double[][] matrix = math(yaws[position], pitchs[position],
							rolls[position], yaws[i], pitchs[i], rolls[i]);
					double ss = 0;
					for (int k = 0; k < 3; k++)
						for (int l = 0; l < 3; l++)
							ss += (matrix[k][l] - m.matrix[k][l])
									* (matrix[k][l] - m.matrix[k][l]);
					if (ss < MOTION_SENSITIVITY
							&& System.currentTimeMillis() - lastRegisterTime > timeBetweenRegistering
							) {
						notifyListeners((int) m.id);
						lastRegisterTime = System.currentTimeMillis();
						detected = true;
					}
				}
				s++;
			}
			i = (i - 1 + ARRAY_SIZE) % ARRAY_SIZE;
		}
		position = (position + 1) % ARRAY_SIZE;
	}

	@Override
	public void onCreate() {
		// File f=new File("/sdcard/motions.txt");
		Cursor c = getContentResolver().query(
				MotionsDB.MOTIONS_CONTENT_URI,
				new String[] { "A00", "A01", "A02", "A10", "A11", "A12", "A20",
						"A21", "A22", "time", "_id" },
				null, null, null);

		while (!c.isLast()) {
			
			c.moveToNext();
			Motion motion = new Motion();
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					motion.matrix[i][j] = c.getFloat(c.getColumnIndex("A"+i+""+j));
			motion.time = c.getLong(c.getColumnIndex(MotionColumns.TIME));
			motion.id = c.getLong(c.getColumnIndex(MotionColumns._ID));
			addMotion(motion);
		}
		showNotification();

		super.onCreate();
	}

	@Override
	public void switchMe() {
		// TODO Auto-generated method stub
		super.switchMe();
	}
	

}
