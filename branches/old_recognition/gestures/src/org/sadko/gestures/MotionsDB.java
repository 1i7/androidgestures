/*
  * Copyright (C) 2007 The Android Open Source Project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
 
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
 
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
 
  */
package org.sadko.gestures;

//import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
//import android.provider.BaseColumns;
//import android.util.Log;

public class MotionsDB extends ContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://org.sadko.gestures.content");
	public static final Uri MOTIONS_CONTENT_URI=Uri.withAppendedPath(CONTENT_URI, "motions");
	public static final Uri TASKS_CONTENT_URI=Uri.withAppendedPath(CONTENT_URI, "tasks");
	private static final UriMatcher sUriMatcher;
	private static final int MOTIONS = 1;
	private static final int MOTION_ID = 2;
	private static final int TASKS = 3;
	private static final int TASK_ID = 4;
	public static final String DATABASE_NAME = "MOTION_DB";
	public static final String MOTIONS_TABLE_NAME = "MOTIONS";
	public static final String TASKS_TABLE_NAME = "TASKS";
	private static final int DATABASE_VERSION = 2;


	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI("org.sadko.gestures.content", "motions", MOTIONS);
		sUriMatcher.addURI("org.sadko.gestures.content", "motions/#", MOTION_ID);
		sUriMatcher.addURI("org.sadko.gestures.content", "tasks", TASKS);
		sUriMatcher.addURI("org.sadko.gestures.content", "tasks/#", TASK_ID);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//Log.i("Dcreate", ":)");
			String script = "CREATE TABLE " + MOTIONS_TABLE_NAME + " ("
					+ MotionColumns._ID
					+ " integer primary key autoincrement, ";
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++) {
					script = script + MotionColumns.MATRIX[i][j] + " float,";
				}

			script = script + MotionColumns.TIME + " integer, "
					+ MotionColumns.NAME + " VARCHAR(20), "
					+ MotionColumns.MODIFIED_DATE + " INTEGER" + ");";
			db.execSQL(script);
			script = "CREATE TABLE " + TASKS_TABLE_NAME + " ("
					+ ActivityColumns._ID
					+ " integer primary key autoincrement, ";
			script = script + ActivityColumns.PACK + " VARCHAR(256), ";
			script = script + ActivityColumns.ACTIVITY + " VARCHAR(256), ";
			script = script + ActivityColumns.MOTION_ID + " integer, ";
			script = script + " FOREIGN KEY(" + ActivityColumns.MOTION_ID
					+ ")  REFERENCES " + MOTIONS_TABLE_NAME + "("
					+ MotionColumns._ID + ") ON DELETE CASCADE);";
			db.execSQL(script);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//Log
				//	.w("db", "Upgrading database from version " + oldVersion
					//		+ " to " + newVersion
						//	+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + MOTIONS_TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		long id = Long.parseLong(arg0.getLastPathSegment());
		switch (sUriMatcher.match(arg0)) {
		case TASK_ID: {
			mOpenHelper.getWritableDatabase().delete(TASKS_TABLE_NAME,
					"_id=" + id, null);
			break;
		}
		case MOTION_ID: {
			mOpenHelper.getWritableDatabase().delete(MOTIONS_TABLE_NAME,
					"_id=" + id, null);
			break;
		}

		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case TASKS: {
			long rowId = db.insert(
					TASKS_TABLE_NAME, null, initialValues);
			
			Uri taskUri = ContentUris.withAppendedId(TASKS_CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(taskUri, null);
			//Log.i("DB","TASK inserted "+rowId);
			if(rowId<0) 
				throw new SQLException("Failed to insert row into " + uri);
			getContext().getContentResolver().notifyChange(uri, null);
			return taskUri;
		}
		case MOTIONS: {
			initialValues.put(MotionColumns.MODIFIED_DATE, System
					.currentTimeMillis());
			long rowId = db.insert(MOTIONS_TABLE_NAME, null, initialValues);
			Uri motionUri = ContentUris.withAppendedId(MOTIONS_CONTENT_URI, rowId);
			//getContext().getContentResolver().notifyChange(motionUri, null);
			//Log.i("DB","MOTION inserted "+rowId);
			if(rowId<0) 
				throw new SQLException("Failed to insert row into " + uri);
			getContext().getContentResolver().notifyChange(MOTIONS_CONTENT_URI,null);
			
			return motionUri;
		}
		default:{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		}
		
		
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		//Log.i("query", ":)");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case MOTIONS:
			qb.setTables(MOTIONS_TABLE_NAME);
			break;

		case MOTION_ID:
			qb.setTables(MOTIONS_TABLE_NAME);
			qb.appendWhere("_id" + "=" + ContentUris.parseId(uri));
			break;
		case TASKS:
			qb.setTables(TASKS_TABLE_NAME);
			break;
		case TASK_ID:
			qb.setTables(TASKS_TABLE_NAME);
			qb.appendWhere("_id" + "=" + ContentUris.parseId(uri));

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		//Log.i("query",qb.buildQuery(projection, selection, selectionArgs, null, null, null, null));
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, null, null);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int rez=0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case TASKS: {
			rez=db.update(TASKS_TABLE_NAME, values, selection, selectionArgs);
			
			
			getContext().getContentResolver().notifyChange(TASKS_CONTENT_URI, null);
			//Log.i("DB","TASK updated "+rez);
			return rez;
		}
		case MOTIONS: {
			values.put(MotionColumns.MODIFIED_DATE, System
					.currentTimeMillis());
			rez = db.update(MOTIONS_TABLE_NAME,values,selection,selectionArgs);
			//getContext().getContentResolver().notifyChange(motionUri, null);
			//Log.i("DB","MOTION updated "+rez);
			getContext().getContentResolver().notifyChange(MOTIONS_CONTENT_URI,null);
			
			return rez;
		}
		case MOTION_ID:{
			values.put(MotionColumns.MODIFIED_DATE, System.currentTimeMillis());
			rez=db.update(MOTIONS_TABLE_NAME, values, "_ID="+ContentUris.parseId(uri),null);
			getContext().getContentResolver().notifyChange(MOTIONS_CONTENT_URI,null);
			
			return rez;
			}
		
		case TASK_ID:
			rez=db.update(TASKS_TABLE_NAME, values, "_ID="+ContentUris.parseId(uri),null);
			getContext().getContentResolver().notifyChange(TASKS_CONTENT_URI,null);
			
			return rez;
		default:{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		}
		
		
	}

}
