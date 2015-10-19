package org.tzuchi.us.nca.mtc;

import com.fasterxml.jackson.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author Alvin
 * 
 * Helper class to help deserialization from JSON to POJO
 *
 */

public class MTCEventJson {
	private String eventId;
	private String eventName;
	private String beginTime;
	private String endTime;
	private String place;
	
	public MTCEventJson(@JsonProperty("eventId") String _eventId,
			            @JsonProperty("eventName") String _eventName,
			            @JsonProperty("beginTime") String _beginTime,
			            @JsonProperty("endTime") String _endTime,
			            @JsonProperty("place") String _place) {
		eventId = _eventId;
		eventName = _eventName;
		beginTime = _beginTime;
		endTime = _endTime;
		place = _place;
	}
	
	public MTCEvent convertToMTCEvent() {
		// Date conversion
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa", Locale.ENGLISH);
		Date begin_time;
		Date end_time;
		
		try {
			begin_time = df.parse(beginTime);
			end_time = df.parse(endTime);
			
			MTCEvent event = new MTCEvent(eventId, eventName, begin_time, end_time, place);
			return event;
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return null;
	}
	
	public String getEventId() {
		return eventId;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public String getBeginTime() {
		return beginTime;
	}
	
	public String getEndTime() {
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
	
	public void setBeginTime(String _beginTime) {
		beginTime = _beginTime;
	}
	
	public void setEndTime(String _endTime) {
		endTime = _endTime;
	}
	
	public void setPlace(String _place) {
		place = _place;
	}
}
