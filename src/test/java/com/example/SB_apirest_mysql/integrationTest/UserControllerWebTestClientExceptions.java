package com.example.SB_apirest_mysql.integrationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.service.UserService;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerWebTestClientExceptions {

	private ObjectMapper objectMapper;

	@Autowired
	private WebTestClient client;

	@MockBean
	private UserService userService;

	User user;

	User invalidUser;

	User invalidUserUpdate;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		user = UserBuilder.withAllDumy().setId(null).build();

		invalidUser = new User();

		invalidUserUpdate = UserBuilder.withAllDumy().setId(1L).setEmail(null).setName(null).setPassword(null)
				.setPhone(null).build2();

	}

	@Test
	// @Order(3)
	void testCReateUser_whenBadRequest() {
		when(userService.save(any(User.class))).thenThrow(new ConstraintViolationException("There was a error", null));

		client.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST).expectBody().isEmpty();
	}

	@Test
	// @Order(3)
	void testCReateUser_whenInternalServerError() {
		when(userService.save(any(User.class))).thenThrow(new RuntimeException("Service exception"));

		client.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().isEmpty();
	}

	@Test
	void testCreateUser_whenValidationErrors() throws Exception {

		String invalidUserJson = objectMapper.writeValueAsString(invalidUser);

		client.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).bodyValue(invalidUserJson).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("$.message").isEqualTo("Validation failed")
				.jsonPath("$.name").isEqualTo("Name cannot be empty").jsonPath("$.email")
				.isEqualTo("Email cannot be empty").jsonPath("$.password").isEqualTo("Password cannot be empty");

	}

	@Test
	void testDeleteUser_whenInternalServerError() {
		when(userService.findById(1L)).thenReturn(Optional.of(new User()));
		doThrow(new RuntimeException("Internal server error")).when(userService).deleteById(1L);

		client.delete().uri("/api/users/1").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody().isEmpty();

	}

	@Test
	void testDeleteUser_whenUserNotFound() {
		when(userService.findById(1L)).thenReturn(Optional.empty());

		client.delete().uri("/api/users/1").exchange().expectStatus().isNotFound().expectBody().isEmpty();
	}

	// test faild get all users
	@Test
	// @Order(6)
	void testGetAllUsers_whenExceptionThrown() {
		when(userService.findAll()).thenThrow(new RuntimeException("Service exception"));

		client.get().uri("/api/users").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody().isEmpty();
	}

	// test faild gteuser by id
	@Test
	// @Order(3)
	void testGetUserById_whenExceptionThrown() {
		when(userService.findById(1L)).thenThrow(new RuntimeException("Service exception"));

		client.get().uri("/api/users/1").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody().isEmpty();
	}

	// test fail when user not found
	@Test
	// @Order(2)
	void testGetUserById_whenUserNotFound() {
		when(userService.findById(1L)).thenReturn(Optional.empty());

		client.get().uri("/api/users/1").exchange().expectStatus().isNotFound().expectBody().isEmpty();
	}

	@Test
	// @Order(3)
	void testUpdateUser_whenBadRequest() {
		when(userService.findById(1L)).thenReturn(Optional.of(user));
		when(userService.update(any(User.class)))
				.thenThrow(new ConstraintViolationException("There was a error", null));

		client.put().uri("/api/users/1").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST).expectBody().isEmpty();
	}

	@Test
	// @Order(3)
	void testUpdateUser_whenInternalServerError() {
		when(userService.findById(1L)).thenReturn(Optional.empty());
		when(userService.update(any(User.class))).thenThrow(new RuntimeException("There was a error."));

		client.put().uri("/api/users/1").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isEqualTo(HttpStatus.NOT_FOUND).expectBody().isEmpty();
	}

	@Test
	// @Order(3)
	void testUpdateUser_whenNotFound() {
		when(userService.findById(1L)).thenReturn(Optional.of(user));
		when(userService.update(any(User.class))).thenThrow(new RuntimeException("Service exception"));

		client.put().uri("/api/users/1").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR).expectBody().isEmpty();
	}

	@Test
	void testUpdateUser_whenValidationErrors() throws Exception {

		when(userService.findById(anyLong())).thenReturn(Optional.of(new User()));

		String invalidUserJson = objectMapper.writeValueAsString(invalidUser);

		client.put().uri("/api/users/1").contentType(MediaType.APPLICATION_JSON).bodyValue(invalidUserJson).exchange()
				.expectStatus().isBadRequest().expectBody().jsonPath("$.message").isEqualTo("Validation failed")
				.jsonPath("$.name").isEqualTo("Name cannot be empty").jsonPath("$.email")
				.isEqualTo("Email cannot be empty").jsonPath("$.password").isEqualTo("Password cannot be empty");

	}

}
