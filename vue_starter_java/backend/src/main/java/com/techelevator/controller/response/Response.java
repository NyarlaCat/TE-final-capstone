package com.techelevator.controller.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=ResponseSerializer.class)
public class Response<O> {
	public static final Response<?> EMPTY_SUCCESS = new Response<>();
	
	private String status;
	private O object;
	private String objectName = null;
	private List<ResponseError> errors = null;
	
	private final String FAILED = "failed";
	private final String SUCCESS = "success";
	
	public Response() {
		this.status = SUCCESS;
	}
	public Response(List<? extends ResponseError> errors) {
		this.status = FAILED;
		this.errors = new ArrayList<ResponseError>();
		for( Object error : errors ) {
			this.errors.add((ResponseError)error);
		}
	}
	public <E extends ResponseError> Response(E error) {
		this.status = FAILED;
		this.errors = new ArrayList<>();
		errors.add(error);
	}
	public Response(O object) {
		this.status = SUCCESS;
		this.object = object;
	}
	public Response(String name, O object) {
		this.status = SUCCESS;
		this.object = object;
		this.objectName = name;
	}
	
	public String getStatus() { return status; }
	public List<? extends ResponseError> getErrors() { return errors; }
	public O getObject() { return object; }
	public String getObjectName() { return objectName; }
}

class ResponseSerializer extends JsonSerializer<Response<?>> {
	@Override
	public void serialize(Response<?> value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException {
		jgen.writeStartObject();
		jgen.writeStringField("status", value.getStatus());
		if(value.getObject() != null ) {
			String name = "object";
			if( value.getObjectName() != null ) {
				name = value.getObjectName();
			}
			jgen.writeObjectField(name, value.getObject());
		}
		if(value.getErrors() != null ) {
			jgen.writeObjectField("errors", value.getErrors());
		}
		jgen.writeEndObject();
	}
}