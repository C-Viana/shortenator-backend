package com.cviana.app.shared.files;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.io.Resource;

import com.cviana.app.url.Url;

public interface FileCreator {
	
	static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm").withZone(ZoneId.of("GMT-3"));
    String[] header = {"NAME", "DOMAIN", "SHORT LINK", "SOURCE LINK", "EXPIRATION"};
    
	public Resource create(List<Url> content, String baseUrl);
}
