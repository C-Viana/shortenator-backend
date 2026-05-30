package com.cviana.app.url.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class GmailSender {
	
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private	String serverSender;
	
	public GmailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendMessageWithAttachment(String userMail, String[] to, String title, String content, String filePath) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setFrom(serverSender, userMail + " via Shortenator");
	    helper.setTo(to);
	    helper.setReplyTo(userMail);
	    helper.setSubject(title);
	    helper.setText(content);
	    
	    FileSystemResource file = new FileSystemResource(new File(filePath));
	    helper.addAttachment(Path.of(filePath).getFileName().toString(), file);
		
	    javaMailSender.send(message);
	}

}
