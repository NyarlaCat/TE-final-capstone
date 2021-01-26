package com.techelevator.integrationtests;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.EventAttendees;

public class JdbcEventDaoIntegrationTest extends DaoIntegrationTest {

	@Test
	public void getEventsForUser_returns_events_for_user() {

		String sql = "SELECT COUNT(*) as event_count FROM event WHERE event_id = ?";
		SqlRowSet select = jdbcTemplate.queryForRowSet(sql, testEventId1);
		select.next();

		long expected = select.getLong("event_count");
		long actual = (long) eventDao.getEventsForUser(testUserId).size();

		Assert.assertEquals(expected, actual);

	}

	@Test
	public void events_can_be_created_and_updated() {
		Event testEvent = getEventById(testEventId2);

		eventDao.createEvent(testEvent, testUserId);
		

		eventDao.createEvent(testEvent, testUserId);



		testEvent.setName("New Test Name");
		
	}
	
	@Test
	public void createAddress_returns_address() {
		Address address = getAddressById(testAddressId);
		eventDao.createAddress(address, testUserId);
		//Assert.assertEquals(address.getStreetAddress(), eventDao.getAddress(testAddressId).getStreetAddress());
	}

	@Test
	public void getEventAttendees_returns_event_attendees_list() {

		//String sql = "SELECT COUNT(*) as attendees_count FROM event_attendees WHERE event_id = ?";
		//SqlRowSet select = jdbcTemplate.queryForRowSet(sql, testEventId1);
		//select.next();

		//long expected = select.getLong("attendees_count");
		//long actual = (long) eventDao.getEventAttendees(testEventId1).size();

		//Assert.assertEquals(expected, actual);
	}


	private Event getEventById(long eventId) {
		String sqlQuery = "SELECT * FROM event WHERE event_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, eventId);

		Event event = null;
		if (results.next()) {
			event = mapRowToEvent(results);
		}
		return event;
	}

	private Event mapRowToEvent(SqlRowSet row) {
		Event event = new Event();

		event.setEventId(row.getLong("event_id"));
		event.setName(row.getString("event_name"));
		event.setDate(row.getDate("event_date").toLocalDate());
		event.setTime(row.getString("event_time"));
		event.setDescription(row.getString("description"));
		event.setDeadline(row.getDate("deadline").toLocalDate());
		event.setAddressId(row.getLong("address_id"));

		return event;
	}

	private Address getAddressById(long addressId) {
		String sqlQuery = "SELECT * FROM address WHERE address_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, addressId);

		Address address = null;
		if (results.next()) {
			address = mapToAddress(results);
		}
		return address;
	}

	private Address mapToAddress(SqlRowSet results) {
		Address address = new Address();

		address.setAddressId(results.getInt("address_id"));
		address.setStreetAddress(results.getString("street_address"));
		address.setCity(results.getString("city"));
		address.setState(results.getString("state"));
		address.setZip(results.getString("zip"));
		address.setUserId(results.getLong("user_id"));

		return address;
	}

	private EventAttendees getEventAttendeesByEventId(long eventId) {
		String sqlQuery = "SELECT * FROM event_attendees WHERE event_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, eventId);

		EventAttendees attendee = null;
		if (results.next()) {
			attendee = mapRowToEventAttendees(results);
		}
		return attendee;
	}

	private EventAttendees mapRowToEventAttendees(SqlRowSet row) {
		EventAttendees eventAttendees = new EventAttendees();

		eventAttendees.setEventId(row.getLong("event_id"));
		eventAttendees.setUserId(row.getLong("user_id"));
		eventAttendees.setHost(row.getBoolean("is_host"));
		eventAttendees.setAttending(row.getBoolean("is_attending"));
		eventAttendees.setFirstName(row.getString("first_name"));
		eventAttendees.setLastName(row.getString("last_name"));
		eventAttendees.setAdultGuests(row.getInt("adult_guests"));
		eventAttendees.setChildGuests(row.getInt("child_guests"));

		return eventAttendees;
	}

}
