package com.real.estate.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.real.estate.user.entity.User;
import com.real.estate.user.model.UserDTO;
import com.real.estate.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/registerUser")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) throws JsonProcessingException {
        logger.info("Invoke Register User Method::");
        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    response.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            if (user.getUserName() != null && user.getPassword() != null) {
                ResponseEntity<UserDTO> registeredUserResponse = userService.registerUser(user);
                if (registeredUserResponse.getStatusCode() == HttpStatus.CREATED) {
                    response.put("message", messageSource.getMessage("registration.success", null, Locale.getDefault()));
                    logger.info("User Registered Successfully::");
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                } else {
                    response.put("error", "Registration failed");
                    logger.error("User registration failed::");
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                response.put("error", messageSource.getMessage("user.username.null", null, Locale.getDefault()));
                logger.error("User Cannot be Registered due to null username or password::");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            logger.error("User Cannot be Registered issue occurred::", e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/loginUser")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        Map<String, String> response = new HashMap<>();
        if (userName == null || userName.isEmpty()) {
            response.put("error", messageSource.getMessage("login.username.null", null, Locale.getDefault()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (password == null || password.isEmpty()) {
            response.put("error", messageSource.getMessage("login.password.null", null, Locale.getDefault()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            ResponseEntity<String> loginResult = userService.loginUser(userName, password);
            if (loginResult.getStatusCode() == HttpStatus.OK) {
                response.put("message", loginResult.getBody());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("error", loginResult.getBody());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDTO> updateUser(@RequestBody User user) throws JsonProcessingException {
        if (user == null || user.getUserName() == null) {
            throw new IllegalArgumentException(messageSource.getMessage("user.username.null", null, Locale.getDefault()));
        }
        ResponseEntity<UserDTO> updatedUserResponse = userService.updateUser(user);
        if (updatedUserResponse.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(updatedUserResponse.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam("userName") String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.username.null", null, Locale.getDefault()));
        }
        ResponseEntity<Void> deleteResponse = userService.deleteUser(userName);
        if (deleteResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(messageSource.getMessage("user.delete.success", null, Locale.getDefault()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Invoke get all users method::");
        ResponseEntity<List<UserDTO>> allUsersResponse = userService.getAllUsers();
        if (allUsersResponse.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(allUsersResponse.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
