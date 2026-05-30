package com.cviana.app.shared.files;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.url.Url;

public class ExcelFile implements FileCreator {
	
	@Override
	public Resource create(List<Url> content, String baseUrl) {
		String randomSuffix = UUID.randomUUID().toString().replace("-", "");
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+"url_list_"+randomSuffix+".xlsx";
		
		File file = new File(filePath);
		FileOutputStream fos = null;
		
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("URLs");
			
			addHeader(sheet, header, getHeaderStyle(workbook));
			addContent(sheet, content, baseUrl);
			
			fos = new FileOutputStream(filePath);
			workbook.write(fos);
			fos.close();
			
			return new FileSystemResource(file);
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage());
		}
		
	}
	
	private CellStyle getHeaderStyle(Workbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Liberation Sans");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		headerStyle.setFont(font);
		
		return headerStyle;
	}
	
	private void addHeader(Sheet sheet, String[] headers, CellStyle cellStyle) {
		Row row = sheet.createRow(0);
		
		for (int i = 0; i < headers.length; i++) {
			row.createCell(i).setCellStyle(cellStyle);
			row.getCell(i).setCellValue(headers[i]);
			sheet.autoSizeColumn(i);
		}
	}
	
	private void addContent(Sheet sheet, List<Url> content, String baseUrl) {
		int rowIndex = 1;
		String shortenedUrl = "";
		String dateValue = "";
		
		for (Url url : content) {
			Row row = sheet.createRow(rowIndex);
    		shortenedUrl = baseUrl + "/r/" + url.getShortenedUrlCode();
			dateValue = (url.getExpiresAt() == null) ? "N/A" : FORMATTER.format(url.getExpiresAt());
			
			row.createCell(0).setCellValue(url.getName());
			row.createCell(1).setCellValue(url.getSourceDomain());
			row.createCell(2).setCellValue(shortenedUrl);
			row.createCell(3).setCellValue(url.getSourceUrl());
			row.createCell(4).setCellValue(dateValue);
			
			rowIndex++;
		}
	}
	
}
