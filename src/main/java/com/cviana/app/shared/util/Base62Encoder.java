package com.cviana.app.shared.util;

public class Base62Encoder {
	
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int BASE = ALPHABET.length();
	
	public static String encode(long number) {
		StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(ALPHABET.charAt((int)(number % BASE)));
            number /= BASE;
        }
        return result.reverse().toString();
	}
}
