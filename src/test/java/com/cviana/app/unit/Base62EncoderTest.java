package com.cviana.app.unit;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cviana.app.shared.util.Base62Encoder;

class Base62EncoderTest {
	
	@Test
	@DisplayName("Deve codificar ID 1 corretamente")
	void shouldEncodeId1() {
		String result = Base62Encoder.encode(1L);
        assertThat(result).isEqualTo("b");
	}

	@Test
	@DisplayName("Deve gerar códigos diferentes para IDs diferentes")
	void shouldGenerateDistinctCodesForDistinctIds() {
		String code1 = Base62Encoder.encode(1L);
        String code2 = Base62Encoder.encode(2L);
        assertThat(code1).isNotEqualTo(code2);
	}

	@Test
	@DisplayName("Deve gerar código não vazio para qualquer ID positivo")
	void shouldGenerateNonEmptyCode() {
		assertThat(Base62Encoder.encode(9999L)).isNotEmpty();
        assertThat(Base62Encoder.encode(1000000L)).isNotEmpty();
	}

}
