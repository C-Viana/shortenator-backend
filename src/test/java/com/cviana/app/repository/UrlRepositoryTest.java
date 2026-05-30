package com.cviana.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.cviana.app.url.Url;
import com.cviana.app.url.UrlRepository;
import com.cviana.app.user.User;
import com.cviana.app.user.UserRepository;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UrlRepositoryTest {

	@SuppressWarnings("resource")
	@Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18")
        .withDatabaseName("shortenator_test")
        .withUsername("test")
        .withPassword("test");

    @Autowired UrlRepository urlRepository;
    @Autowired UserRepository userRepository;

    private User testUser;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        urlRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("hashedpassword");
        testUser = userRepository.save( user );
    }
    
    @AfterAll
    static void closure() {
    	postgres.stop();
    	postgres.close();
    }

    @Test
    @DisplayName("Deve encontrar URL pelo código encurtado")
    void shouldFindByShortenedUrlCode() {
        Url url = buildUrl("abc123", testUser);
        urlRepository.save(url);

        Optional<Url> result = urlRepository.findByShortenedUrlCode("abc123");

        assertThat(result).isPresent();
        assertThat(result.get().getSourceUrl()).isEqualTo("https://github.com");
    }

    @Test
    @DisplayName("Deve retornar página de URLs do usuário")
    void shouldFindPageByUser() {
        urlRepository.save(buildUrl("code1", testUser));
        urlRepository.save(buildUrl("code2", testUser));

        Page<Url> result = urlRepository.findByUser(testUser, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve filtrar URLs pelo domínio")
    void shouldFindByUserAndDomain() {
        urlRepository.save(buildUrl("code1", testUser, "github.com"));
        urlRepository.save(buildUrl("code2", testUser, "google.com"));

        Page<Url> result = urlRepository.findByUserAndSourceDomain(
            testUser, "github.com", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    private Url buildUrl(String code, User user) {
        return buildUrl(code, user, "github.com");
    }

    private Url buildUrl(String code, User user, String domain) {
        return new Url("Test URL", domain, "https://" + domain, code, Instant.now(), null, user);
    }

}
