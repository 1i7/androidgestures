package net.panda.motionRegister;

//import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.Random;

//import org.sadko.gestures.Gesture;



import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

public class GestureCatcher implements SensorEventListener, SensorListener {
	Gesture gest;
	double[] readings = new double[3];
	double[][] scoreMatrix;
	double[] newScores;
	final static double TIME_EXTENTION = 2;
	final static int DISCRETIZATION_NUMBER = 10;
	final static int ARRAY_LENGTH = DISCRETIZATION_NUMBER * 2 + 1;
	static double yaw;
	static double pitch;
	static double roll;
	public double SENSITIVITY = 0.7;
	static double GRAVITY = SensorManager.STANDARD_GRAVITY;
	GestureCatcherActivity container;
	GestureCatcher(Gesture g) {
		gest = g;
		g.trim();
		scoreMatrix = new double[DISCRETIZATION_NUMBER * 2 + 1][gest.readingsSequence.length];
		newScores = new double[gest.readingsSequence.length];
		for (int index = 0; index < gest.readingsSequence.length; ++index) {
			Log.i("catcher constr", "! " + gest.readingsSequence[index][0]
					+ " " + gest.readingsSequence[index][1] + " "
					+ gest.readingsSequence[index][2]);
		}
		
		
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	long lastAcceptedTime;
	long nowTime;

	public void onSensorChanged(int sensor, float[] values) {
		// TODO делать это все когда надо

		if (sensor == SensorManager.SENSOR_ORIENTATION) {
			setOrientation(values);
			//Log.i("ori","tuta");
			return;
		}
		nowTime = System.currentTimeMillis();
		if (nowTime - lastAcceptedTime >= gest.timeIntervalMillis) {
			if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
				substractG(values, readings, yaw, pitch, roll);
				//Log.i("vals",""+readings[0]+" "+readings[1]+" "+readings[2]);
				computeScores();
			}
			lastAcceptedTime = nowTime;
		}

	}

	private void setOrientation(float[] values) {
		yaw = values[0]*Math.PI/180;
		pitch = values[1]*Math.PI/180;
		roll = values[2]*Math.PI/180;

	}

	double[] tmpReadings = new double[3];
	int received=0;
	private void computeScores() {
		for (int i = 0; i < ARRAY_LENGTH; i++) {
			double extensionMultiplyer = Math.pow(TIME_EXTENTION,
					((double) i - DISCRETIZATION_NUMBER)
							/ DISCRETIZATION_NUMBER);
			// if (i == DISCRETIZATION_NUMBER)
			for (int k = 0; k < 3; k++)
				tmpReadings[k] = readings[k]
						/ (extensionMultiplyer * extensionMultiplyer);
			// if(i> DISCRETIZATION_NUMBER)
			int intPart = (int) Math.floor(extensionMultiplyer);
			int cyclesNumber = (Math.random() > extensionMultiplyer - intPart ? 1
					: 0) + intPart;
			for (int index = 0; index < cyclesNumber; ++index) {
				newScores[0] = score(tmpReadings, gest.readingsSequence[0]);
				for (int j = 1; j < gest.readingsSequence.length; j++)
					newScores[j] = scoreMatrix[i][j - 1]
							+ score(tmpReadings, gest.readingsSequence[j]);
				System.arraycopy(newScores, 0, scoreMatrix[i], 0,
						newScores.length);
			}
		}
		double max = Double.NEGATIVE_INFINITY;
		// scoreMatrix[scoreMatrix.length][0];
		for (int i = 0; i < ARRAY_LENGTH; i++)
			max = (max > scoreMatrix[i][gest.readingsSequence.length - 1] ? max
					: scoreMatrix[i][gest.readingsSequence.length - 1]);
		if(scoreMatrix[DISCRETIZATION_NUMBER][gest.readingsSequence.length - 1]/gest.readingsSequence.length >= SENSITIVITY){ 
			//Log.i("yo!",scoreMatrix[DISCRETIZATION_NUMBER][gest.readingsSequence.length - 1]/gest.readingsSequence.length+" "+ ++received);
			container.increase();
		}
	}
	
	
	//final double expMultiplyer = 10000;
	private double score(double[] reads, double[] gestReadings) {
		return (Math.atan(Math.abs(reads[0]/gestReadings[0]))*Math.atan(Math.abs(reads[1]/gestReadings[1]))*Math.atan(Math.abs(reads[2]/gestReadings[2]))) *
		( Math.exp( -((reads[0] - gestReadings[0]) * (reads[0] - gestReadings[0])) / ((gestReadings[0]*gestReadings[0]))) +
		Math.exp( -((reads[1] - gestReadings[1]) * (reads[1] - gestReadings[1])) / (gestReadings[1]*gestReadings[1])) +
		Math.exp( -((reads[2] - gestReadings[2]) * (reads[2] - gestReadings[2]))/ (gestReadings[2]*gestReadings[2])));
		// return 0;
	}

