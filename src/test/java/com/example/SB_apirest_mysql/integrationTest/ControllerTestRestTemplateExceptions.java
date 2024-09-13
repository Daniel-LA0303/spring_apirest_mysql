package com.example.SB_apirest_mysql.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTestRestTemplateExceptions {

	/**
	 * Tambien se puede usar el entorno real utilizando el url
	 * localhost:8080/api/users
	 */

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@LocalServerPort
	private int port;

	HttpHeaders headers;

	User invalidUser;

	User userB;

	@BeforeEach
	void setUp() {
		userB = UserBuilder.withAllDumy().setId(1L).build();

		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		invalidUser = UserBuilder.withAllDumy().setId(null).setEmail(null).setPassword(null).setPhone(null)
				.setName(null).build();
	}

	@Test
	@Order(4)
	void testCreateUser_ValidationErrors() {

		ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/users", invalidUser, Map.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("Validation failed", body.get("message"));

		assertTrue(body.containsKey("name"));
		assertTrue(body.containsKey("email"));
		assertTrue(body.containsKey("password"));

		assertEquals("Name cannot be empty", body.get("name"));
		assertEquals("Email cannot be empty", body.get("email"));
		assertEquals("Password cannot be empty", body.get("password"));
		assertEquals("Validation failed", body.get("message"));
	}

	@Test
	@Order(1)
	void testDeleteUser_NotFound() {
		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/999", HttpMethod.DELETE, null, Map.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testGetUserById_NotFound() {
		User nonExistentUser = new User();
		nonExistentUser.setId(999L);

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/999", HttpMethod.GET,
				new HttpEntity<>(nonExistentUser, headers), Map.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	@Order(2)
	void testUpdateUser_NotFound() {

		User nonExistentUser = new User();
		nonExistentUser.setId(999L);

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/999", HttpMethod.PUT,
				new HttpEntity<>(nonExistentUser, headers), Map.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	@Order(3)
	void testUpdateUser_ValidationErrors() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<User> requestEntity = new HttpEntity<>(invalidUser, headers);

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/2", HttpMethod.PUT, requestEntity,
				Map.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("Validation failed", body.get("message"));

		assertTrue(body.containsKey("name"));
		assertTrue(body.containsKey("email"));
		assertTrue(body.containsKey("password"));

		assertEquals("Name cannot be empty", body.get("name"));
		assertEquals("Email cannot be empty", body.get("email"));
		assertEquals("Password cannot be empty", body.get("password"));
		assertEquals("Validation failed", body.get("message"));
	}

}
