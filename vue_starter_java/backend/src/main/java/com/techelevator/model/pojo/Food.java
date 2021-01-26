package com.techelevator.model.pojo;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Food {

	private long foodId;
	
	@NotBlank(message="Food name needs to be provided")
	@Length(max=50, message="Food name cannot be over 50 characters")
	private String foodName;
	
	// these are all nullable in the database
	private boolean vegetarian;
	private boolean vegan;
	private boolean glutenFree;
	private boolean nutFree;
	
	@Length(max=255, message="Food description cannot be over 255 characters")
	private String description;
	private long eventId;
	private String foodCategory;

	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}

	public boolean isVegan() {
		return vegan;
	}

	public void setVegan(boolean vegan) {
		this.vegan = vegan;
	}

	public boolean isGlutenFree() {
		return glutenFree;
	}

	public void setGlutenFree(boolean glutenFree) {
		this.glutenFree = glutenFree;
	}

	public boolean isNutFree() {
		return nutFree;
	}

	public void setNutFree(boolean nutFree) {
		this.nutFree = nutFree;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getFoodCategory() {
		return foodCategory;
	}

	public void setFoodCategory(String foodCategory) {
		this.foodCategory = foodCategory;
	}

}
