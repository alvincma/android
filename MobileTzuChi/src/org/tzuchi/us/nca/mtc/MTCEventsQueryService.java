package org.tzuchi.us.nca.mtc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;

public class MTCEventsQueryService extends Service {
	
	private final IBinder mBinder = new LocalBinder();
	
	private MTCEventsDBAdapter eventsDBAdapter;
	private Notification newEventNotification;
	private EventsInquiryTask inquiryTask = null;
	private AlarmManager alarms;
	private PendingIntent alarmIntent;
	private int notifyId = 0;
	
	public class LocalBinder extends Binder {
		
		MTCEventsQueryService getEventsQueryService() {
			return MTCEventsQueryService.this;
		}
	}
	
	private class EventsInquiryTask extends AsyncTask<Void, MTCEvent, Void> {
		@Override 
		protected Void doInBackground(Void... params) {
			// Get the events in JSON
			URL url;
			ArrayList<MTCEvent> newEvents = new ArrayList<MTCEvent>();
			
			try {
				String eventsFeed = getString(R.string.events_feed);
				url = new URL(eventsFeed);
				
				URLConnection connection;
				connection = url.openConnection();
				
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				int responseCode = httpConnection.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK){
					// Parse the JSON events feed
					InputStream is = httpConnection.getInputStream();
					
					ObjectMapper mapper = new ObjectMapper();
					try {
						ArrayList<MTCEventJson> eventsList = mapper.readValue(is, new TypeReference<ArrayList<MTCEventJson>>() {});
						
						if (!eventsList.isEmpty()) {
							newEvents = processNewEvents(eventsList);
						}
					} catch (JsonGenerationException jge) {
						jge.printStackTrace();
					} catch (JsonMappingException jme) {
						jme.printStackTrace();
					}
				}
				
				for (MTCEvent event : newEvents) {
					// Check if new event is already past, if not, add to events database
					Date now = new Date();
					Date beginTime = event.getBeginTime();
					
					if (!beginTime.before(now)) {
						// Add new events to events database, assuming not attend					
						eventsDBAdapter.insertEvent(event, "N");
						
						publishProgress(event);
					}
				}
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(MTCEvent...events) {
			String serviceName = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager;
		
			notificationManager = (NotificationManager)getSystemService(serviceName);
			
			// Collect event information
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa", Locale.ENGLISH);
			String eventId = events[0].getEventId();
			String eventName = events[0].getEventName();
			String beginTime = df.format(events[0].getBeginTime());
			String endTime = df.format(events[0].getEndTime());
			String place = events[0].getPlace();
			
			Context context = getApplicationContext();
			String expandedTitle = eventName;
			String expandedText = "On:" + beginTime + ". At: " + place;
			Intent addEventIntent = new Intent(MTCEventsQueryService.this, MTCAddEvent.class);
			addEventIntent.putExtra("EVENT_ID", eventId);
			addEventIntent.putExtra("EVENT_NAME", eventName);
			addEventIntent.putExtra("BEGIN_TIME", beginTime);
			addEventIntent.putExtra("END_TIME", endTime);
			addEventIntent.putExtra("PLACE", place);
			PendingIntent launchIntent = PendingIntent.getActivity(context, notifyId, addEventIntent, 0);
			
			newEventNotification.setLatestEventInfo(context, expandedTitle, expandedText, launchIntent);
			newEventNotification.when = System.currentTimeMillis();
			newEventNotification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			
			notificationManager.notify(notifyId, newEventNotification);
			notifyId++;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}	
	}
	
	@SuppressWarnings(value = {"deprecation"})
	@Override
	public void onCreate() {
		String ALARM_ACTION;
		
		// Create Events DB Adapter
		eventsDBAdapter = new MTCEventsDBAdapter(this);
		
		// Open or create the database
		eventsDBAdapter.open();
		
		int icon = R.drawable.notification;
		String tickerText = "New Volunteering Event";
		long when = System.currentTimeMillis();
		
		newEventNotification = new Notification(icon, tickerText, when);
		
		// Set up alarm manager
		alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		ALARM_ACTION = MTCEventsAlarmReceiver.ACTION_REFRESH_EVENTS_ALARM;
		Intent intent = new Intent(ALARM_ACTION);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		// Clean up past registered events
		cleanPastEvents();
		
		// Retrieve the shared preferences
		Context context = getApplicationContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		// Get preferences
		boolean autoUpdate = preferences.getBoolean(MTCPreferences.PREF_AUTO_UPDATE, false);
		int updateFreq = Integer.valueOf(preferences.getString(MTCPreferences.PREF_UPDATE_FREQ, "12"));
		
		// Set up alarm manager if auto update is enabled
		if (autoUpdate) {
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			// Production interval (in hours)
			/*
			long timeToQuery = SystemClock.elapsedRealtime() + updateFreq * 60 * 60 * 1000;
			alarms.setRepeating(alarmType, timeToQuery, updateFreq * 60 * 60 * 1000, alarmIntent);
			*/
			// Testing interval (in minutes)
			long timeToQuery = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
			alarms.setRepeating(alarmType, timeToQuery, updateFreq * 60 * 1000, alarmIntent);
			
		} else {
			alarms.cancel(alarmIntent);
		}
		
		// Run inquiry task
		inquiryEvents();
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		eventsDBAdapter.close(); 
	}
	
	public void cleanPastEvents() {
		// Get all events from events database
		Cursor cursor = eventsDBAdapter.getAllEventsCursor();
		
		// Get todays's date
		Date now = new Date();
		
		if (cursor.moveToFirst()) {
			do {
				String eventID = cursor.getString(cursor.getColumnIndex(eventsDBAdapter.KEY_EVENT_ID));
				long beginTime = cursor.getLong(cursor.getColumnIndex(eventsDBAdapter.KEY_BEGIN_TIME));
				Date beginTimeDate = new Date(beginTime);
				
				if (beginTimeDate.before(now)) {
					eventsDBAdapter.removeEventByID(eventID);
				}		
			} while (cursor.moveToNext());
		}
	}
	
	private ArrayList<MTCEvent> processNewEvents(ArrayList<MTCEventJson> newEventsList) {
		ArrayList<MTCEvent> newEvents = new ArrayList<MTCEvent>();
		
		// Check for each events feed from Calendar server
		for (MTCEventJson event : newEventsList) {
			// Convert it to real MTCEvent POJO
			MTCEvent newEvent = event.convertToMTCEvent();
			
			if (!checkEventExist(newEvent)) {
				newEvents.add(newEvent);
			}
		}
		
		return newEvents;
	}

	private boolean checkEventExist(MTCEvent newEvent) {
		// Get all events from events database
		Cursor cursor = eventsDBAdapter.getAllEventsCursor();
		
		if (cursor.moveToFirst()) {
			do {
				String eventID = cursor.getString(cursor.getColumnIndex(eventsDBAdapter.KEY_EVENT_ID));
				
				if (eventID.equals(newEvent.getEventId())) {
					return true;
				}
			} while (cursor.moveToNext());
		}
		return false;
	}
	
	public void inquiryEvents() {
		if (inquiryTask == null || inquiryTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			inquiryTask = new EventsInquiryTask();
			inquiryTask.execute((Void[])null);
		}
	}
}
