package com.cviana.app.shared.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.url.Url;

public class TextFile implements FileCreator {

	@Override
	public Resource create(List<Url> content, String baseUrl) {
		String randomSuffix = UUID.randomUUID().toString().replace("-", "");
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+"url_list"+randomSuffix+".txt";
		String shortenedUrl = "";
		String dateValue = "";

	    int columns = header.length;
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
			for (int i = 0; i < columns; i++) {
				writer.write(header[i]);
				if( i == columns-1 ) break;
				writer.write("\t");
			}
			
			writer.newLine();
			
			for (Url url : content) {
				shortenedUrl = baseUrl + "/r/" + url.getShortenedUrlCode();
            	dateValue = (url.getExpiresAt() == null) ? "N/A" : FORMATTER.format(url.getExpiresAt());
				
			    writer.write(url.getName() + "\t");
			    writer.write(url.getSourceDomain() + "\t");
			    writer.write(shortenedUrl + "\t");
			    writer.write(url.getSourceUrl() + "\t");
			    writer.write(dateValue);
			    writer.newLine();
			}
			
			return new FileSystemResource(filePath);
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new ServerErrorException();
		}
	}
	
}
