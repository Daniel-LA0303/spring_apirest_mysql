package com.example.SB_apirest_mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.repository.UserRepository;
import com.example.SB_apirest_mysql.service.UserServiceImpl;
import com.example.SB_apirest_mysql.utils.UserBuilder;

@ExtendWith(MockitoExtension.class)
public class UserServicesTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	private User user;

	private List<User> users = new ArrayList<>();

	@BeforeEach
	void setUp() {
		user = UserBuilder.withAllDumy().build();

		User user2 = new UserBuilder().setId(2L).setName("Carlos").setEmail("carlos@example.com")
				.setPassword("password123").setPhone("0987654321").build();

		User user3 = new UserBuilder().setId(3L).setName("GATSBY").setEmail("gatsby@example.com")
				.setPassword("password123").setPhone("0987654321").build2();

		users.add(user);
		users.add(user2);
		users.add(user3);
	}

	@Test
	@Order(3)
	@DisplayName("Create one user")
	@Tag("Create")
	void testCreateUser() {

		when(userRepository.save(user)).thenReturn(user);
		User savedUser = userServiceImpl.save(user);

		assertNotNull(savedUser);
		assertFalse(savedUser.getName().isEmpty());
		assertFalse(savedUser.getEmail().isEmpty());
		assertFalse(savedUser.getPassword().isEmpty());
		assertFalse(savedUser.getPhone().isEmpty());

		assertEquals("Luis", savedUser.getName());
		assertEquals("email@email.com", savedUser.getEmail());
		assertEquals("12345", savedUser.getPassword());
		assertEquals("1234567890", savedUser.getPhone());
		assertEquals(1L, savedUser.getId());

		verify(userRepository, times(1)).save(any(User.class));
		verify(userRepository, never()).findAll();

	}

	@Test
	@Order(5)
	@DisplayName("Delete one user")
	@Tag("Delete")
	void testDeleteUser() {

		doNothing().when(userRepository).deleteById(1L);

		userServiceImpl.deleteById(1L);

		verify(userRepository, times(1)).deleteById(1L);

	}

	@Test
	@Order(1)
	@DisplayName("Get all users")
	@Tag("GetAll")
	void testGetAllUsers() {

		when(userRepository.findAll()).thenReturn(users);

		List<User> usuarios = userServiceImpl.findAll();

		assertNotNull(usuarios);
		assertEquals(usuarios.size(), 3);

		assertEquals("Luis", users.get(0).getName());
		assertEquals("email@email.com", users.get(0).getEmail());
		assertEquals("12345", users.get(0).getPassword());
		assertEquals("1234567890", users.get(0).getPhone());

		verify(userRepository, times(1)).findAll();
	}

	@Test
	@Order(2)
	@DisplayName("Get one user")
	@Tag("GetOne")
	void testGetOneUser() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		Optional<User> userR = userServiceImpl.findById(1L);
		assertNotNull(userR.get());
		assertTrue(userR.isPresent());

		assertEquals("Luis", userR.get().getName());
		assertEquals("email@email.com", userR.get().getEmail());
		assertEquals("12345", userR.get().getPassword());
		assertEquals("1234567890", userR.get().getPhone());
		assertEquals(1L, userR.get().getId());

		verify(userRepository, times(1)).findById(1L);
		verify(userRepository, never()).findAll();
		verify(userRepository, never()).deleteById(1L);

	}

	@Test
	@Order(4)
	@DisplayName("Update one user")
	@Tag("Update")
	void testUpdateUser() {

		when(userRepository.findAll()).thenReturn(users);
		when(userRepository.save(user)).thenReturn(user);

		List<User> usersUpdate = userServiceImpl.findAll();

		User userUpdate = userServiceImpl.update(user);

		assertNotNull(userUpdate);
		assertEquals("Luis", userUpdate.getName());
		assertEquals("email@email.com", userUpdate.getEmail());
		assertEquals("12345", userUpdate.getPassword());
		assertEquals("1234567890", userUpdate.getPhone());
		assertEquals(1L, userUpdate.getId());

		assertFalse(userUpdate.getName().isEmpty());

		assertNotNull(usersUpdate);
		assertEquals(usersUpdate.size(), 3);

		assertTrue(usersUpdate.contains(user));

		verify(userRepository, times(1)).findAll();
		verify(userRepository, times(1)).save(any(User.class));

	}

}
