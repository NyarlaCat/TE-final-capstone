package com.techelevator.model.pojo;

public class Order {
	// these are set by the path/request
	private long eventId;
	private long userId;
	
	// set by the database
	private long orderId;
	
	private long foodId;
	private int quantity;
	private String status;
	
	private Food food;

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Food getFood() {
		return this.food;
	}
	public void setFood(Food food) {
		this.food = food;
	}
}
