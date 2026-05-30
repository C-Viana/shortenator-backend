package com.cviana.app.shared.constants;

import java.util.HashMap;
import java.util.Map;

public class FileFormat {
	public static final String CSV = "text/csv";
	public static final String CSV_UTF8 = "text/csv; charset=utf-8";
	public static final String PDF = "application/pdf";
	public static final String TXT = "text/html";
	public static final String XLS = "application/vnd.ms-excel";
	public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	public static Map<String, String> getAll() {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("CSV", CSV);
		hashMap.put("CSV_UTF8", CSV_UTF8);
		hashMap.put("PDF", PDF);
		hashMap.put("TXT", TXT);
		hashMap.put("XLSX", XLSX);
		return hashMap;
	}
}
