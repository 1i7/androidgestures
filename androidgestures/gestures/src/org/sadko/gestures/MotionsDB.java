package org.sadko.gestures;

import java.util.HashMap;

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
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class MotionsDB extends ContentProvider {
	public static final Uri CONTENT_URI =Uri.parse("content://org.sadko.gestures.content"); 
        //Uri.parse("content://net.panda.motionrecorder.MotionsDB");
	//public static final String A[][]=new String[3][3];
    private static final UriMatcher sUriMatcher;
    private static final int MOTIONS = 1;
    private static final int MOTION_ID = 2;
	//public static final String DURATION="time";
	public static final String DATABASE_NAME="MOTION_DB6";
	public static final String TABLE_NAME="MOTIONS_last";
	private static final int DATABASE_VERSION = 2;
	private static class MOTION_COL implements BaseColumns{
		
	}
    private static HashMap<String, String> sNotesProjectionMap;
    
	static{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI("org.sadko.gestures.content", "motions", MOTIONS);
        sUriMatcher.addURI("org.sadko.gestures.content", "motions/#", MOTION_ID);
        
        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put("_id","_id");
        for(int i=0;i<3;i++)
        	for(int j=0;j<3;j++)
        		sNotesProjectionMap.put(MotionColumns.MATRIX[i][j], MotionColumns.MATRIX[i][j]);
        sNotesProjectionMap.put(MotionColumns.TIME, MotionColumns.TIME);
        sNotesProjectionMap.put(MotionColumns.NAME, MotionColumns.NAME);
        //sNotesProjectionMap.put(MotionColumns.PATH, MotionColumns.PATH);
        sNotesProjectionMap.put(MotionColumns.PACK, MotionColumns.PACK);
        sNotesProjectionMap.put(MotionColumns.ACTIVITY, MotionColumns.ACTIVITY);
        
	}

	    private static class DatabaseHelper extends SQLiteOpenHelper {

	        DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	            
	            Log.i("database created","yes");
	        }
	        @Override
	        public void onCreate(SQLiteDatabase db) {
	        	Log.i("Dcreate",":)");
	        	String script="CREATE TABLE " + TABLE_NAME + " (" +BaseColumns._ID+" integer primary key autoincrement, ";
	        	for(int i=0;i<3;i++)
	        		for(int j=0;j<3;j++){
	        			script=script+ MotionColumns.MATRIX[i][j]+ " float,";
	        		}
	        	
	        	script=script+MotionColumns.TIME+" integer, "+
	        	MotionColumns.NAME+" VARCHAR(20), "+
	        	MotionColumns.MODIFIED_DATE+ " INTEGER, "+
	        	MotionColumns.PACK+ " VARCHAR(256), "+
	        	MotionColumns.ACTIVITY+ " VARCHAR(256) "+
	        	");";
	        	Log.i("script",script+":)");
	            db.execSQL(script);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            Log.w("db", "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
	            onCreate(db);
	        }
	    }
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		long id=Long.parseLong(arg0.getLastPathSegment());
		
		Log.i("delete",""+mOpenHelper.getWritableDatabase().delete(TABLE_NAME,"_id="+id,null));
		
		// TODO Auto-generated method stub
		Log.i("delete", ":)");
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		//Log.i("contentprov","method invoked");
	    /*if (sUriMatcher.match(uri) != MOTIONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());*/
        // Make sure that the fields are all set
		initialValues.put(MotionColumns.MODIFIED_DATE, System.currentTimeMillis());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, "motion", initialValues);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
    	Log.i("create", ":)");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.i("query", ":)");
	      SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

	        switch (sUriMatcher.match(uri)) {
	        case MOTIONS:
	            qb.setTables(TABLE_NAME);
	            qb.setProjectionMap(sNotesProjectionMap);
	            break;

	        case MOTION_ID:
	            qb.setTables(TABLE_NAME);
	            qb.setProjectionMap(sNotesProjectionMap);
	            qb.appendWhere("_id" + "=" + uri.getPathSegments().get(1));
	            Log.i("sdf","here i am "+ uri.getPathSegments());
	            break;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        // If no sort order is specified use the default


	        // Get the database and run the query
	        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
	        //Log.i("database",db.getPath());
	        
	        //String s=qb.buildQuery(projection, selection, selectionArgs, null, null, null, null);
	        //Log.i("query",s);
	        
	        Cursor c=qb.query(db,projection, selection, selectionArgs, null, null, null, null);
	        //Log.i("query",s);
	        // Tell the cursor what uri to watch, so it knows when its source data changes
	       //c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.i("update", ":)");
		// TODO Auto-generated method stub
		return 0;
	}

}
