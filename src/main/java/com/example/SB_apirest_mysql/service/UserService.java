package com.example.SB_apirest_mysql.service;

import com.example.SB_apirest_mysql.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);

    void deleteById(Long id);
}
