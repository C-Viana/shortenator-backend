package com.cviana.app.shared.util;

import java.util.List;

public class DeviceTypeResolver {
	private static final List<String> MOBILE_KEYWORDS = List.of("mobile", "android", "iphone", "ipod", "blackberry", "windows phone");
	private static final List<String> TABLET_KEYWORDS = List.of("ipad", "tablet");
	
	public static String resolve(String userAgent) {
        if (userAgent == null) return "unknown";
        String ua = userAgent.toLowerCase();

        if (TABLET_KEYWORDS.stream().anyMatch(ua::contains)) return "tablet";
        if (MOBILE_KEYWORDS.stream().anyMatch(ua::contains)) return "mobile";
        return "desktop";
    }
    
}
