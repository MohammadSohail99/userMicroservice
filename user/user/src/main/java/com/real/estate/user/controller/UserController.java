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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Map<String, String> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) throws JsonProcessingException {
        logger.info("Invoke Register User Method::");
        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    response.put(error.getField(), error.getDefaultMessage()));
            return response;
        }

        try {
            if (user.getUserName() != null && user.getPassword() != null) {
                UserDTO userDTO = userService.registerUser(user);
                response.put("message", messageSource.getMessage("registration.success", null, Locale.getDefault()));
                logger.info("User Registered Successfully::");
            } else {
                response.put("error", messageSource.getMessage("user.username.null", null, Locale.getDefault()));
                logger.error("User Cannot be Registered due to null username or password::");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            logger.error("User Cannot be Registered issue occurred::", e);
        }
        return response;
    }

    @GetMapping("/loginUser")
    public Map<String, String> loginUser(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        Map<String, String> response = new HashMap<>();
        if (userName == null || userName.isEmpty()) {
            response.put("error", messageSource.getMessage("login.username.null", null, Locale.getDefault()));
        } else if (password == null || password.isEmpty()) {
            response.put("error", messageSource.getMessage("login.password.null", null, Locale.getDefault()));
        } else {
            String loginResult = userService.loginUser(userName, password);
            response.put("message", loginResult);
        }
        return response;
    }

    @PutMapping("/updateUser")
    public UserDTO updateUser(@RequestBody User user) throws JsonProcessingException {
        if (user == null || user.getUserName() == null) {
            throw new IllegalArgumentException(messageSource.getMessage("user.username.null", null, Locale.getDefault()));
        }
        return userService.updateUser(user);
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam("userName") String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("user.username.null", null, Locale.getDefault()));
        }
        userService.deleteUser(userName);
        return messageSource.getMessage("user.delete.success", null, Locale.getDefault());
    }
}
