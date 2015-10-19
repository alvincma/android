package org.tzuchi.us.nca.mtc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.widget.TextView;

import android.util.Log;

public class MTCAddEvent extends Activity {
	
	public static final String [] EVENT_PROJECTION  = new String[] {
		Calendars._ID
	};
	String projection[] = {"_id", "calendar_displayName"};
	Uri calendars = Uri.parse("content://com.android.calendar/calendars");
	
	private static final int PROJECTION_ID_INDEX = 0;
	
	private static final String attend ="Y";
	
	private String eventId;
	private String eventName;
	private String beginTime;
	private String endTime;
	private String place;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_event);
		
		// Get intent information
		Intent intent = getIntent();
		eventId = intent.getExtras().getString("EVENT_ID");
		eventName = intent.getExtras().getString("EVENT_NAME");
		beginTime = intent.getExtras().getString("BEGIN_TIME");
		endTime = intent.getExtras().getString("END_TIME");
		place = intent.getExtras().getString("PLACE");
		
		Log.v("MTCAddEvent", eventId + " " + eventName + " " + beginTime + " " + endTime + " " + place);
		
		// Display information on view
		TextView nameTV = (TextView)findViewById(R.id.eventName);
		nameTV.setText(eventName);
		TextView beginTV = (TextView)findViewById(R.id.eventBeginTime);
		beginTV.setText(beginTime);
		TextView endTV = (TextView)findViewById(R.id.eventEndTime);
		endTV.setText(endTime);
		TextView placeTV = (TextView)findViewById(R.id.eventPlace);
		placeTV.setText(place);
	}
	
	// User wants to attend, ask user if he/she wants to add this event to his/her calendar
	public void attendEvent(View view) {
		// Modify event in events table, set ACCEPTED to "Y"
		MTCEventsDBAdapter eventsDBAdapter = new MTCEventsDBAdapter(this);
		
		eventsDBAdapter.open();
		
		eventsDBAdapter.updateEventById(eventId, attend);
		
		eventsDBAdapter.close();
		
		// Prompt user an alert dialog asking to add event to device's calendar
		raiseCalendarAlertDialog();
	}
	
	// User doesn't want to attend this event, close the activity
	public void denyEvent(View view) {
		finish();
	}
	
	// Raise Calendar Alert Dialog
	public void raiseCalendarAlertDialog() {
		new AlertDialog.Builder(this)
		.setTitle("Create Event for Calendar")
		.setMessage("Do you want to add this event to your calendar(s)?")
		.setPositiveButton("Yes", new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					addToCalendar();
					finish();
				}
		})
		.setNegativeButton("No", new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					// User clicks no, just finish()
					finish();
				}
		})
		.show();
	}
	
	// Add event to calendar 
	public void addToCalendar() {
		// Find user's google calendars
		Cursor cursor = null;
		ContentResolver resolver = getContentResolver();
		Uri calUri = Calendars.CONTENT_URI;
		String selection = "(" + Calendars.ACCOUNT_TYPE + " = ?)";
		String[] selectionArgs = new String [] { "com.google" };
		
		cursor = resolver.query(calUri, EVENT_PROJECTION, selection, selectionArgs, null);
		
		// Find all calendar IDs, although I just want one
		if (cursor.moveToFirst()) {
			do {
				long calId = 0;
				long startMillis = 0;
				long endMillis = 0;
				
				calId = cursor.getLong(PROJECTION_ID_INDEX);
				
				Log.v("MTCAddEvent", "Cal Id: " + calId);
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa", Locale.ENGLISH);
				try {
					Date beginDate = df.parse(beginTime);
					startMillis = beginDate.getTime();
					Date endDate = df.parse(endTime);
					endMillis = endDate.getTime();
				} catch (ParseException pe) {
					pe.printStackTrace();
				}
				
				ContentValues values = new ContentValues();
				values.put(Events.DTSTART, startMillis);
				values.put(Events.DTEND, endMillis);
				values.put(Events.TITLE, eventName);
				values.put(Events.EVENT_LOCATION, place);
				values.put(Events.CALENDAR_ID, calId);
				values.put(Events.EVENT_TIMEZONE, "America/Los_Anegles");
				
				resolver.insert(Events.CONTENT_URI, values);	
			} while (cursor.moveToNext());
		}
	}
}
