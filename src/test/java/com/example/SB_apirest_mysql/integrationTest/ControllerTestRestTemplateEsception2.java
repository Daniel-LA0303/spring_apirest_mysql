package com.example.SB_apirest_mysql.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.service.UserService;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTestRestTemplateEsception2 {

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

	User userB2;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		userB = UserBuilder.withAllDumy().setId(1L).build();

		userB2 = UserBuilder.withAllDumy().setId(null).build2();

		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		invalidUser = UserBuilder.withAllDumy().setId(null).setEmail(null).setPassword(null).setPhone(null)
				.setName(null).build();
	}

	@Test
	void testCreateUser_BadRequest() {

		when(userService.save(any(User.class))).thenThrow(new ConstraintViolationException("There was a error", null));

		ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/users", userB2, Map.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}

	@Test
	void testCreateUser_InternalServerError() {
		when(userService.save(any(User.class))).thenThrow(new RuntimeException("Service exception"));

		ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/users", userB2, Map.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testDeleteOneUser_InternalServerError() {

		when(userService.findById(1L)).thenReturn(Optional.of(new User()));
		doThrow(new RuntimeException("Internal server error")).when(userService).deleteById(1L);

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/1", HttpMethod.DELETE, null, Map.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

	}

	@Test
	void testGetAllUsers_InternalServerError() {
		when(userService.findAll()).thenThrow(new RuntimeException("Service exception"));

		ResponseEntity<Map> response = testRestTemplate.getForEntity("/api/users", Map.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testGetOneUser_InternalServerError() {
		when(userService.findById(1L)).thenThrow(new RuntimeException("Service exception"));

		ResponseEntity<Map> response = testRestTemplate.getForEntity("/api/users/1", Map.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testUpdateUser_BadRequest() {
		when(userService.findById(1L)).thenReturn(Optional.of(userB2));
		when(userService.update(any(User.class)))
				.thenThrow(new ConstraintViolationException("There was a error", null));

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/1", HttpMethod.PUT,
				new HttpEntity<>(userB2, headers), Map.class);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}

	@Test
	void testUpdateUser_InternalServerError() {
		when(userService.findById(1L)).thenReturn(Optional.of(userB2));
		when(userService.update(any(User.class))).thenThrow(new RuntimeException("Service exception"));

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/1", HttpMethod.PUT,
				new HttpEntity<>(userB2, headers), Map.class);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

}