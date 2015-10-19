package org.tzuchi.us.nca.mtc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MTCEventsDBAdapter {
	private static final String TAG = "MTCEventsDBAdapter";
	
	private static final String DATABASE_NAME = "mtc.db";
	private static final String EVENTS_TABLE = "events";
	private static final int DATABASE_VERSION = 1;
	
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
	
	public static final String YES = "Y";
	
	private SQLiteDatabase eventsDatabase;
	private final Context context;
	private MTCEventsDBOpenHelper dbHelper;
	
	private static class MTCEventsDBOpenHelper extends SQLiteOpenHelper {
		
		public MTCEventsDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		// SQL Statement to create a new database
		private static final String DATABASE_CREATE = 
				"create table " + EVENTS_TABLE + " (" +
						KEY_ID + " integer primary key autoincrement, " + 
						KEY_EVENT_ID + " text not null unique, " + 
						KEY_NAME + " text not null, " +
						KEY_BEGIN_TIME + " long, " +
						KEY_END_TIME + " long, " +
						KEY_PLACE + " text not null," + 
						KEY_ACCEPTED + " text not null);";
		
		private static final String DATABASE_CREATE1 = "create table events (_id integer primary key autoincrement, event_id text unique, name text not null, begin_time integer, end_time integer, place text not null, accepted text not null);";
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE1);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion + 
					   ", which will destroy all old data.");
			
			// Drop the old table
			db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
			
			// Create a new one
			onCreate(db);
		}
	}
	
	public MTCEventsDBAdapter(Context _context) {
		this.context = _context;
		dbHelper = new MTCEventsDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void open() throws SQLiteException {
		try {
			eventsDatabase = dbHelper.getWritableDatabase();
		} catch (SQLiteException se) {
			eventsDatabase = dbHelper.getReadableDatabase();
		}
	}
	
	public void close() {
		eventsDatabase.close();
	}
	
	// Insert a new event
	public long insertEvent(MTCEvent event, String accepted) {
		// Create a new row of values to insert.
		ContentValues newEventValues = new ContentValues();
		
		// Assign values for each column
		newEventValues.put(KEY_EVENT_ID, event.getEventId());
		newEventValues.put(KEY_NAME, event.getEventName());
		newEventValues.put(KEY_BEGIN_TIME, event.getBeginTime().getTime());
		newEventValues.put(KEY_END_TIME, event.getEndTime().getTime());
		newEventValues.put(KEY_PLACE, event.getPlace());
		newEventValues.put(KEY_ACCEPTED, accepted);
		
		// Insert the new row
		return eventsDatabase.insert(EVENTS_TABLE, null, newEventValues);
	}
	
	// Remove an event based on its index
	public boolean removeEvent(long rowIndex) {
		return eventsDatabase.delete(EVENTS_TABLE, KEY_ID + "=" + rowIndex, null) > 0;
	}
	
	// Remove an event based on its event ID
	public boolean removeEventByID(String eventId) {
		return eventsDatabase.delete(EVENTS_TABLE, KEY_EVENT_ID + "='" + eventId + "'", null) > 0;
	}
	
	// Update an event by Row Index
	public boolean updateEvent(long rowIndex, String accepted) {
		ContentValues newValue = new ContentValues();
		
		newValue.put(KEY_ACCEPTED, accepted);
		
		return eventsDatabase.update(EVENTS_TABLE, newValue, KEY_ID + "=" + rowIndex, null) > 0;
	}
	
	// Update an event by Event Id
	public boolean updateEventById(String eventId, String accepted) {
		ContentValues newValue = new ContentValues();
		
		newValue.put(KEY_ACCEPTED, accepted);
		
		return eventsDatabase.update(EVENTS_TABLE, newValue, KEY_EVENT_ID + "='" + eventId + "'", null) > 0;
	}
	
	// Get all events, accepted or not
	public Cursor getAllEventsCursor() {
		return eventsDatabase.query(EVENTS_TABLE, 
				                    new String[] { KEY_ID, KEY_EVENT_ID, KEY_NAME, KEY_BEGIN_TIME, KEY_END_TIME, KEY_PLACE, KEY_ACCEPTED },
				                    null, null, null, null, null);
	}
	
	// Get all accepted events 
	public Cursor getAllRegisteredEvents() {
		return eventsDatabase.query(EVENTS_TABLE, 
                                    new String[] { KEY_ID, KEY_EVENT_ID, KEY_NAME, KEY_BEGIN_TIME, KEY_END_TIME, KEY_PLACE, KEY_ACCEPTED },
                                    KEY_ACCEPTED + "= '" + YES + "'", null, null, null, null);
	}
	
	// Set cursor to specific event
	public Cursor setCursorToEvent(long rowIndex) {
		Cursor result = eventsDatabase.query(true, EVENTS_TABLE, 
				                             new String[] { KEY_ID, KEY_EVENT_ID, KEY_NAME, KEY_BEGIN_TIME, KEY_END_TIME, KEY_PLACE, KEY_ACCEPTED }, 
				                             KEY_ID + "=" + rowIndex, null, null, null, null, null);
		
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLiteException("No event found for row: " + rowIndex);
		}
		
		return result;
	}
	
	// Construct Event objects from database
	public MTCEvent getEvent(long rowIndex) 
		throws SQLiteException {
		
		Cursor cursor = eventsDatabase.query(true, EVENTS_TABLE, 
				                             new String[] { KEY_ID, KEY_EVENT_ID, KEY_NAME, KEY_BEGIN_TIME, KEY_END_TIME, KEY_PLACE, KEY_ACCEPTED }, 
				                             KEY_ID + "=" + rowIndex, null, null, null, null, null);
		
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLiteException("No event found for row: " + rowIndex);
		}
		
		String eventId = cursor.getString(cursor.getColumnIndex(KEY_EVENT_ID));
		String eventName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
		long beginTime = cursor.getLong(cursor.getColumnIndex(KEY_BEGIN_TIME));
		long endTime = cursor.getLong(cursor.getColumnIndex(KEY_END_TIME));
		String place = cursor.getString(cursor.getColumnIndex(KEY_PLACE));
		
		MTCEvent result = new MTCEvent(eventId, eventName, new Date(beginTime), new Date(endTime), place);
		
		return result;
	}
}
