package com.cviana.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.cviana.app.auth.TokenService;
import com.cviana.app.shared.exception.errors.NotFoundException;
import com.cviana.app.url.dto.UrlRequestDto;
import com.cviana.app.user.User;
import com.cviana.app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UrlControllerTest {
	
	@Container
	@SuppressWarnings("resource")
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18")
        .withDatabaseName("shortenator_test")
        .withUsername("test")
        .withPassword("test");
	
    @SuppressWarnings({ "resource", "rawtypes" })
	@Container
    static GenericContainer<?> redis = new GenericContainer("redis:8")
        .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
    
    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired TokenService tokenService;
    
    static ObjectMapper objectMapper;
    
    private String token;
    
    @BeforeAll
    static void start() {
    	objectMapper = new ObjectMapper();
    	objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
    
    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("hashedpassword");
        
        User savedUser = userRepository.save(user);
        token = "Bearer " + tokenService.generateToken(savedUser.getEmail()).accessToken();
    }
    
    @Test
    @DisplayName("POST /api/v1/urls/shorten deve retornar 201 com URL encurtada")
    void shouldShortenUrl() throws Exception {
        UrlRequestDto request = new UrlRequestDto("Meu GitHub", "https://github.com", null);
        
        mockMvc.perform(post("/api/v1/urls/shorten")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.shortened_url").isNotEmpty())
            .andExpect(jsonPath("$.source_domain").value("github.com"));
    }
    
    @Test
    @DisplayName("POST /api/v1/urls/shorten sem autenticação deve retornar 403")
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        UrlRequestDto request = new UrlRequestDto("Meu GitHub", "https://github.com", null);

        mockMvc.perform(post("/api/v1/urls/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("GET /r/{code} com código inválido deve retornar 404")
    void shouldReturn404ForInvalidCode() throws Exception {
        MvcResult result = mockMvc.perform(get("/r/codigoinexistente")).andReturn();
        Assertions.assertEquals(NotFoundException.class, result.getResolvedException().getClass());
        //Assertions.assertThrows(NotFoundException.class, () -> mockMvc.perform(get("/r/codigoinexistente")) );
    }
}
