package com.example.SB_apirest_mysql.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.repository.UserRepository;
import com.example.SB_apirest_mysql.utils.UserBuilder;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/import.sql")
public class RepositoriesTest {

	@Autowired
	UserRepository userRepository;

	@Test
	@Order(5)
	void testDeleteById() {

		Optional<User> user = userRepository.findById(2L);

		assertFalse(user.isEmpty());

		userRepository.deleteById(2L);

		Optional<User> deletedUser = userRepository.findById(2L);
		assertTrue(deletedUser.isEmpty(), "This user does not exists");
	}

	@Test
	@Order(1)
	void testFindAllUsers() {
		List<User> users = (List<User>) userRepository.findAll();

		assertFalse(users.isEmpty());
		assertEquals(users.size(), 2);

	}

	@Test
	@Order(2)
	void testFindById() {

		Optional<User> user = userRepository.findById(2L);

		assertFalse(user.isEmpty());
		assertEquals("jane.smith@example.com", user.get().getEmail());
		assertEquals("Jane Smith", user.get().getName());
		assertEquals("password456", user.get().getPassword());
		assertEquals("098-765-4321", user.get().getPhone());

	}

	@Test
	@Order(3)
	void testSaveUser() {

		User newUser = UserBuilder.withAllDumy().setId(null).build();

		User user = userRepository.save(newUser);

		assertEquals(newUser.getEmail(), user.getEmail());
		assertEquals(newUser.getName(), user.getName());
		assertEquals(newUser.getPassword(), user.getPassword());
		assertEquals(newUser.getPhone(), user.getPhone());

	}

	@Test
	@Order(4)
	void testUpdateUser() {

		User userToUpdate = UserBuilder.withAllDumy().setId(null).build();

		Optional<User> user = userRepository.findById(2L);

		assertFalse(user.isEmpty());
		assertEquals("jane.smith@example.com", user.get().getEmail());
		assertEquals("Jane Smith", user.get().getName());
		assertEquals("password456", user.get().getPassword());
		assertEquals("098-765-4321", user.get().getPhone());

		User newUserUpdated = userRepository.save(userToUpdate);

		assertEquals(userToUpdate.getEmail(), newUserUpdated.getEmail());
		assertEquals(userToUpdate.getName(), newUserUpdated.getName());
		assertEquals(userToUpdate.getPassword(), newUserUpdated.getPassword());
		assertEquals(userToUpdate.getPhone(), newUserUpdated.getPhone());

	}

}
