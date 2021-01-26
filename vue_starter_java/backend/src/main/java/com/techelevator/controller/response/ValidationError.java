package com.techelevator.controller.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=ValidationErrorSerializer.class)
public class ValidationError extends ResponseError {
	private Object field; // can be String or String[]
	private Object value;
	
	public ValidationError(Object field, Object value, String error) {
		super(error);
		if( field instanceof String || field instanceof String[] ) {
			this.field = field;
		} else {
			this.field = null;
		}
		if( value instanceof String || value instanceof String[] ) {
			this.value = value;
		} else {
			this.value = null;
		}
	}
	
	public Object getField() {
		return field;
	}
	public Object getValue() {
		return value;
	}
	
	public static List<ValidationError> createList(BindingResult result) {
		List<ValidationError> validationErrors = new ArrayList<>();
        
        for( ObjectError objError : result.getAllErrors() ) {
        	FieldError error = (FieldError)objError;
        	String rejectedValue = error.getRejectedValue()==null?null:error.getRejectedValue().toString();
        	validationErrors.add(new ValidationError(error.getField(), rejectedValue, error.getDefaultMessage()));
        }
        
        return validationErrors;
	}
	
	public static List<ValidationError> createList(DataIntegrityViolationException e) {
		Object column;
		Object value;
		String message;
		String[] split = e.getRootCause().getMessage().split("Detail: Key \\(");
		if( split.length > 1 ) {
	        split = split[1].split("\\)=\\(");
	        if( !split[0].contains(",") ) { 
	        	column = split[0];
	        } else {
	        	column = split[0].split(", ");
	        }
	        
	        split = split[1].split("\\)");
	        if( !split[0].contains(",") ) {
	        	value = split[0];
	        } else {
	        	value = split[0].split(", ");
	        }
	        
	        if( split[1].contains("is not present") ) {
	        	if( column instanceof String[] ) {
	        		message = "Invalid group";
	        	} else {
	        		message = String.format("Invalid %s", column);
	        	}
	        } else {
	        	if( value instanceof String[] ) {
	        		message = "Group already exists";
	        	} else {
	        		message = String.format("%s already exists", value);
	        	}
	        }
		} else {
			// null value
			split = e.getRootCause().getMessage().split("\"");
			column = split[1];
			value=null;
			message = String.format("%s cannot be null", column);
		}
        
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError(column, value, message));
        
        return errors;
	}
}

class ValidationErrorSerializer extends JsonSerializer<ValidationError> {
	@Override
	public void serialize(ValidationError value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField("error", value.getError());
		
		if( value.getField() instanceof String[] && value.getValue() instanceof String[] &&
			((String[])value.getField()).length == ((String[])value.getValue()).length ) {
			
			String[] fields = (String[])value.getField();
			String[] values = (String[])value.getValue();
			int length = fields.length;
			Map<String,String> map = new LinkedHashMap<>();
			
			for( int i=0; i<length; i++ ) {
				map.put(fields[i], values[i]);
			}
			gen.writeObjectField("objects", map);
		} else {
			gen.writeObjectField("field", value.getField());
			gen.writeObjectField("value", value.getValue());
		}
		gen.writeEndObject();
	}
	
}