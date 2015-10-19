package org.tzuchi.us.nca.mtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MTCEventsAlarmReceiver extends BroadcastReceiver {
	
	public static final String ACTION_REFRESH_EVENTS_ALARM = "org.tzuchi.us.nca.mtc.ACTION_REFRESH_EVENTS_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startIntent = new Intent(context, MTCEventsQueryService.class);
		context.startService(startIntent);
	}

}
