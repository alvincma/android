package org.tzuchi.us.nca.mtc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MTCMainActivity extends Activity {
    
    /*
     * Option menu item definitions
     */
    private static final int MENU_EVENTS = Menu.FIRST;
    private static final int MENU_UPDATE = Menu.FIRST + 1;
    private static final int MENU_PREFERENCES = Menu.FIRST + 2;
    
    /*
     * Result codes
     */
    private static final int SHOW_PREFERENCES = 1;
    
    /*
     * Preferences variables
     */
    boolean autoUpdate = false;
    int inquiryFreq = 0;
    
    /*
     * Service variables
     */
    MTCEventsQueryService mService;
    boolean mBound = false;
    
    private ServiceConnection mConnection = new ServiceConnection() {
    	
    	public void onServiceConnected(ComponentName className, IBinder service) {
    		MTCEventsQueryService.LocalBinder binder = (MTCEventsQueryService.LocalBinder)service;
    		mService = binder.getEventsQueryService();
    		mBound = true;
    	}
    	
    	public void onServiceDisconnected(ComponentName arg0) {
    		mBound = false;
    	}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mtcmain);
        
        readPreferencesUpdate();
        
        Intent intent = new Intent(MTCMainActivity.this, MTCEventsQueryService.class);
        
        startService(intent);
    }
    
    @Override 
    protected void onStart() {
    	super.onStart();
    	
    	// Bind to Event Query Service
    	Intent intent = new Intent(this, MTCEventsQueryService.class);
    	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	// Unbind from service
    	if (mBound) {
    		unbindService(mConnection);
    		mBound = false;
    	}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	// Stop the service
    	Intent intent = new Intent(MTCMainActivity.this, MTCEventsQueryService.class);
    	stopService(intent);
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem itemEvents = menu.add(0, MENU_EVENTS, Menu.NONE, R.string.menu_registered_events);
    	MenuItem itemUpdate = menu.add(0, MENU_UPDATE, Menu.NONE, R.string.menu_refresh);
    	MenuItem itemPrefs = menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
    	
    	// Assign Icons
    	itemEvents.setIcon(R.drawable.events);
    	itemUpdate.setIcon(R.drawable.update);
    	itemPrefs.setIcon(R.drawable.prefs);
    	
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	switch (item.getItemId()) {
    		case MENU_EVENTS:
    			Intent ei = new Intent(this, MTCRegisteredEvents.class);
    			startActivity(ei);
    			return true;
    		case MENU_UPDATE: 
    			if (mBound) {
    				mService.inquiryEvents();
    			}
    			return true;
    		case MENU_PREFERENCES:
    			Intent pi = new Intent(this, MTCPreferences.class);
    			startActivityForResult(pi, SHOW_PREFERENCES);
    			return true;
    	}
    	return false;
    }
    
    private void readPreferencesUpdate() {
    	Context context = getApplicationContext();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	autoUpdate = prefs.getBoolean(MTCPreferences.PREF_AUTO_UPDATE, true);
    	inquiryFreq = Integer.parseInt(prefs.getString(MTCPreferences.PREF_UPDATE_FREQ, "12"));
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (requestCode == SHOW_PREFERENCES) {
    		if (resultCode == Activity.RESULT_OK) {
    			readPreferencesUpdate();
    		}
    	}
    }
}
