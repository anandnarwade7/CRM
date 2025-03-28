package com.crm.mailservice;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private static Session session = null;

	public MailService() {
	}

	private static Session getMailSession() {
		Properties properties = System.getProperties();
		System.out.println("Properties :" + properties);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("dhanashrighule7@gmail.com", "nfxw mhud pndk vnve");
			}
		});
		session.setDebug(true);
		return session;
	}

	public void sendEmail(String email, String subject, String text) {
		try {
			Session mailSession = getMailSession();
			MimeMessage message = new MimeMessage(mailSession);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

			message.setSubject(subject);
			message.setContent(text, "text/html");

			Transport.send(message);
			System.out.println("Email sent successfully to " + email);
		} catch (Exception e) {
			System.err.println("Error sending email: " + e.getMessage());
		}
	}

}
