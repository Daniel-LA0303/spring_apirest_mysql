package com.example.SB_apirest_mysql.repository;

import com.example.SB_apirest_mysql.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    //Optional<User> update(User user);
}
