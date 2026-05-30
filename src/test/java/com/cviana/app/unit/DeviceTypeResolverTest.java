package com.cviana.app.unit;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.cviana.app.shared.util.DeviceTypeResolver;

class DeviceTypeResolverTest {
	
	@ParameterizedTest(name = "User-Agent {0} deve retornar {1}")
    @CsvSource({
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit, desktop",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 14), mobile",
        "Mozilla/5.0 (Linux; Android 10; Mobile), mobile",
        "Mozilla/5.0 (iPad; CPU OS 14), tablet",
        ", unknown"  // null → unknown
    })
	void shouldResolveDeviceType(String userAgent, String expected) {
        assertThat(DeviceTypeResolver.resolve(userAgent)).isEqualTo(expected);
	}

}
