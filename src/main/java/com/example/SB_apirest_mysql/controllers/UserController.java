package com.example.SB_apirest_mysql.controllers;

import com.example.SB_apirest_mysql.models.User;
import com.example.SB_apirest_mysql.service.UserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/api")
@RestController
//@Api(tags = "User Management", description = "Endpoints para gestionar usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    //@ApiOperation("Get all users")
    public ResponseEntity<Map<String, List<User>>> getUsers(){
        try {
            List<User> users = userService.findAll();
            Map<String, List<User>> response = Collections.singletonMap("users", users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);

            if (user.isPresent()) {
                Map<String, Object> response = Collections.singletonMap("user", user.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {

            if (bindingResult.hasErrors()) {

                Map<String, Object> errors = new HashMap<>();
                errors.put("message", "Validation failed");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            User newUser = userService.save(user);

            Map<String, Object> response = Collections.singletonMap("user", newUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            Optional<User> userToUpdate = userService.findById(id);

            if (userToUpdate.isPresent()) {
                if (bindingResult.hasErrors()) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("message", "Validation failed");
                    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                    for (FieldError fieldError : fieldErrors) {
                        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                }

                User existingUser = userToUpdate.get();
                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setPassword(user.getPassword());
                existingUser.setPhone(user.getPhone());

                User updatedUser = userService.update(existingUser);

                Map<String, Object> response = Collections.singletonMap("user", updatedUser);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);

            if (user.isPresent()) {
                userService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
