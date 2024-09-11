package com.example.SB_apirest_mysql.integrationTest;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerWebTestClient {

	private ObjectMapper objectMapper;

	@Autowired
	private WebTestClient client;

	User user;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		user = UserBuilder.withAllDumy().setId(null).build();

	}

	@Test
	@Order(3)
	void testCreateUser() {

		client.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.user.id").isEqualTo(3).jsonPath("$.user.name").isEqualTo("Luis").jsonPath("$.user.email")
				.isEqualTo("email@email.com").jsonPath("$.user.phone").isEqualTo("1234567890");

	}

	@Test
	@Order(5)
	void testDeleteUser() {
		client.delete().uri("/api/users/2").exchange().expectStatus().isOk();
	}

	@Test
	@Order(1)
	void testGetAllUsers() {
		client.get().uri("/api/users").exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.users[0].name").isEqualTo("John Doe")
				.jsonPath("$.users[0].email").isEqualTo("john.doe@example.com").jsonPath("$.users[0].phone")
				.isEqualTo("123-456-7890").jsonPath("$.users[0].id").isEqualTo(1).jsonPath("$.users").isArray()
				.jsonPath("$.users").value(hasSize(2));
	}

	@Test
	@Order(2)
	void testGetUserById() {
		client.get().uri("/api/users/1").exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.user.name").isEqualTo("John Doe")
				.jsonPath("$.user.email").isEqualTo("john.doe@example.com").jsonPath("$.user.phone")
				.isEqualTo("123-456-7890").jsonPath("$.user.id").isEqualTo(1);
	}

	@Test
	@Order(4)
	void testUpdateUser() {

		client.put().uri("/api/users/2").contentType(MediaType.APPLICATION_JSON).bodyValue(user).exchange()
				.expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.user.id").isEqualTo(2).jsonPath("$.user.name").isEqualTo("Luis").jsonPath("$.user.email")
				.isEqualTo("email@email.com").jsonPath("$.user.phone").isEqualTo("1234567890");

	}

}