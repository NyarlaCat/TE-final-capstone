package com.techelevator.integrationtests;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.authentication.PasswordHasher;
import com.techelevator.model.jdbc.JdbcEventDao;
import com.techelevator.model.jdbc.JdbcFoodOrderDao;
import com.techelevator.model.jdbc.JdbcUserDao;
import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.EventAttendees;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Order;
import com.techelevator.model.pojo.User;

public abstract class DaoIntegrationTest {

	/*
	 * Using this particular implementation of DataSource so that every database
	 * interaction is part of the same database session and hence the same database
	 * transaction
	 */
	private static SingleConnectionDataSource dataSource;
	
	protected JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
	private PasswordHasher passwordHasher;
	
	protected JdbcFoodOrderDao foodOrderDao = new JdbcFoodOrderDao(getDataSource());
	protected JdbcEventDao eventDao = new JdbcEventDao(getDataSource());
	protected JdbcUserDao userDao = new JdbcUserDao(getDataSource(), passwordHasher);
	protected Long testFoodId1;
	protected Long testFoodId2;
	protected Long testOrderId;
	protected Long testUserId;
	protected Long testEventId1;
	protected Long testEventId2;
	protected Long testAddressId;
	protected EventAttendees attendee;

	/*
	 * Before any tests are run, this method initializes the datasource for testing.
	 */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/cookoutdb");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/*
		 * The following line disables autocommit for connections returned by this
		 * DataSource. This allows us to rollback any changes after each test
		 */
		dataSource.setAutoCommit(false);
	}

	/*
	 * After all tests have finished running, this method will close the DataSource
	 */
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	/*
	 * Before each test, we insert data into the tables of our database so we won't
	 * have to rely on data already being there
	 */
	@Before
	public void setup() {
		//password is "fakepassword"
		String sqlInsertFakeUser = "INSERT INTO users (username, password, salt, email, date_registered) "
				+ "VALUES (?, ?, ?, ?, ?) RETURNING user_id";

		testUserId = jdbcTemplate.queryForObject(sqlInsertFakeUser, Long.TYPE, "FakeUser2", "Pn74iOeZYhkH9F8JfExrWA==", "8U4SVwd0wFj7Un21FRdX9LxmKRdBcUr9yGbr19aU5ZlsTlQG6CEOEO4b8avJUVw0RMUkniiGRS8VNMqWHZpcExhlCsK06jgnVxSIMBElr2rEpqTzi3uiTvmhM6wyGXVlHHoshMMgtx6I/lAQYKpK3Hv2z0aiP99MI1URiY983p8=", "Fake2@Fake.com", Timestamp.valueOf("2019-12-11 17:26:30"));
		
		String sqlInsertFakeAddress = "INSERT INTO address (street_address, city, state, zip, user_id) "
				   + "VALUES (?, ?, ?, ?, ?) RETURNING address_id";

	    testAddressId = jdbcTemplate.queryForObject(sqlInsertFakeAddress, Long.TYPE, "1337 Very Fake Street", "Boston", "MA", "02134", testUserId);
		
		String sqlInsertFakeEvent = "INSERT INTO event (event_name, event_date, event_time, description, deadline, address_id) "
		    + "VALUES (?, ?, ?, ?, ?, ?) RETURNING event_id";

		testEventId1 = jdbcTemplate.queryForObject(sqlInsertFakeEvent, Long.TYPE, "Summer Cookout 2020", LocalDate.of(2020, 07, 05), "3pm", "Partying after the 4th of July!", LocalDate.of(2020, 06, 28), testAddressId);

		sqlInsertFakeEvent = "INSERT INTO event (event_name, event_date, event_time, description, deadline, address_id) "
			+ "VALUES (?, ?, ?, ?, ?, ?) RETURNING event_id";

		testEventId2 = jdbcTemplate.queryForObject(sqlInsertFakeEvent, Long.TYPE, "Fibonacci Day Party", LocalDate.of(2020, 11, 23), "5pm", "Celebrate Fibonacci Day!", LocalDate.of(2020, 11, 16), testAddressId);
		
		String sqlInsertFakeFood = "INSERT INTO food (food_name, vegetarian, vegan, gluten_free, " 
		    + "nut_free, description, event_id, food_category) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING food_id";

		testFoodId1 = jdbcTemplate.queryForObject(sqlInsertFakeFood, Long.TYPE, "Grilled Chicken", false, false, true, true, "Marinated grilled chicken", testEventId1, "Entree");

		sqlInsertFakeFood = "INSERT INTO food (food_name, vegetarian, vegan, gluten_free, " 
		    + "nut_free, description, event_id, food_category) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING food_id";

		testFoodId2 = jdbcTemplate.queryForObject(sqlInsertFakeFood, Long.TYPE, "Grilled Tofu", true, true, true, true, "Marinated grilled tofu", testEventId2, "Entree");
		
		String sqlInsertFakeOrder = "INSERT INTO orders (event_id, user_id, food_id, status, quantity) "
			+ "VALUES (?, ?, ?, ?, ?) RETURNING order_id";

		testOrderId = jdbcTemplate.queryForObject(sqlInsertFakeOrder, Long.TYPE, testEventId2, testUserId, testFoodId1, "done", 2);
		
		EventAttendees attendee = new EventAttendees();
		attendee.setEventId(testEventId1);
		attendee.setUserId(testUserId);
		attendee.setHost(true);
		attendee.setAttending(true);
		attendee.setFirstName("Fake");
		attendee.setLastName("Fakington");
		attendee.setAdultGuests(1);
		attendee.setChildGuests(0);
		eventDao.addEventAttendee(testEventId1, attendee);
	}

	/*
	 * After each test, we rollback any changes that were made to the database so
	 * that everything is clean for the next test
	 */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	/*
	 * This method provides access to the DataSource for subclasses so that they can
	 * instantiate a DAO for testing
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
}
