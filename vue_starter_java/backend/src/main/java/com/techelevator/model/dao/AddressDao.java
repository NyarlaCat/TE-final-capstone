package com.techelevator.model.dao;

import java.util.List;

import com.techelevator.model.pojo.Address;

public interface AddressDao {
	public List<Address> getAddressesForUser(long userID);
	public Address getAddress(long addressID, long userID);
	public Address createAddress(Address address);
}
