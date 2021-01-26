package com.techelevator.model.dao;

import java.util.List;

import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Order;

public interface FoodOrderDao {
	
	public List<Food> getFoodItems(long eventId, long userId);
	public Event createFoodItems(Food food, long eventId, long userId);
	public Event updateFoodItems(long eventId, long itemId, long userId, Food food);
	public Integer deleteFoodItem(long eventId, long userId, long foodId);
	public List<Order> getEventOrders(long eventId);
	public Event createOrder(long eventId, long userId, Order order);
	public Event updateOrder(long eventId, long orderId, long userId, Order order);
	public Integer deleteOrder(long eventId, long userId, long orderId);
}
