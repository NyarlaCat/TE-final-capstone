package com.techelevator.model.pojo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class Event{
	private long eventId;
	
	@NotNull(message="Event requires a name")
	@List({
		@Length(min=1, message="Event name needs to have at least one character"),
		@Length(max=255, message="Event name cannot be more than 255 characters")
	})
	private String name;
	
	@NotNull(message="Event needs a date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonSerialize(using=LocalDateSerializer.class)
	private LocalDate date;
	
	@NotNull(message="Event needs a time")
	@List({
		@Length(min=1, message="Event time needs to have at least one character"),
		@Length(max=10, message="Event time cannot be more than 10 characters")
	})
	private String time;
	
	@NotNull(message="Event needs a description")
	@List({
		@Length(min=1, message="Event description needs to have at least one character"),
		@Length(max=255, message="Event description cannot be more than 255 characters")
	})
	private String description;
	
	@NotNull(message="Event needs an RSVP date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonSerialize(using=LocalDateSerializer.class)
	private LocalDate deadline;
	
	@NotNull(message="Event needs to have an address")
	private Long addressId;
	
	private long userId;
	private Boolean hosting=true;
	private Boolean attending=true;
	private boolean isInvited=false;
	private Address address;
	
	@JsonIgnore
	@AssertTrue(message="RSVP needs to be before the event date")
	public boolean isRsvpBeforeDate() {
		if( deadline == null ) return false;
		return deadline.compareTo(date) < 0;
	}

	@JsonIgnore
	@AssertTrue(message="Event's RSVP needs to be a future date") // @Future doesn't support LocalDate
	public boolean isRsvpInFuture() {
		if( deadline == null ) return false;
		return deadline.isAfter(LocalDate.now());
	}

	@JsonIgnore
	@AssertTrue(message="Event needs to take place on a future date") // @Future doesn't support LocalDate
	public boolean isDateInFuture() {
		if( date == null ) return false;
		return date.isAfter(LocalDate.now());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getDeadline() {
		return deadline;
	}
	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	public Boolean isAttending() {
		return attending;
	}
	public void setAttending(Boolean attending) {
		this.attending = attending;
	}
	public Boolean isHosting() {
		return hosting;
	}
	public void setHosting(Boolean hosting) {
		this.hosting = hosting;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public boolean isInvited() {
		return this.isInvited;
	}
	public void setInvited(boolean isInvited) {
		this.isInvited = isInvited;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
}

class LocalDateSerializer extends JsonSerializer<LocalDate> {
	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();
		
		String month = value.getMonth().toString().substring(0,1).toUpperCase()
				     + value.getMonth().toString().substring(1).toLowerCase();
		gen.writeObjectField("month", month);
		gen.writeObjectField("day", value.getDayOfMonth());
		gen.writeObjectField("year", value.getYear());
		String dayOfWeek = value.getDayOfWeek().toString().substring(0,1).toUpperCase()
						 + value.getDayOfWeek().toString().substring(1).toLowerCase();
		gen.writeObjectField("dayOfWeek", dayOfWeek);
		gen.writeObjectField("daysAway", ChronoUnit.DAYS.between(LocalDate.now(), value));
		
		gen.writeEndObject();
	}
}
