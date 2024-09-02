package com.example.SB_apirest_mysql.utils;

import com.example.SB_apirest_mysql.models.User;

public class UserBuilder {

	private Long id;

	private String name;

	private String email;

	private String password;

	private String phone;

	public static UserBuilder withAllDumy() {

		return new UserBuilder().setEmail("email@email.com").setId(1L).setName("Luis").setPassword("12345")
				.setPhone("1234567890");
	}

	public User build() {
		User user = new User();

		user.setEmail(email);
		user.setId(id);
		user.setName(name);
		user.setPassword(password);
		user.setPhone(phone);

		return user;
	}

	public User build2() {
		User user = new User(id, name, email, password, phone);
		return user;
	}

	public User build3() {
		User user = new User();
		user.setEmail(email);
		user.setName(name);
		user.setPassword(password);
		user.setPhone(phone);

		return user;
	}

	/**
	 * set the value of the propertie email
	 *
	 * @param email the email to set
	 */
	public UserBuilder setEmail(String email) {
		this.email = email;
		return this;
	}

	/**
	 * set the value of the propertie id
	 *
	 * @param id the id to set
	 */
	public UserBuilder setId(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * set the value of the propertie name
	 *
	 * @param name the name to set
	 */
	public UserBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * set the value of the propertie password
	 *
	 * @param password the password to set
	 */
	public UserBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * set the value of the propertie phone
	 *
	 * @param phone the phone to set
	 */
	public UserBuilder setPhone(String phone) {
		this.phone = phone;
		return this;
	}

}
