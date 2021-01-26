package com.techelevator.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.RequestAuthProvider;
import com.techelevator.controller.response.Response;
import com.techelevator.controller.response.ResponseError;
import com.techelevator.controller.response.ValidationError;
import com.techelevator.model.dao.AddressDao;
import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.User;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AddressController {
	
	@Autowired
	private AddressDao addressDao;
	
	private static final Response<?> UNKNOWN_USER_RESPONSE = new Response<>(new ResponseError("Unknown User"));
	
	/**
	 * Gets the address by the ID
	 * Roles: Anonymous (must have created the address)
	 * 
	 * @param addressid the ID of the address
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Address object</li>
	 * 		<li><h3>HTTP 400</h3> Address doesn't exist or user does not have access to address</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
	 */
	@GetMapping(path="/address/{addressid}")
	public Response<?> getAddressByID(@PathVariable long addressid,
									  HttpServletRequest request,
									  HttpServletResponse response) {
		User user = getUser(request);
		if( user == null ) {
			return badUser(response);
		}
		Address address = addressDao.getAddress(addressid, user.getId());
		
		if( address != null ) {
			return new Response<>(address);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new Response<>(new ResponseError("Invalid address"));
		}
	}
	
	
	/**
	 * Gets a list of addresses that the user has entered
	 * Roles: Attendee | Host | Chef
	 * 
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> List of Address objects</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
	 */
	@GetMapping(path="/addresses")
	public Response<?> getAddressesForUser(HttpServletRequest request,
										   HttpServletResponse response) {
		User user = getUser(request);
		if( user == null ) {
			return badUser(response);
		}
		List<Address> addresses = addressDao.getAddressesForUser(user.getId());
		
		return new Response<>(addresses);
	}

	/**
	 * Adds a new address
	 * Roles: Anonymous
	 * 
	 * @param address Address object to add
	 * @param result Validation results of creating the Address object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 201</h3> Address object</li>
	 * 		<li><h3>HTTP 400</h3> Bad validation</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
	 */
	@PostMapping("/address")
	public Response<?> addAddress(@Valid @RequestBody Address address,
								   BindingResult result,
								   HttpServletRequest request,
								   HttpServletResponse response) {
		User user = getUser(request);
		if( user == null ) {
			return badUser(response);
		}
		if( result.hasErrors() ) {
			return badValidation(result, response);
		}
		// override Address object
		address.setUserId(user.getId());
		
		address = addressDao.createAddress(address);
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		return new Response<>(address);
	}

    private User getUser(HttpServletRequest request) {
    	return (User)request.getAttribute(RequestAuthProvider.USER_KEY);
    }
    private Response<?> badUser(HttpServletResponse response) {
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	return UNKNOWN_USER_RESPONSE;
    }
    private Response<ResponseError> badValidation(BindingResult result,
    											  HttpServletResponse response) {
    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	return new Response<ResponseError>(ValidationError.createList(result));
    }
}
