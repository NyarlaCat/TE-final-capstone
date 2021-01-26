package com.techelevator.email;

public class EmailConfig {
	private String fromAddress;
	private String host;
	private String port;
	private String password;
	private String emailTemplateDirectory;
	
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getEmailTemplateDirectory() {
		return emailTemplateDirectory;
	}
	public void setEmailTemplateDirectory(String emailTemplateDirectory) {
		this.emailTemplateDirectory = emailTemplateDirectory;
	}
}
