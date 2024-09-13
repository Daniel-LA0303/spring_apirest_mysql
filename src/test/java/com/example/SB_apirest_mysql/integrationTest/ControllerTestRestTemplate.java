package com.example.SB_apirest_mysql.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
class ControllerTestRestTemplate {

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

	User userB;

	User userUpdate;

	@BeforeEach
	void setUp() {
		userB = UserBuilder.withAllDumy().setId(null).build();

		userUpdate = UserBuilder.withAllDumy().setId(null).setEmail("email2@email.com").setName("Daniel")
				.setPassword("1234").setPhone("0099887766").build();
	}

	@Test
	@Order(5)
	void testDeleteUser() {

		ResponseEntity<Void> response = testRestTemplate.exchange("/api/users/3", HttpMethod.DELETE, null, Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(1)
	void testGetAllUsers() {
		ResponseEntity<Map> response = testRestTemplate.getForEntity("/api/users", Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Map<String, List<User>> body = response.getBody();
		assertNotNull(body);
		assertTrue(body.containsKey("users"));
		List<User> users = body.get("users");
		assertNotNull(users);

		assertFalse(users.isEmpty());
	}

	@Test
	@Order(2)
	void testGetOneUserById() {

		ResponseEntity<Map> response = testRestTemplate.getForEntity("/api/users/1", Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);

		assertTrue(body.containsKey("user"));

		User user = objectMapper.convertValue(body.get("user"), User.class);

		assertNotNull(user);

		assertEquals("John Doe", user.getName());
		assertEquals("john.doe@example.com", user.getEmail());
		assertEquals("123-456-7890", user.getPhone());
		assertEquals(1L, user.getId());
	}

	@Test
	@Order(3)
	void testNewUser() {
		ResponseEntity<Map> response = testRestTemplate.postForEntity("/api/users", userB, Map.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);

		assertTrue(body.containsKey("user"));

		User createdUser = objectMapper.convertValue(body.get("user"), User.class);

		assertNotNull(createdUser);
		assertEquals("Luis", createdUser.getName());
		assertEquals("email@email.com", createdUser.getEmail());
		assertEquals("1234567890", createdUser.getPhone());

	}

	@Test
	@Order(4)
	void testUpdateUser() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<User> requestEntity = new HttpEntity<>(userUpdate, headers);

		ResponseEntity<Map> response = testRestTemplate.exchange("/api/users/2", HttpMethod.PUT, requestEntity,
				Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertTrue(body.containsKey("user"));

		User userUpdated = objectMapper.convertValue(body.get("user"), User.class);

		assertNotNull(userUpdated);
		assertEquals("Daniel", userUpdated.getName());
		assertEquals("email2@email.com", userUpdated.getEmail());
		assertEquals("0099887766", userUpdated.getPhone());
	}

}