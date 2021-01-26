package com.techelevator.model.pojo;

public class Address {

	private long addressId;
	private String streetAddress;
	private String city;
	private String state;
	private String zip;
	private long userId;

	public long getAddressId() {
		return addressId;
	}

	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