	public static void substractG(float[] sourceValues, double[] destValues,
			double yaw, double pitch, double roll) {
		double[] afterRotation = new double[3];
		// yaw = sourceValues[0];
		// pitch = sourceValues[1];
		// roll = sourceValues[2];
		afterRotation[0] = (-Math.sin(roll) * Math.sin(yaw) * Math.sin(pitch) + Math
				.cos(yaw)
				* Math.cos(roll))
				* sourceValues[0]
				+ (Math.cos(pitch) * Math.sin(yaw))
				* sourceValues[1]
				+ (Math.sin(pitch) * Math.cos(roll) * Math.sin(yaw) + Math
						.cos(yaw)
						* Math.sin(roll)) * sourceValues[2];

		afterRotation[1] = (-Math.sin(roll) * Math.cos(yaw) * Math.sin(pitch) - Math
				.sin(yaw)
				* Math.cos(roll))
				* sourceValues[0]
				+ (Math.cos(pitch) * Math.cos(yaw))
				* sourceValues[1]
				+ (Math.sin(pitch) * Math.cos(roll) * Math.cos(yaw) - Math
						.sin(roll)
						* Math.sin(yaw)) * sourceValues[2];

		afterRotation[2] = (-Math.sin(roll) * Math.cos(pitch))
				* sourceValues[0] + (-Math.sin(pitch)) * sourceValues[1]
				+ (Math.cos(pitch) * Math.cos(roll)) * sourceValues[2];
		/* Log.i("substracting",
		 ""+afterRotation[0]+" "+afterRotation[1]+" "+afterRotation[2]);*/
		afterRotation[2] += GRAVITY;

		destValues[0] = (-Math.sin(roll) * Math.sin(yaw) * Math.sin(pitch) + Math
				.cos(yaw)
				* Math.cos(roll))
				* afterRotation[0]
				+ (-Math.sin(roll) * Math.cos(yaw) * Math.sin(pitch) - Math
						.sin(yaw)
						* Math.cos(roll))
				* afterRotation[1]
				+ (-Math.sin(roll) * Math.cos(pitch)) * afterRotation[2];

		destValues[1] = (Math.cos(pitch) * Math.sin(yaw)) * afterRotation[0]
				+ (Math.cos(pitch) * Math.cos(yaw)) * afterRotation[1]
				+ (-Math.sin(pitch)) * afterRotation[2];

		destValues[2] = (Math.sin(pitch) * Math.cos(roll) * Math.sin(yaw) + Math
				.cos(yaw)
				* Math.sin(roll))
				* afterRotation[0]
				+ (Math.sin(pitch) * Math.cos(roll) * Math.cos(yaw) - Math
						.sin(roll)
						* Math.sin(yaw))
				* afterRotation[1]
				+ (Math.cos(pitch) * Math.cos(roll)) * afterRotation[2];

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}
}
