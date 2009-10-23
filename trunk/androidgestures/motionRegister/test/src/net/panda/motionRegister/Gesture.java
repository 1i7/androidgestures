package net.panda.motionRegister;

//import org.sadko.gestures.MotionsDB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import android.content.ContentValues;

public class Gesture implements Serializable{
/*	public static final Uri contentUri = Uri.withAppendedPath(
			MotionsDB.CONTENT_URI, "GESTURES");*/
	private long id;

	double[/* number of reading */][/* ax ay az */] readingsSequence;
	long timeIntervalMillis;
	final static double TREMBLING_CONSTANT = 0.02;
	// TODO сделать конструкторы
	Gesture castToTimeInterval(long newtimeInterval) {
		Gesture rezult = new Gesture();
		rezult.id = id;
		rezult.timeIntervalMillis = newtimeInterval;
		double fraction = (double) newtimeInterval / timeIntervalMillis;
		int newArrayLength = (int) Math.floor(readingsSequence.length
				/ fraction);
		rezult.readingsSequence = new double[newArrayLength][3];
		for (int index = 0; index < newArrayLength; ++index) {
			// TODO линеаризацию сделать
			int indexInOld = (int) Math.floor(index * fraction);
			// может быть отстой из-за одного куска памяти
			rezult.readingsSequence[index] = readingsSequence[indexInOld];
		}

		return rezult;
	}

	ContentValues gestureToDB() {
		return null;
	}
	void trim() {
		int beginIndex;
		for (beginIndex = 0; Math.abs(readingsSequence[beginIndex][0])<TREMBLING_CONSTANT &&
			Math.abs(readingsSequence[beginIndex][1])<TREMBLING_CONSTANT &&
			Math.abs(readingsSequence[beginIndex][2])<TREMBLING_CONSTANT ;++beginIndex);
		int endIndex;
		for (endIndex = readingsSequence.length - 1; Math.abs(readingsSequence[endIndex][0])<TREMBLING_CONSTANT &&
			Math.abs(readingsSequence[endIndex][1])<TREMBLING_CONSTANT &&
			Math.abs(readingsSequence[endIndex][2])<TREMBLING_CONSTANT ;--endIndex);
		double [][] newSeq = new double[endIndex - beginIndex + 1][3];
		System.arraycopy(readingsSequence, beginIndex, newSeq, 0, newSeq.length);
		readingsSequence = newSeq;
	}
	/*void save() {
		File f= new File("sdcard/gesture.gst");
		try {
			FileWriter fw = new FileWriter(f);
			for(int i = 0)
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		// TODO реализовать в датабазе это все
		/*Uri persistUri = ContentUris.withAppendedId(contentUri, id);
		ContentValues values = gestureToDB();
		if (database.query(persistUri, null, null, null, null).getCount() != 0)
			database.update(persistUri, values, null, null);
		else {
			id = ContentUris.parseId(database.insert(contentUri, values));
		}*/
	//}
}
