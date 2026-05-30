package com.cviana.app.shared.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.url.Url;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfFile implements FileCreator {
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Resource create(List<Url> content, String baseUrl) {
		String randomSuffix = UUID.randomUUID().toString().replace("-", "");
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+"url_list_"+randomSuffix+".pdf";
		
		File file = null;
		FileOutputStream os = null;
		Document document = new Document();
		log.info( "CREATING PDF FILE" );
		
		try {
			file = new File(filePath);
			os = new FileOutputStream(file);
			PdfWriter.getInstance(document, os);
			
			document.open();
			PdfPTable table = new PdfPTable(header.length);
			addTableHeader(table);
			addRows(table, content, baseUrl + "/r/");
			document.add(table);
			document.close();
			
			log.info( "FILE EXISTIS IN " + file.getAbsolutePath() );
			
			return new FileSystemResource(file);
		}
		catch (Exception e) {
			throw new ServerErrorException(e.getMessage());
		}
		finally {
			if(document.isOpen()) document.close();
			try {
				os.close();
			} catch (IOException e) {
				throw new ServerErrorException(e.getMessage());
			}
		}
	}
	
	private void addTableHeader(PdfPTable table) {
	    Stream.of(header)
	      .forEach(columnTitle -> {
	        PdfPCell header = new PdfPCell();
	        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        header.setBorderWidth(2);
	        header.setPhrase(new Phrase(columnTitle));
	        table.addCell(header);
	    });
	}
	
	private void addRows(PdfPTable table, List<Url> content, String baseUrl) {
		String dateValue = "";
		log.info( "ADDING ROLES. "+content.size()+" IN TOTAL" );
		
		for (Url url : content) {
        	dateValue = (url.getExpiresAt() == null) ? "N/A" : FORMATTER.format(url.getExpiresAt());
        	
			table.addCell(url.getName());
			table.addCell(url.getSourceDomain());
			table.addCell(baseUrl + url.getShortenedUrlCode());
			table.addCell(url.getSourceUrl());
			table.addCell(dateValue);
		}
	}
	
	

}
