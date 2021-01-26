package com.techelevator.model.pojo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class Invitee {

	private long inviteId;
	
	@NotBlank(message="Email must be provided")
	@Email(message="Email must be valid")
	private String email;
	
	private long eventId;
	
	@NotNull(message="Role must be assigned")
	private String role;
	
	public long getInviteId() {
		return inviteId;
	}
	public void setInviteId(long inviteId) {
		this.inviteId = inviteId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
