package org.tzuchi.us.nca.mtc;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class MTCRegisteredEvents extends Activity {
	
	private static final int UNREGISTER_EVENT = Menu.FIRST;
	
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	
	private ArrayList<MTCEvent> registeredEvents;
	private ListView listView;
	private EditText editText;
	private MTCEventAdapter ea;
	
	private MTCEventsDBAdapter eventsDBAdapter;
	private Cursor eventsCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);
		
		listView = (ListView)findViewById(R.id.eventsListView);
		editText = (EditText)findViewById(R.id.eventsEditText);
		
		registeredEvents = new ArrayList<MTCEvent>();
		int resID = R.layout.event;
		ea = new MTCEventAdapter(this, resID, registeredEvents);
		listView.setAdapter(ea);
		
		registerForContextMenu(listView);
		// restoreUIState() ?
		
		// Create DB adapter
		eventsDBAdapter = new MTCEventsDBAdapter(this);
		
		// Open or create the database
		eventsDBAdapter.open();
		
		// Populate registered event list
		populateRegisteredEventsList();
	}
	
	private void populateRegisteredEventsList() {
		// Get all registered events from the database
		eventsCursor = eventsDBAdapter.getAllRegisteredEvents();
		startManagingCursor(eventsCursor);
		
		// Update the array
		updateArray();
	}
	
	private void updateArray() {
		eventsCursor.requery();
		
		registeredEvents.clear();
		
		if (eventsCursor.moveToFirst()) {
			do {
				String eventID = eventsCursor.getString(eventsCursor.getColumnIndex(eventsDBAdapter.KEY_EVENT_ID));
				String name = eventsCursor.getString(eventsCursor.getColumnIndex(eventsDBAdapter.KEY_NAME));
				long beginTime = eventsCursor.getLong(eventsCursor.getColumnIndex(eventsDBAdapter.KEY_BEGIN_TIME));
				long endTime = eventsCursor.getLong(eventsCursor.getColumnIndex(eventsDBAdapter.KEY_END_TIME));
				String place = eventsCursor.getString(eventsCursor.getColumnIndex(eventsDBAdapter.KEY_PLACE));
				
				MTCEvent newEvent = new MTCEvent(eventID, name, new Date(beginTime), new Date(endTime), place);
				registeredEvents.add(0, newEvent);
			} while (eventsCursor.moveToNext());
			
			ea.notifyDataSetChanged();
		}
	}
	
	@Override 
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SELECTED_INDEX_KEY, listView.getSelectedItemPosition());
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		int pos = -1;
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY)) {
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
			}
		}
		
		listView.setSelection(pos);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override 
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		
		menu.setHeaderTitle("Selected Registered Volunteering Events");
		MenuItem itemRemove = menu.add(0, UNREGISTER_EVENT, Menu.NONE, R.string.unregister);
		
		// Assign icon
		itemRemove.setIcon(R.drawable.remove_event);
		
		// Set shortcut
		itemRemove.setShortcut('0', 'r');
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		switch (item.getItemId()) {
			case UNREGISTER_EVENT:
				AdapterView.AdapterContextMenuInfo menuInfo;
				menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
				int index = menuInfo.position;
				removeItem(index);
				
				return true;
		}
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Close the database
		eventsDBAdapter.close();
	}
	
	private void removeItem(int index) {
		// Remove event by event ID
		MTCEvent event = registeredEvents.get(index);
		final String selectedEventId = event.getEventId();
		eventsDBAdapter.removeEventByID(selectedEventId);
		
		updateArray();
	}
}
