package com.techelevator.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.authentication.RequestAuthProvider;
import com.techelevator.controller.response.Response;
import com.techelevator.controller.response.ResponseError;
import com.techelevator.controller.response.ResponseMap;
import com.techelevator.controller.response.ValidationError;
import com.techelevator.model.dao.FoodOrderDao;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.Food;
import com.techelevator.model.pojo.Order;
import com.techelevator.model.pojo.User;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FoodController {
	
	@Autowired
	private FoodOrderDao foodOrderDao;

	private static final Response<?> UNKNOWN_USER_RESPONSE = new Response<>(new ResponseError("Unknown User"));
	private static final Response<?> UNKNOWN_EVENT_RESPONSE = new Response<>(new ResponseError("Event not found"));
	
	/**
	 * Views the menu for the specified event.
	 * Roles: Attendee | Host | Chef
	 * 
	 * @param eventid Event ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> List of food items for event</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
	 */
    @GetMapping(path="/event/{eventid}/menu")
    public Response<?> getFoodItems(@PathVariable long eventid, 
    								HttpServletRequest request,
    								HttpServletResponse response) {
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	List<Food> foodItems = foodOrderDao.getFoodItems(eventid, user.getId());
    	if( foodItems == null ) {
    		return badEvent(response);
    	}
    	return new Response<>(foodItems);
    }

    /**
     * Adds a food item to the specified event's menu.
     * Roles: Host
     * 
     * @param eventid Event ID set by the path
     * @param food Food item to be added
     * @param result Validation results for building the Food object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 	<li><h3>HTTP 201</h3> Object containing the event object and the food object</li>
	 * 	<li><h3>HTTP 400</h3> Event doesn't exist, user does not have access to event, or failed validation</li>
	 * 	<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PostMapping(path="/event/{eventid}/menu")
    public Response<?> createFoodItems(@PathVariable long eventid,
    									@Valid @RequestBody Food food,
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
    	Event event = foodOrderDao.createFoodItems(food, eventid, user.getId());
    	
    	if( event != null ) {
	    	response.setStatus(HttpServletResponse.SC_CREATED);
	    	return new Response<>(
	    		new ResponseMap().put("event", event).put("food", food).build()
	    	);
    	} else {
    		return badEvent(response);
    	}
    }
    
    /**
     * Updates the specified food item for the specified event.
     * Roles: Host
     * 
     * @param eventid Event ID set by the path
     * @param itemid Item ID set by the path
     * @param food Food object to be updated
     * @param result Validation results for creating the food object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Object containing the event object and the updated food object</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist, user does not have access to event, or validation error</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PutMapping(path="/event/{eventid}/menu/{itemid}")
    public Response<?> updateFoodItems(@PathVariable long eventid, 
    									@PathVariable long itemid, 
    									@Valid @RequestBody Food food, 
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
    	
    	Event event = foodOrderDao.updateFoodItems(eventid, itemid, user.getId(), food);
    	
    	if( event != null ) {
    		return new Response<>(
    			new ResponseMap().put("event", event).put("food", food).build()
    		);
    	} else {
    		return badEvent(response);
    	}
    }
    
    /**
     * Delete the specified food item from the menu of the specified event.
     * Roles: Host
     * 
     * @param eventid Event ID set by the path
     * @param itemid Item ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Empty response</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @DeleteMapping(path="/event/{eventid}/menu/{itemid}")
    public Response<?> deleteFoodItem(@PathVariable long eventid, 
    								  @PathVariable long itemid, 
    								  HttpServletRequest request,
    								  HttpServletResponse response) {
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	// TODO: return food object that was deleted?
    	Integer deleted = foodOrderDao.deleteFoodItem(eventid, user.getId(), itemid);
    	
    	if( deleted != null && deleted > 0 ) {
	    	// successfully deleted a record
	    	return Response.EMPTY_SUCCESS;
    	} else {
	    	// no record to delete
	    	return badEvent(response);
    	}
    }
    
    /**
     * Gets all the submitted orders for the specified event.
     * Roles: Chef
     * 
     * @param all orders, completed orders, incomplete orders
     */
    @GetMapping(path="/event/{eventid}/orders")
    public Response<?> getEventOrders(@PathVariable long eventid, 
    								  HttpServletResponse response) {
    	// TODO Where is the chef role located?
    	return new Response<>(foodOrderDao.getEventOrders(eventid));
    }
    
    /**
     * Place an order for the specified event.
     * Roles: Attendee | Host
     * 
     * @param eventid Event ID set by the path
     * @param order Order to be created
     * @param result Validation results from creating the Order object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 201</h3> Object containing the event and the order that was added</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist, user does not have access to event, or validation error</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PostMapping(path="/event/{eventid}/order")
    public Response<?> createOrder(@PathVariable long eventid, 
    							   @Valid @RequestBody Order order, 
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
    	// override Order object
    	order.setEventId(eventid);
    	order.setUserId(user.getId());
    	order.setStatus("waiting");
    	
    	Event event = foodOrderDao.createOrder(eventid, user.getId(), order);
    	
    	if( event != null ) {
	    	response.setStatus(HttpServletResponse.SC_CREATED);
	    	return new Response<>(
	    		new ResponseMap().put("event", event).put("order", order).build()
	    	);
    	} else {
    		return badEvent(response);
    	}
    }
    
    /**
     * Updates an order for the specified event.
     * Roles: Attendee | Host
     * 
     * @param eventid Event ID set by the path
     * @param orderid Order ID set by the path
     * @param order Order object that contains the updated information
     * @param result Validation result from creating the Order object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Object containing the event and the order that was updated</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PutMapping(path="/event/{eventid}/order/{orderid}")
    public Response<?> updateOrder(@PathVariable long eventid, 
    								@PathVariable long orderid,
    								@Valid @RequestBody Order order,
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
    	// override Order object
    	order.setOrderId(orderid);
    	order.setEventId(eventid);
    	order.setUserId(user.getId());
    	
    	Event event = foodOrderDao.updateOrder(eventid, orderid, user.getId(), order);
    	
    	if( event != null ) {
    		return new Response<>(
    			new ResponseMap().put("event", event).put("order", order).build()
    		);
    	} else {
    		return badEvent(response);
    	}
    }
    
    /**
     * Deletes an order for the specified event.
     * Roles: Attendee | Host
     * 
     * @param eventid Event ID set by the path
     * @param orderid Order ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Empty response</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @DeleteMapping(path="/event/{eventid}/order/{orderid}")
    public Response<?> deleteOrder(@PathVariable long eventid,
    							   @PathVariable long orderid, 
    							   HttpServletRequest request,
    							   HttpServletResponse response) {
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	Integer updates = foodOrderDao.deleteOrder(eventid, user.getId(), orderid);
    	
    	if( updates != null && updates > 0 ) {
	    	// successfully deleted a record
	    	return Response.EMPTY_SUCCESS;
    	} else {
	    	// no record to delete
    		// TODO: maybe change this?
	    	return badEvent(response);
    	}
    }
    
    private User getUser(HttpServletRequest request) {
    	return (User)request.getAttribute(RequestAuthProvider.USER_KEY);
    }
    private Response<?> badUser(HttpServletResponse response) {
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	return UNKNOWN_USER_RESPONSE;
    }
    private Response<?> badEvent(HttpServletResponse response) {
    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	return UNKNOWN_EVENT_RESPONSE;
    }
    private Response<ResponseError> badValidation(BindingResult result,
    											  HttpServletResponse response) {
    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	return new Response<ResponseError>(ValidationError.createList(result));
    }
}
