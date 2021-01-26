package com.techelevator.integrationtests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.jdbc.JdbcFoodOrderDao;
import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Order;

public class JdbcFoodOrderDaoIntegrationTest extends DaoIntegrationTest {

	@Test
	public void getFoodItems_returns_food_for_event() {
		String sql = "SELECT COUNT(*) as food_count FROM food WHERE event_id = ?";
		SqlRowSet select = jdbcTemplate.queryForRowSet(sql, testEventId1);
		select.next();

		long expected = select.getLong("food_count");
		long actual = (long) foodOrderDao.getFoodItems(testEventId1).size();

		Assert.assertEquals(expected, actual);

	}

	@Test
	public void food_can_be_created_and_updated() {
		
		Assert.assertEquals(1, foodOrderDao.getFoodItems(testEventId2).size());
		Food testFood = new Food();
		testFood.setFoodName("Testing");
		testFood.setVegetarian(false);
		testFood.setVegan(false);
		testFood.setGlutenFree(false);
		testFood.setNutFree(false);
		testFood.setDescription("Testing");
		testFood.setEventId(testEventId2);
		testFood.setFoodCategory("Entree");
		foodOrderDao.createFoodItems(testFood);

		Assert.assertNotNull(foodOrderDao.getFoodItems(testEventId2));
		Assert.assertEquals(2, foodOrderDao.getFoodItems(testEventId2).size());

		Assert.assertEquals("Testing", testFood.getFoodName());

		testFood.setFoodName("Test");
		foodOrderDao.updateFoodItems(testFood);

		Assert.assertEquals("Test", testFood.getFoodName());

	}

	@Test
	public void deleteFoodItem_deletes_correctly() {
		Assert.assertEquals(1, foodOrderDao.getFoodItems(testEventId2).size());
		foodOrderDao.deleteFoodItem(testEventId2, testFoodId2);

		Assert.assertEquals(0, foodOrderDao.getFoodItems(testEventId2).size());
	}

	@Test
	public void getEventOrders_returns_orders() {
		String sql = "SELECT COUNT(*) as order_count FROM orders " + "WHERE event_id = ?";
		SqlRowSet select = jdbcTemplate.queryForRowSet(sql, testEventId1);
		select.next();

		long expected = select.getLong("order_count");
		long actual = (long) foodOrderDao.getEventOrders(testEventId1).size();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void orders_can_be_created_and_updated() {
		Order testOrder = getOrderById(testOrderId);

		foodOrderDao.createOrder(testOrder);

		Assert.assertNotNull(foodOrderDao.getEventOrders(testEventId2));
		Assert.assertEquals(2, foodOrderDao.getEventOrders(testEventId2).size());

		testOrder.setStatus("done");
		foodOrderDao.updateOrder(testOrder);
		Assert.assertEquals("done", testOrder.getStatus());
	}

	@Test
	public void deleteOrder() {
		Assert.assertEquals(1, foodOrderDao.getEventOrders(testEventId2).size());
		foodOrderDao.deleteOrder(testEventId2, testOrderId);

		Assert.assertEquals(0, foodOrderDao.getEventOrders(testEventId2).size());
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
		food.setEventId(row.getLong("event_id"));
		food.setFoodCategory(row.getString("food_category"));

		return food;
	}

	private Food getFoodById(long foodId) {
		String sqlQuery = "SELECT * FROM food WHERE food_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, foodId);

		Food food = null;
		if (results.next()) {
			food = mapRowToFood(results);
		}
		return food;
	}
	
	private Order getOrderById(long orderId) {
		String sqlQuery = "SELECT * FROM orders WHERE order_id=?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlQuery, orderId);

		Order order = null;
		if (results.next()) {
			order = mapRowToOrder(results);
		}
		return order;
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
