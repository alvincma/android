package org.tzuchi.us.nca.mtc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MTCPreferences extends PreferenceActivity {
	
	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
	
	SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
		
		PreferenceManager.setDefaultValues(getBaseContext(), R.xml.userpreferences, false);

	}
}
