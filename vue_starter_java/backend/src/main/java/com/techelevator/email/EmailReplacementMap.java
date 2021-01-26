package com.techelevator.email;

import java.util.HashMap;
import java.util.Map;

public class EmailReplacementMap {
	private Map<String,String> map = new HashMap<>();
	public EmailReplacementMap put(String replaceThis, String withThis) {
		this.map.put(replaceThis, withThis);
		return this;
	}
	public Map<String,String> build() {
		return this.map;
	}
}
