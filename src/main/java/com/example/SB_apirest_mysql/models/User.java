package com.example.SB_apirest_mysql.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "user_profiles")
public class User {

	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "Name cannot be empty")
	private String name;
	@Email(message = "Email should be valid")
	@NotEmpty(message = "Email cannot be empty")
	private String email;
	@NotEmpty(message = "Password cannot be empty")
	private String password;
	private String phone;

	public User() {
	}

	public User(Long id, String name, String email, String password, String phone) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
