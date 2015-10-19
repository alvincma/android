package org.tzuchi.us.nca.mtc;

import java.text.SimpleDateFormat;
import java.util.*;

import android.content.Context;
import android.view.*;
import android.widget.*;

import org.tzuchi.us.nca.mtc.R;

public class MTCEventAdapter extends ArrayAdapter<MTCEvent> {
	
	int resource;
	
	public MTCEventAdapter(Context _context, int _resource, List<MTCEvent> _events) {
		super(_context, _resource, _events);
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout eventView;
		
		MTCEvent event = getItem(position);
		
		String nameString = event.getEventName();
		Date beginTime = event.getBeginTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aaa", Locale.ENGLISH);
		String beginTimeString = df.format(beginTime);
		
		if (convertView == null) {
			eventView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, eventView, true);
		} else {
			eventView = (LinearLayout)convertView;
		}
		
		TextView nameView = (TextView)eventView.findViewById(R.id.rowName);
		TextView beginTimeView = (TextView)eventView.findViewById(R.id.rowBeginTime);
		
		nameView.setText(nameString);
		beginTimeView.setText(beginTimeString);
		
		return eventView;
	}

}
