package com.techelevator.model.dao;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.EventAttendees;
import com.techelevator.model.pojo.Invitee;

public interface EventDao {
	
	public List<Event> getEventsForUser(long userId);
	public Event createEvent(Event event, long userID) throws DataIntegrityViolationException;
	public Event deleteEvent(long eventID, long userID);
	public List<EventAttendees> getEventAttendees(long eventID, long userID);
	public EventAttendees addEventAttendee(long eventID, long userID, EventAttendees attendees) throws DataIntegrityViolationException;
	public Event updateEvent(long eventID, long userID, Event event);
	public Event getEventDetails(long eventID, long userID);
	
	public Invitee sendInvite(long eventID, long userID, Invitee invitee);

}
