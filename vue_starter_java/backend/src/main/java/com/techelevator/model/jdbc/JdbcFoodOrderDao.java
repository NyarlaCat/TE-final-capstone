package com.techelevator.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.model.dao.EventDao;
import com.techelevator.model.dao.FoodOrderDao;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Order;

@Component
public class JdbcFoodOrderDao implements FoodOrderDao {

	JdbcTemplate jdbc;
	
	EventDao eventDao;
	
	@Autowired
	public JdbcFoodOrderDao(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
		eventDao = new JdbcEventDao(dataSource);
	}
	
	@Override
	public List<Food> getFoodItems(long eventId, long userId) {
		// make sure user is part of the event
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null ) {
			return null;
		}
		String sqlString = "SELECT food.food_id, food.food_name, food.vegetarian, food.vegan, food.gluten_free, food.nut_free, food.description, food.event_id, food.food_category "
						 + "FROM food "
						 + "WHERE event_id = ?";
		
		SqlRowSet results = jdbc.queryForRowSet(sqlString, eventId);
		
		List<Food> foodList = new ArrayList<>();
		while( results.next() ) {
			foodList.add(mapRowToFood(results));
		}
		
		return foodList;
	}

	@Override
	public Event createFoodItems(Food food, long eventId, long userId) {
		// make sure user is host
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null || event.isHosting() == false ) {
			return null;
		}
		
		String sqlString = "INSERT INTO food (food_name, vegetarian, vegan, gluten_free, nut_free, description, event_id, food_category) "
						 + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		int updates = jdbc.update(sqlString, food.getFoodName(), food.isVegetarian(), food.isVegan(), food.isGlutenFree(), food.isNutFree(), food.getDescription(), eventId, food.getFoodCategory());
		
		if( updates > 0 ) {
			return event;
		}
		
		return null;
	}

	@Override
	public Event updateFoodItems(long eventId, long itemId, long userId, Food food) {
		// make sure user is host
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null || event.isHosting() == false ) {
			return null;
		}
		
		String sqlString = "UPDATE food SET "
						 + "food_name = ?, "
						 + "vegetarian = ?, "
						 + "vegan = ?, "
						 + "gluten_free = ?, "
						 + "nut_free = ?, "
						 + "description = ?, "
						 + "food_category = ? "
						 + "WHERE food_id = ?";
		
		Food newFood = null;

		int updates = jdbc.update(sqlString, food.getFoodName(), food.isVegetarian(), food.isVegan(), food.isGlutenFree(), food.isNutFree(), food.getDescription(), food.getFoodCategory(), itemId);

		if( updates > 0 ) {
			return event;
		}
		
		return null;
	}

	@Override
	public Integer deleteFoodItem(long eventId, long userId, long foodId) {
		// make sure user is host
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null || event.isHosting() == false ) {
			return null;
		}
		String sqlString = "DELETE FROM food WHERE food_id = ?";
		
		return jdbc.update(sqlString, foodId);
	}

	@Override
	public List<Order> getEventOrders(long eventId) {
		String sqlString = "SELECT order_id, event_id, user_id, food_id, status, quantity FROM orders "
						 + "WHERE event_id = ?";
		
		SqlRowSet results = jdbc.queryForRowSet(sqlString, eventId);
		
		List<Order> orders = new ArrayList<>();
		while( results.next() ) {
			orders.add(mapRowToOrder(results));
		}
		
		return orders;
	}

	@Override
	public Event createOrder(long eventId, long userId, Order order) {
		// make sure user is part of event
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null ) {
			return null;
		}
		String sqlString = "INSERT INTO orders (event_id, user_id, food_id, status, quantity)"
						 + " VALUES(?, ?, ?, ?, ?)";
		
		int updates = jdbc.update(sqlString, order.getEventId(), order.getUserId(), order.getFoodId(), order.getStatus(), order.getQuantity());
		
		if( updates > 0 ) {
			return event;
		}
		
		return null;
	}

	@Override
	public Event updateOrder(long eventId, long orderId, long userId, Order order) {
		// make sure user is part of event
		Event event = eventDao.getEventDetails(eventId, userId);
		if( event == null ) {
			return null;
		}
		String sqlString = "UPDATE orders SET "
						 + "event_id = ?, "
						 + "user_id = ?, "
						 + "food_id = ?, "
						 + "status = ?, "
						 + "quantity = ? "
						 + "WHERE order_id = ?";
		
		int updates;
		// if user is host, can update anything
		if( event.isHosting() ) {
			updates = jdbc.update(sqlString, order.getEventId(), order.getUserId(), order.getFoodId(), order.getStatus(), order.getQuantity(), orderId);
		} else {
			// can only update their own order
			sqlString += " AND user_id = ?";
			updates = jdbc.update(sqlString, order.getEventId(), order.getUserId(), order.getFoodId(), order.getStatus(), order.getQuantity(), orderId, userId);
		}
		
		if( updates > 0 ) {
			return event;
		}
		
		return null;
	}

	@Override
	public Integer deleteOrder(long eventId, long userId, long orderId) {
		// if user is host, able to delete
		// if user is not host, able to delete only if they own the order
		Event event = eventDao.getEventDetails(eventId, userId);
		String sqlString = "DELETE FROM order WHERE orderId = ?";
		if( event.isHosting() == false ) {
			sqlString += " AND user_id = ?";
			return jdbc.update(sqlString, orderId, userId);
		}
		return jdbc.update(sqlString, orderId);
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
