package org.tzuchi.us.nca.mtc;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MTCEventsProvider extends ContentProvider {
	
	public static final Uri CONTENT_URL = Uri.parse("contents://org.tzuchi.us.nca.provider.mtc/events");
	
	// Constants to differentiate between the different URI requests
	private static final int EVENTS = 1;
	private static final int EVENT_ID = 2;
	
	private static final UriMatcher uriMatcher;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("org.tzuchi.us.nca.provider.mtc", "events", EVENTS);
		uriMatcher.addURI("org.tzuchi.us.nca.provider.mtc", "events/#", EVENT_ID);
	}

	// Events database definition
	private SQLiteDatabase eventsDatabase;
	
	private static final String TAG = "MTCEventsProvider";
	private static final String DATABASE_NAME = "mtc.db";
	private static final int DATABASE_VERSION = 1;
	private static final String EVENTS_TABLE = "events";
	
	// Register Events Table Column Name
	public static final String KEY_ID = "_id";
	public static final String KEY_EVENT_ID = "event_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_BEGIN_TIME = "begin_time";
	public static final String KEY_END_TIME = "end_time";
	public static final String KEY_PLACE = "place";
	public static final String KEY_ACCEPTED = "accepted";
	
	// Register Events Table Column Indexes
	public static final int EVENT_ID_COLUMN = 1;
	public static final int NAME_COLUMN = 2;
	public static final int BEGIN_TIME_COLUMN = 3;
	public static final int END_TIME_COLUMN = 4;
	public static final int PLACE_COLUMN = 5;
	public static final int ACCEPTED_COLUMN = 6;
	
	// Helper class for opening creating and managing database version control
	private static class eventsDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE =
				"create table " + EVENTS_TABLE + " (" +
				KEY_ID + " integer primary key autoincrement, " +
				KEY_EVENT_ID + " text not null unique, " + 
				KEY_NAME + " text not null, " +
				KEY_BEGIN_TIME + " long, " +
				KEY_END_TIME + " long, " +
				KEY_PLACE + " text not null," + 
				KEY_ACCEPTED + " text not null);";
		
		public eventsDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
					    ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
			onCreate(db);
		}
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		
		switch (uriMatcher.match(uri)) {
			case EVENTS:
				count = eventsDatabase.delete(EVENTS_TABLE, where, whereArgs);
				break;
			case EVENT_ID:
				String segment = uri.getPathSegments().get(1);
				count = eventsDatabase.delete(EVENTS_TABLE, KEY_ID + "=" + segment +
											  (!TextUtils.isEmpty(where) ? " AND (" 
											  + where + ')' : ""),
						                      whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case EVENTS: 
				return "vnd.android.cursor.dir/vnd.tzuchi.events";
			case EVENT_ID:
				return "vnd.android.cursor.item/vnd.tzuchi.events";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri _uri, ContentValues initialContentValues) {
		// Insert the new event, returns the row number if successful
		long rowID = eventsDatabase.insert(EVENTS_TABLE, "events", initialContentValues);
		
		// Return a URI to the newly inserted row on success
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URL, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		
		eventsDatabaseHelper dbHelper = new eventsDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		eventsDatabase = dbHelper.getWritableDatabase();
		
		return (eventsDatabase == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(EVENTS_TABLE);
		
		// If this is a row query, limit the result set to the passed in row
		switch (uriMatcher.match(uri)) {
			case EVENT_ID: 
				qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		
		
		// If no sort order is specified, sort by event begin time
		String orderBy;
		
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_BEGIN_TIME;
		} else {
			orderBy = sort;
		}
		
		// Execute the query to the underlying database
		Cursor cursor = qb.query(eventsDatabase, projection, selection, selectionArgs, null, null, orderBy);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		// Return a cursor to query result
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
		int count;
		
		switch (uriMatcher.match(uri)) {
			case EVENTS:
				count = eventsDatabase.update(EVENTS_TABLE, contentValues, where, whereArgs);
				break;
			case EVENT_ID:
				String segment = uri.getPathSegments().get(1);
				count = eventsDatabase.update(EVENTS_TABLE, contentValues, 
											  KEY_ID + "=" + segment
											  + (!TextUtils.isEmpty(where) ? " AND ("
											  + where + ')' : ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
