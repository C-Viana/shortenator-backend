package com.cviana.app.shared.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.cviana.app.url.Url;
import com.opencsv.CSVWriter;

@Component
public class CsvFile implements FileCreator {
	
	@Override
	public Resource create(List<Url> content, String baseUrl) {
		String randomSuffix = UUID.randomUUID().toString().replace("-", "");
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+"url_list"+randomSuffix+".csv";
		String shortenedUrl = "";
		String dateValue = "";

	    try (final CSVWriter writer = new CSVWriter(new FileWriter(filePath)) ) {
	    	writer.writeNext(header);
	    	
	    	for (Url url : content) {
	    		shortenedUrl = baseUrl + "/r/" + url.getShortenedUrlCode();
            	dateValue = (url.getExpiresAt() == null) ? "N/A" : FORMATTER.format(url.getExpiresAt());
            	
            	writer.writeNext(
            		new String[] {
            			url.getName(), url.getSourceDomain(), shortenedUrl, url.getSourceUrl(), dateValue
            		});
			}
	    } catch (IOException e) {
	    	throw new RuntimeException("Failed to generate CSV file", e);
		}
	    
		return new FileSystemResource(filePath);
	}

}
