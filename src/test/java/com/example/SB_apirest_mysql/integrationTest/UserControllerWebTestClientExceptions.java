package com.example.SB_apirest_mysql.integrationTest;

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
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.service.UserService;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerWebTestClientExceptions {

	private ObjectMapper objectMapper;

	@Autowired
	private WebTestClient client;

	@MockBean
	private UserService userService;

	User user;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		user = UserBuilder.withAllDumy().setId(null).build();

	}

	@Test
	// @Order(6)
	void testGetAllUsers_whenExceptionThrown() {
		when(userService.findAll()).thenThrow(new RuntimeException("Service exception"));

		client.get().uri("/api/users").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody().isEmpty();
	}

	@Test
	// @Order(3)
	void testGetUserById_whenExceptionThrown() {
		when(userService.findById(1L)).thenThrow(new RuntimeException("Service exception"));

		client.get().uri("/api/users/1").exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody().isEmpty();
	}

	@Test
	// @Order(2)
	void testGetUserById_whenUserNotFound() {
		when(userService.findById(1L)).thenReturn(Optional.empty());

		client.get().uri("/api/users/1").exchange().expectStatus().isNotFound().expectBody().isEmpty();
	}

}
