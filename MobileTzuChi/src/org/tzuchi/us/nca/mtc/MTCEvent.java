package org.tzuchi.us.nca.mtc;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MTCEvent {
	private String eventId;
	private String eventName;
	private Date beginTime;
	private Date endTime;
	private String place;
	
	public MTCEvent(String _eventId,
			        String _eventName,
					Date _beginTime,
					Date _endTime,
					String _place) {
		eventId = _eventId;
		eventName = _eventName;
		beginTime = _beginTime;
		endTime = _endTime;
		place = _place;
	}
	
	// Accessor methods 
	public String getEventId() {
		return eventId;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public Date getBeginTime() {
		return beginTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setEventId(String _eventId) {
		eventId = _eventId;
	}
	
	public void setEventName(String _eventName) {
		eventName = _eventName;
	}

	public void setBeginTime(Date _beginTime) {
		beginTime = _beginTime;
	}
	
	public void setEndTime(Date _endTime) {
		endTime = _endTime;
	}
	
	public void setPlace(String _place) {
		place = _place;
	}
}
