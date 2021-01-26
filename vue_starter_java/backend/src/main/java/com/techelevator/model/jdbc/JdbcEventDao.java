package com.techelevator.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techelevator.model.dao.EventDao;
import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.EventAttendees;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Invitee;
import com.techelevator.model.pojo.Order;

@Component
public class JdbcEventDao implements EventDao {
	
	private JdbcTemplate jdbc;
	
	private static final String EVENT_COLUMNS = "SELECT event.event_id, event.event_name, event.event_date, event.event_time, event.description, event.deadline, event.address_id, event_attendees.is_host, event_attendees.is_attending, event_attendees.user_id, invitees.email ";
	
	@Autowired
	public JdbcEventDao(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	
	private static final String EVENT_FOR_USERS_COLUMNS = "SELECT event.event_id, event.event_name, event.event_date, event.event_time, event.description, event.deadline, event.address_id, event_attendees.is_host, event_attendees.is_attending, event_attendees.user_id ";
	
	@Override
	public List<Event> getEventsForUser(long userId) {
		String sqlQuery = 
				EVENT_FOR_USERS_COLUMNS
				+"FROM event_attendees "
				+"JOIN event USING(event_id) "
				+"WHERE user_id = ?";
		
		SqlRowSet eventResults = jdbc.queryForRowSet(sqlQuery, userId);
		
		List<Event> eventListForUser = new ArrayList<Event>();
		
		while(eventResults.next()) {
			Event event = mapRowToEvent(eventResults);
			event.setInvited(false);
			eventListForUser.add(event);
		}

		// get invitations
		sqlQuery = "SELECT event_id, event_name, event_date, event_time, description, deadline, address_id, email, false as is_host, null as is_attending, user_id "
						+ "FROM invitees "
						+ "JOIN users USING(email) "
						+ "JOIN event USING(event_id) "
						+ "WHERE user_id = ?";
		
		eventResults = jdbc.queryForRowSet(sqlQuery, userId);
		
		while( eventResults.next()) {
			Event event = mapRowToEvent(eventResults);
			
			// see if already in the list
			boolean found = false;
			for( Event eventListElement : eventListForUser ) {
				if( eventListElement.getEventId() == event.getEventId() ) {
					found = true;
					break;
				}
			}
			if( found ) {
				continue;
			}
			
			event.setInvited(true);
			eventListForUser.add(event);
		}
		
		return eventListForUser;
	}

	@Override
	@Transactional
	public Event createEvent(Event event, long userID) throws DataIntegrityViolationException {
		// start a transaction to rollback if needed
		String sqlQuery = "INSERT INTO event (event_name, event_date, event_time, description, deadline, address_id) "
						+ "VALUES (?, ?, ?, ?, ?, ?) RETURNING event_id";

		long eventID = jdbc.queryForObject(sqlQuery, Long.class,
						event.getName(),
						event.getDate(),
						event.getTime(),
						event.getDescription(),
						event.getDeadline(),
						event.getAddressId());
		
		event.setEventId(eventID);
		
		sqlQuery = "INSERT INTO event_attendees (event_id, user_id, is_host, is_attending) "
					 + "VALUES(?, ?, true, true)";
		jdbc.update(sqlQuery, eventID, userID);

		return event;
	}

	@Override
	public Event deleteEvent(long eventID, long userID) {
		// make sure user is host
		Event event = this.getEventDetails(eventID, userID);
		
		if( event == null || event.isHosting() == false ) {
			return null;
		}
		
		// MANUAL CASCADE: delete from invitees
		String sqlString = "DELETE FROM invitees WHERE event_id = ?";
		jdbc.update(sqlString, eventID);
		
		// MANUAL CASCADE: delete from event_attendees
		sqlString = "DELETE FROM event_attendees WHERE event_id = ?";
		jdbc.update(sqlString, eventID);
		
		// MANUAL CASCADE: delete from orders
		sqlString = "DELETE FROM orders WHERE event_id = ?";
		jdbc.update(sqlString, eventID);
		
		// MANUAL CASCADE: delete from food
		sqlString = "DELETE FROM food WHERE event_id = ?";
		jdbc.update(sqlString, eventID);
		
		sqlString = "DELETE FROM event WHERE event_id = ?";
		
		int updates = jdbc.update(sqlString, eventID);
		
		if( updates > 0 ) {
			return event;
		}
		return null;
	}

	@Override
	public List<EventAttendees> getEventAttendees(long eventID, long userID) {
		// see if invited or attendee
		List<Event> events = this.getEventsForUser(userID);
		boolean found = false;
		for( Event event : events ) {
			if( event.getEventId() == eventID ) {
				found = true;
				break;
			}
		}
		if( !found ) {
			return null;
		}
		String sqlString =	"SELECT event_attendees.event_id, event_attendees.user_id, event_attendees.is_host, event_attendees.is_attending, event_attendees.first_name, event_attendees.last_name, event_attendees.adult_guests, event_attendees.child_guests "
							+ "FROM event_attendees "
							+ "WHERE event_id = ?";
		
		SqlRowSet attendeeResults = jdbc.queryForRowSet(sqlString, eventID);
		
		List<EventAttendees> listOfAttendees = new ArrayList<EventAttendees>();
		
		while(attendeeResults.next()) {
			EventAttendees attendee = mapRowToEventAttendees(attendeeResults);
			
			// get the food items
			sqlString = "SELECT * FROM orders "
					  + "JOIN food USING(food_id) "
					  + "WHERE user_id = ?";
			
			SqlRowSet foodResults = jdbc.queryForRowSet(sqlString, attendee.getUserId());
			while( foodResults.next() ) {
				Order order = mapRowToOrder(foodResults);
				order.setFood(mapRowToFood(foodResults));
				attendee.addOrder(order);
			}
			
			listOfAttendees.add(attendee);
		}
		
		return listOfAttendees;
	}

	@Override
	public EventAttendees addEventAttendee(long eventID, long userID, EventAttendees attendees) throws DataIntegrityViolationException {
		// make sure the user is invited and not the host
		Event details = this.getEventDetails(eventID, userID);
		if( details == null || (details.isHosting() != null && details.isHosting() == true) || details.isInvited() == false ) {
			return null;
		}
		
		String sqlString = "INSERT INTO event_attendees(event_id, user_id, is_host, is_attending, first_name, last_name, adult_guests, child_guests) "
						 + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		int updates = jdbc.update(sqlString, eventID, attendees.getUserId(), attendees.isHost(), attendees.isAttending(), attendees.getFirstName(), attendees.getLastName(), attendees.getAdultGuests(), attendees.getChildGuests());
		
		EventAttendees newEventAttendees = null;
		if( updates > 0 ) {
			newEventAttendees = attendees;
		}
		
		return newEventAttendees;
	}
	
	@Override
	public Invitee sendInvite(long eventID, long userId, Invitee invitee) {
		// make sure user is host
		Event event = getEventDetails(eventID, userId);
		if( event == null || !event.isHosting() ) {
			return null;
		}
		
		String sqlString = 	"INSERT INTO invitees(email, event_id, role) "
							+ "VALUES(?, ?, ?) RETURNING invite_id";
		
		long inviteId = jdbc.queryForObject(sqlString, Long.class,
											invitee.getEmail(),
											invitee.getEventId(),
											invitee.getRole());
		
		invitee.setInviteId(inviteId);
		
		return invitee;
	}

	@Override
	public Event updateEvent(long eventID, long userID, Event event) {
		// make sure host
		Event origEvent = this.getEventDetails(eventID, userID);
		
		if( origEvent == null || origEvent.isHosting() == false ) {
			return null;
		}
		String sqlString = "UPDATE event SET "
						 + "event_name = ?, "
						 + "event_date = ?, "
						 + "event_time = ?, "
						 + "description = ?, "
						 + "deadline = ?, "
						 + "address_id = ? "
						 + "WHERE event_id = ?";
		
		jdbc.update(sqlString, event.getName(), event.getDate(), event.getTime(), event.getDescription(), event.getDeadline(), event.getAddressId(), event.getEventId());
		
		return event;
	}

	@Override
	public Event getEventDetails(long eventID, long userID) {
		// only allow event details if they're part of the event (attendees or invite)
		List<Event> events = this.getEventsForUser(userID);
		Event returnEvent = null;
		for( Event event : events ) {
			if( event.getEventId() == eventID ) {
				returnEvent = event;
				break;
			}
		}
		
		return returnEvent;
	}
	
	
	
	public Address createAddress(Address address, long userId) {
		String sqlQuery = "INSERT INTO address (street_address, city, state, zip, user_id) "
				 + "VALUES (?, ?, ?, ?, ?) RETURNING address_id";
		long addressId = jdbc.queryForObject(sqlQuery, Long.class,
							address.getStreetAddress(),
							address.getCity(),
							address.getState(),
							address.getZip(),
							address.getUserId());
		
		address.setAddressId(addressId);
		
		return address;
	}

	private Event mapRowToEvent(SqlRowSet row) {
		Event event = new Event();
		
		event.setEventId(row.getLong("event_id"));
		event.setName(row.getString("event_name"));
		event.setDate(row.getDate("event_date").toLocalDate());
		event.setTime(row.getString("event_time"));
		event.setDescription(row.getString("description"));
		event.setDeadline(row.getDate("deadline").toLocalDate());
		
		String booleanString = row.getString("is_host");
		Boolean booleanValue = null;
		if( booleanString != null && booleanString.equalsIgnoreCase("true") ) {
			booleanValue = true;
		} else if( booleanString != null && booleanString.equalsIgnoreCase("false") ) {
			booleanValue = false;
		}
		event.setHosting(booleanValue);
		
		booleanString = row.getString("is_attending");
		booleanValue = null;
		if( booleanString != null && booleanString.equalsIgnoreCase("true") ) {
			booleanValue = true;
		} else if( booleanString != null && booleanString.equalsIgnoreCase("false") ) {
			booleanValue = false;
		}
		event.setAttending(booleanValue);
		
		event.setAddressId(row.getLong("address_id"));
		event.setUserId(row.getLong("user_id"));
		
		return event;
	}
	
	private EventAttendees mapRowToEventAttendees(SqlRowSet row) {
		EventAttendees eventAttendees = new EventAttendees();
		
		eventAttendees.setEventId(row.getLong("event_id"));
		eventAttendees.setUserId(row.getLong("user_id"));
		eventAttendees.setHost(row.getBoolean("is_host"));
		eventAttendees.setAttending(row.getBoolean("is_attending"));
		// If the is_attending column was null, the user has not RSVP'd
		// if it is NOT null they have already RSVPed
		eventAttendees.setHasRsvped(!row.wasNull());
		eventAttendees.setFirstName(row.getString("first_name"));
		eventAttendees.setLastName(row.getString("last_name"));
		eventAttendees.setAdultGuests(row.getInt("adult_guests"));
		eventAttendees.setChildGuests(row.getInt("child_guests"));
		
		return eventAttendees;
	}

	private Food mapRowToFood(SqlRowSet row) {
		Food food = new Food();
		
		food.setFoodId(row.getLong("food_id"));
		food.setFoodName(row.getString("food_name"));
		food.setVegetarian(row.getBoolean("vegetarian"));
		food.setVegan(row.getBoolean("vegan"));
		food.setGlutenFree(row.getBoolean("gluten_free"));
		food.setNutFree(row.getBoolean("nut_free"));
		food.setDescription(row.getString("description"));
		food.setFoodCategory(row.getString("food_category"));
		food.setEventId(row.getLong("event_id"));

		
		return food;
	}
	
	private Order mapRowToOrder(SqlRowSet row) {
		Order order = new Order();
		
		order.setOrderId(row.getLong("order_id"));
		order.setEventId(row.getLong("event_id"));
		order.setUserId(row.getLong("user_id"));
		order.setFoodId(row.getLong("food_id"));
		order.setStatus(row.getString("status"));
		order.setQuantity(row.getInt("quantity"));
		
		return order;
	}
}
