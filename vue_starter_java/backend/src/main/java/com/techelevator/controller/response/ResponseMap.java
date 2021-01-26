package com.techelevator.controller.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseMap {
	Map<String,Object> pairs = new LinkedHashMap<>();
	
	public ResponseMap put(String name, Object obj) {
		pairs.put(name, obj);
		return this;
	}
	public Map<String,Object> build() {
		return pairs;
	}
}
