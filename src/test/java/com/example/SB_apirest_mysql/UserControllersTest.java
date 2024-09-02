package com.example.SB_apirest_mysql;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.example.SB_apirest_mysql.controllers.UserController;
import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.service.UserService;
import com.example.SB_apirest_mysql.utils.UserBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;

@WebMvcTest(UserController.class)
public class UserControllersTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	/**
	 * user
	 */
	private User user;

	private List<User> users = new ArrayList<>();

	ObjectMapper objectMapper;

	/**
	 * user de entrada para nuevo
	 */
	private User user4;

	/**
	 * user updated
	 */
	private User updatedUser;

	/**
	 * user empty
	 */
	User invalidUser = new User();

	/**
	 * Prepara todo lo que se trabajara
	 */
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();

		user = UserBuilder.withAllDumy().build();

		User user2 = new UserBuilder().setId(2L).setName("Carlos").setEmail("carlos@example.com")
				.setPassword("password123").setPhone("0987654321").build();

		User user3 = new UserBuilder().setId(3L).setName("GATSBY").setEmail("gatsby@example.com")
				.setPassword("password123").setPhone("0987654321").build2();

		user4 = new UserBuilder().setName("GATSBY").setEmail("gatsby@example.com").setPassword("1234")
				.setPhone("1234567890").build3();

		updatedUser = new UserBuilder().setEmail("updated.email@email.com").setId(1L).setName("Luis Updated")
				.setPassword("54321").setPhone("0987654321").build();

		users.add(user);
		users.add(user2);
		users.add(user3);

		BindingResult bindingResult = new BeanPropertyBindingResult(invalidUser, "user");
		bindingResult.rejectValue("name", "errorCode", "Name cannot be empty");
		bindingResult.rejectValue("email", "errorCode", "Email cannot be empty");
		bindingResult.rejectValue("password", "errorCode", "Password cannot be empty");
	}

	/**
	 * Prueba cuando hay un Internal Server Error en DeleteUser
	 * 
	 * @throws Exception
	 */
	@Test
	void testDeleteUserInternalServerError() throws Exception {
		when(userService.findById(1L)).thenReturn(Optional.of(new User()));
		doThrow(new RuntimeException("Internal server error")).when(userService).deleteById(1L);

		mvc.perform(delete("/api/users/1")).andExpect(status().isInternalServerError());

		verify(userService).findById(1L);
		verify(userService).deleteById(1L);
	}

	/**
	 * Prueba cuando no encuentra un User
	 * 
	 * @throws Exception
	 */
	@Test
	void testDeleteUserNotFound() throws Exception {
		when(userService.findById(1L)).thenReturn(Optional.empty());

		mvc.perform(delete("/api/users/1")).andExpect(status().isNotFound());

		verify(userService).findById(1L);
		verify(userService, never()).deleteById(1L);
	}

	/**
	 * Prueba cuando un usuario se elimino
	 * 
	 * @throws Exception
	 */
	@Test
	void testDeleteUserSuccess() throws Exception {
		when(userService.findById(1L)).thenReturn(Optional.of(new User()));

		mvc.perform(delete("/api/users/1")).andExpect(status().isOk());

		verify(userService).findById(1L);
		verify(userService).deleteById(1L);
	}

	/**
	 * Prueba cuando obtiene todos los usuarios
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetAllUsers() throws Exception {

		when(userService.findAll()).thenReturn(users);

		mvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.users[0].name").value("Luis"))
				.andExpect(jsonPath("$.users[1].name").value("Carlos"))
				.andExpect(jsonPath("$.users[2].name").value("GATSBY"))
				.andExpect(jsonPath("$.users[0].email").value("email@email.com"))
				.andExpect(jsonPath("$.users[1].email").value("carlos@example.com"))
				.andExpect(jsonPath("$.users[2].email").value("gatsby@example.com"))
				.andExpect(jsonPath("$.users", hasSize(3))).andExpect(content()
						.json(new ObjectMapper().writeValueAsString(Collections.singletonMap("users", users))));

		verify(userService).findAll();

	}

	/**
	 * Prueba cuando hay una excepcion al obtener todos los usuarios
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetAllUsersExcepcion() throws Exception {

		when(userService.findAll()).thenThrow(new RuntimeException("There was a error."));

		mvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(userService).findAll();

	}

	/**
	 * Prueba cuando obtiene un usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetOneUser() throws Exception {

		when(userService.findById(1L)).thenReturn(Optional.of(user));

		mvc.perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.user.name").value("Luis"))
				.andExpect(jsonPath("$.user.email").value("email@email.com"))
				.andExpect(jsonPath("$.user.password").value("12345"))
				.andExpect(jsonPath("$.user.phone").value("1234567890"));

		verify(userService).findById(1L);

	}

	/**
	 * Prueba cuando hay una excepcion al obtener un usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetOneUserException() throws Exception {

		when(userService.findById(1L)).thenThrow(new RuntimeException("There was a error"));

		mvc.perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

		verify(userService).findById(1L);

	}

	/**
	 * Prueba cuando no encontro un usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetOneUserNotFound() throws Exception {
		when(userService.findById(1L)).thenReturn(Optional.empty());

		mvc.perform(get("/api/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()); // Espera

		verify(userService).findById(1L);
	}

	/**
	 * Prueba cuando se crea un nuevo usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testNewUser() throws Exception {

		when(userService.save(any(User.class))).thenReturn(user4);

		mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user4))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.user.name").value("GATSBY"))
				.andExpect(jsonPath("$.user.email").value("gatsby@example.com"))
				.andExpect(jsonPath("$.user.password").value("1234"))
				.andExpect(jsonPath("$.user.phone").value("1234567890"));

		verify(userService).save(any(User.class));
	}

	/**
	 * Prueba cuando hay un Bad Request al crear un nuevo usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testNewUserBadRequest() throws Exception {

		when(userService.save(any(User.class))).thenThrow(new ConstraintViolationException("There was a error", null));

		mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isBadRequest());

	}

	/**
	 * Prueba cuando hay un Internal Error al crear un usuario
	 * 
	 * @throws Exception
	 */
	@Test
	void testNewUserInternalError() throws Exception {

		when(userService.save(any(User.class))).thenThrow(new RuntimeException("There was a error."));

		mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isInternalServerError());

	}

	/**
	 * Prueba cuando hay campos vacios y devuelve las validaciones
	 * 
	 * @throws Exception
	 */
	@Test
	void testNewUserValidationErrors() throws Exception {

		mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(asJsonString(invalidUser)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.name").value("Name cannot be empty"))
				.andExpect(jsonPath("$.password").value("Password cannot be empty"))
				.andExpect(jsonPath("$.email").value("Email cannot be empty"));
	}

	/**
	 * Prueba de usuario actualizado
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateUser() throws Exception {

		when(userService.findById(1L)).thenReturn(Optional.of(user));
		when(userService.update(any(User.class))).thenReturn(updatedUser);

		mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser))).andExpect(status().isOk())
				.andExpect(jsonPath("$.user.id").value(1)).andExpect(jsonPath("$.user.name").value("Luis Updated"))
				.andExpect(jsonPath("$.user.email").value("updated.email@email.com"))
				.andExpect(jsonPath("$.user.password").value("54321"))
				.andExpect(jsonPath("$.user.phone").value("0987654321"));

		verify(userService).findById(1L);
		verify(userService).update(any(User.class));
	}

	/**
	 * Prueba cuando hay un bad request en update
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateUserBadRequest() throws Exception {

		when(userService.findById(1L)).thenReturn(Optional.of(user));
		when(userService.update(any(User.class)))
				.thenThrow(new ConstraintViolationException("There was a error", null));

		mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser))).andExpect(status().isBadRequest());

		verify(userService).findById(1L);

	}

	/**
	 * Prueba cuando hay un Internal Error en update
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateUserInternalError() throws Exception {
		when(userService.findById(1L)).thenReturn(Optional.of(user));
		when(userService.update(any(User.class))).thenThrow(new RuntimeException("There was a error."));

		mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser))).andExpect(status().isInternalServerError());

		verify(userService).findById(1L);

	}

	/**
	 * Prueba cuando no encuentra un usuario para actualizarlo
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateUserNotFound() throws Exception {

		when(userService.findById(1L)).thenReturn(Optional.empty());
		when(userService.update(any(User.class))).thenThrow(new RuntimeException("There was a error."));

		mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser))).andExpect(status().isNotFound());

		verify(userService).findById(1L);
		verify(userService, never()).update(updatedUser);

	}

	/**
	 * Prueba cuando hay validaciones en update
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateUserValidationErrors() throws Exception {

		when(userService.findById(anyLong())).thenReturn(Optional.of(new User()));

		mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(invalidUser)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.name").value("Name cannot be empty"))
				.andExpect(jsonPath("$.email").value("Email cannot be empty"))
				.andExpect(jsonPath("$.password").value("Password cannot be empty"));

		verify(userService).findById(anyLong());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);

	}

}
