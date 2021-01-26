package com.techelevator.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.techelevator.email.EmailReplacementMap;
import com.techelevator.email.SendMail;
import com.techelevator.model.dao.AddressDao;
import com.techelevator.model.dao.EventDao;
import com.techelevator.model.dao.UserDao;
import com.techelevator.model.pojo.Address;
import com.techelevator.model.pojo.Event;
import com.techelevator.model.pojo.EventAttendees;
import com.techelevator.model.pojo.Invitee;
import com.techelevator.model.pojo.User;

@RestController
@CrossOrigin
@RequestMapping("/api")

public class EventController {
	
	@Autowired
	private EventDao eventDao;

	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SendMail email;
	
	private static final Response<?> UNKNOWN_USER_RESPONSE = new Response<>(new ResponseError("Unknown User"));
	private static final Response<?> UNKNOWN_EVENT_RESPONSE = new Response<>(new ResponseError("Event not found"));

	private static final String EMAIL_SUBJECT = "New invitation to event!";
	private static final String EMAIL_USEREXISTS_FILE = "invitation_alreadyUser.html";
	private static final String EMAIL_NEWUSER_FILE = "invitation_notAUser.html";
	
	/**
	 * Gets a list of all the events for the currently logged in user.
	 * Roles: Anonymous
	 * 
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> List of all the events the user has access to</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
	 */
    @GetMapping(path="/events")
    public Response<?> getEventsForUser(HttpServletRequest request, 
    									HttpServletResponse response) {
    	User user = getUser(request);
    	
    	if( user == null ) {
    		return badUser(response);
    	}
    	
    	return new Response<>(eventDao.getEventsForUser(user.getId()));
    }
    
    /**
     * Create cookout
     * Roles: Anonymous
     * 
     * @param event Event object to create
     * @param result Validation results from creating the Event object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 201</h3> Object containing the event and the order that was added</li>
	 * 		<li><h3>HTTP 400</h3> Event validation failed or error with inserting event (database constraints)</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PostMapping(path="/events")
    public Response<?> createEvent(@Valid @RequestBody Event event,
    								BindingResult result,
    								HttpServletRequest request,
    								HttpServletResponse response) {
    	User user = (User)request.getAttribute(RequestAuthProvider.USER_KEY);
    	if( user == null ) {
    		return badUser(response);
    	}
    	
    	if( result.hasErrors() ) {
        	// Form validation failed
            return badValidation(result, response);
    	}
    	
    	try {
    	Address tempAddress = event.getAddress();
    	tempAddress.setUserId(user.getId());
 
    	event.setAddress(addressDao.createAddress(tempAddress));
    	event.setAddressId(event.getAddress().getAddressId());
 
    	Event newEvent = eventDao.createEvent(event, user.getId());

    	
		response.setStatus(HttpServletResponse.SC_CREATED);

			return new Response<>(newEvent);
		} catch (DataIntegrityViolationException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new Response<>(ValidationError.createList(e));
		}
		
    	
    	
    }
    
    /**
     * Views the cookout details for the specified event.
     * Roles: Attendee | Host | Chef
     * 
     * @param eventid Event ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Event object</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @GetMapping(path="/event/{eventid}")
    public Response<?> getEventDetails(@PathVariable long eventid, 
    									HttpServletRequest request, 
    									HttpServletResponse response) {
    	User user = getUser(request);
    	
    	if( user == null ) {
    		return badUser(response);
    	}
    	
    	Event event = eventDao.getEventDetails(eventid, user.getId());
    	
    	if( event == null ) {
    		return badEvent(response);
    	}
    	
    	return new Response<>(event);
    }
    
    /**
     * Deletes the specified event.
     * Roles: Host
     * 
     * @param eventid Event ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> The event that was deleted</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @DeleteMapping(path="/event/{eventid}")
    public Response<?> deleteEvent(@PathVariable long eventid, 
    								HttpServletRequest request,
    								HttpServletResponse response) {
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	Event event = eventDao.deleteEvent(eventid, user.getId());
    	
    	if( event != null ) {
        	// successfully deleted a record
    		return new Response<>(event);
    	} else {
	    	// no record to delete
	    	return badEvent(response);
    	}
    }
    
    /**
     * Gets the attendees from the specified event.
     * Roles: Attendees | Host
     * 
     * @param eventid Event ID set by the path
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> List of event attendees for the event</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @GetMapping(path="/event/{eventid}/attendees")
    public Response<?> getEventAttendees(@PathVariable long eventid, 
    									HttpServletRequest request, 
    									HttpServletResponse response) {
    	
    	User user = getUser(request);
    	
    	if( user == null ) {
    		return badUser(response);
    	}
    	
    	List<EventAttendees> attendees = eventDao.getEventAttendees(eventid, user.getId());
    	
    	if( attendees == null || attendees.size() == 0 ) {
    		return badEvent(response);
    	}
    	
    	return new Response<>(attendees);
    }
    
    /**
     * Adds an attendee to the specified event.
     * Roles: Host
     * 
     * @param attendee EventAttendee object describing the person that's being invited
     * @param eventid Event ID set by the path
     * @param result Validation result for building the EventAttendee object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 201</h3> Object containing the event and the order that was added</li>
	 * 		<li><h3>HTTP 400</h3> Possible reasons:
	 * 			<ul>
	 * 				<li>Event doesn't exist</li>
	 * 				<li>User does not have access to event</li>
	 * 				<li>EventAttendee had validation errors</li>
	 * 			</ul>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PostMapping(path="/event/{eventid}/invite")
    public Response<?> sendInvites(@PathVariable long eventid,
    							   @Valid @RequestBody Invitee invitee,
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
    	// override invitee object
    	invitee.setEventId(eventid);
    	
    	Invitee newInvitee = null;
    	try {
    		newInvitee = eventDao.sendInvite(eventid, user.getId(), invitee);
    	} catch(DataIntegrityViolationException e) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return new Response<>(ValidationError.createList(e));
    	}
    	if( newInvitee == null ) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return new Response<>(new ResponseError("Failed to add new invitee"));
    	}
    	// everything worked, send the correct email
    	//// see if they're a user
    	String template = userDao.emailExists(invitee.getEmail()) ? EMAIL_USEREXISTS_FILE : EMAIL_NEWUSER_FILE;
    	Event event = eventDao.getEventDetails(eventid, user.getId());
    	email.send(invitee.getEmail(), EMAIL_SUBJECT,
    			email.getEmailFile(template), 
    			new EmailReplacementMap()
    				.put("{{username}}", user.getUsername())
    				.put("{{eventname}}", event.getName())
    				.build()
    	);
    	response.setStatus(HttpServletResponse.SC_CREATED);
    	return new Response<>(newInvitee);
    }
    
    /**
     * Adds event attendees to the specified event once they have RSVPd
     */
    
