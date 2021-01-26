package com.techelevator.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMail {
	private String host;
	private String port;
	private String fromAddress;
	private String password;
	private Session session;
	private String emailTemplateDirectory;
	
	@Autowired
	public SendMail(EmailConfig config) {
		this.host = config.getHost();
		this.port = config.getPort();
		this.fromAddress = config.getFromAddress();
		this.password = config.getPassword();
		this.emailTemplateDirectory = config.getEmailTemplateDirectory();
		if( !this.emailTemplateDirectory.endsWith("/") ) {
			this.emailTemplateDirectory += "/";
		}
		
		setup();
	}
	
	public File getEmailFile(String name) {
		File file = new File(SendMail.class.getResource(emailTemplateDirectory+name).getFile());
		if( !file.exists() ) {
			return null;
		}
		return file;
	}
	
	public boolean send(String toAddress, String subject, File template, Map<String,String> replacements) {
		// make sure template exists
		if( template == null || !template.exists() ) {
			return false;
		}
		// read entire contents of file
		try(Scanner scanner = new Scanner(template)) {
			StringBuilder messageBuilder = new StringBuilder();
			while( scanner.hasNext() ) {
				messageBuilder.append(scanner.nextLine());
				messageBuilder.append("\n");
			}
			String message = messageBuilder.toString();
			// parse replacements
			for( Map.Entry<String,String> entry : replacements.entrySet() ) {
				message = message.replace(entry.getKey(), entry.getValue());
			}
			return send(toAddress, subject, message);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	public boolean send(String toAddress, String subject, String message) {
		try {
			MimeMessage email = new MimeMessage(session);
		
			email.setFrom(new InternetAddress(this.fromAddress));
			email.addRecipients(Message.RecipientType.TO, toAddress);
			email.setSubject(subject);
			
			email.setContent(message, "text/html");
			
			Transport transport = session.getTransport("smtp");
			transport.connect(this.host, this.fromAddress, this.password);
			transport.sendMessage(email, email.getAllRecipients());
			transport.close();
			return true;
		} catch(MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return this.host;
	}
	public String getFromAddress() {
		return this.fromAddress;
	}
	
	private void setup() {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.required", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.ssl.enable", "true");
		properties.setProperty("mail.smtp.host", this.host);
		properties.setProperty("mail.user", this.fromAddress);
		properties.setProperty("mail.password", this.password);
		properties.setProperty("mail.smtp.port", this.port);
		
		this.session = Session.getDefaultInstance(properties);
	}
}