    @PostMapping(path="/event/{eventid}/rsvp/accept")
    public Response<?> addEventAttendee(@PathVariable long eventid, 
    									@Valid @RequestBody EventAttendees eventAttendee,
    									BindingResult result,
    									HttpServletRequest request, 
    									HttpServletResponse response){
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	if( result.hasErrors() ) {
    		return badValidation(result, response);
    	}
    	
    	try {
    		eventAttendee.setUserId(user.getId());
    		eventAttendee.setEventId(eventid);
    		EventAttendees eventAttendees = eventDao.addEventAttendee(eventid, user.getId(), eventAttendee);
    		if(eventAttendees == null) {
    			return badEvent(response);
    		}
    		return new Response<>(eventAttendees);
    	} catch (DataIntegrityViolationException e) {
			return new Response<>(ValidationError.createList(e));
		}
    }
    
    @PostMapping(path="/event/{eventid}/rsvp/decline")
    public Response<?> rsvpDecline(@PathVariable long eventid,
							    		@Valid @RequestBody EventAttendees decliningAttendees,
										BindingResult result,
    									HttpServletRequest request, 
    									HttpServletResponse response){
    	User user = getUser(request);
    	if( user == null ) {
    		return badUser(response);
    	}
    	if( result.hasErrors() ) {
    		return badValidation(result, response);
    	}
    	// ensure false
    	decliningAttendees.setAttending(false);
    	// if declining, make sure guest count is 0
    	decliningAttendees.setAdultGuests(0);
    	decliningAttendees.setChildGuests(0);
    	
		decliningAttendees.setUserId(user.getId());
		decliningAttendees.setEventId(eventid);
    	try {
    		EventAttendees eventAttendees = eventDao.addEventAttendee(eventid, user.getId(), decliningAttendees);
    		if(eventAttendees == null) {
    			return badEvent(response);
    		}
    		return new Response<>(eventAttendees);
    	} catch (DataIntegrityViolationException e) {
			return new Response<>(ValidationError.createList(e));
		}
    }
    
//    @DeleteMapping(path="/event/{eventid}/attendees/{userid}")
//    public void removeEventAttendee(HttpServletResponse response) {
//    	// successfully deleted a record
//    	response.setStatus(HttpServletResponse.SC_OK);
//    	// no record to delete
//    	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//    	// not authorized
//    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
    
    /**
     * Updates the specified event.
     * Roles: Host
     * 
     * @param eventid Event ID set by the path
     * @param event Event information to update
     * @param result Validation result from creating the Event object
	 * @param request Request made by the user
	 * @param response Response to send to the user
	 * @return 
	 * 	<ul>
	 * 		<li><h3>HTTP 200</h3> Object containing the old event and the new event</li>
	 * 		<li><h3>HTTP 400</h3> Event doesn't exist or user does not have access to event</li>
	 * 		<li><h3>HTTP 401</h3> User not logged in</li>
	 * </ul>
     */
    @PutMapping(path="/event/{eventid}")
    public Response<?> updateEvent(@PathVariable long eventid, 
    								@Valid @RequestBody Event event, 
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
    	// override Event object
    	event.setEventId(eventid);
    	event.setUserId(user.getId());
    	
    	Event oldEvent = eventDao.updateEvent(eventid, user.getId(), event);
    	
    	if( oldEvent == null ) {
    		return badEvent(response);
    	} else {
    		return new Response<>(
    			new ResponseMap().put("old", oldEvent).put("new", event).build()
    		);
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