package com.real.estate.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.real.estate.user.entity.User;
import com.real.estate.user.enums.UserStatus;
import com.real.estate.user.exception.UserNotFoundException;
import com.real.estate.user.model.UserDTO;
import com.real.estate.user.populator.UserPopulator;
import com.real.estate.user.repository.UserRepo;
import com.real.estate.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<UserDTO> registerUser(User user) throws JsonProcessingException {
        logger.info("Register User Service Started!!!");
        try {
            if (checkUserExist(user.getUserName()) == null) {
                user.setUserStatus(UserStatus.ACTIVE.name());
                userRepo.save(user);
                UserDTO userDTO = new UserDTO();
                userPopulator.populate(user, userDTO);
                logger.info("User registration successful!");
                return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
            } else {
                throw new IllegalArgumentException(messageSource.getMessage("user.username.exists", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("Error occurred during user registration: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> loginUser(String username, String password) {
        logger.info("Login User Method Started::");
        boolean valid = userRepo.findAll()
                .stream()
                .anyMatch(user -> user.getUserName().equalsIgnoreCase(username) && user.getPassword().equals(password));

        if (valid) {
            logger.info("Login completed::");
            return new ResponseEntity<>("Login Successful", HttpStatus.OK);
        }
        logger.error("Login failed::");
        return new ResponseEntity<>("Login Failed: " + messageSource.getMessage("login.error", null, Locale.getDefault()), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(User user) {
        try {
            User existingUser = checkUserExist(user.getUserName());
            if (existingUser != null) {
                existingUser.setEmail(user.getEmail());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setUserStatus(user.getUserStatus());
                existingUser.setUserName(user.getUserName());
                existingUser.setPassword(user.getPassword());

                userRepo.save(existingUser);
                UserDTO userDTO = new UserDTO();
                userPopulator.populate(existingUser, userDTO);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                logger.error("Cannot update details as user does not exist.");
                throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while updating user details:", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> deleteUser(String username) {
        logger.info("Delete user method::");
        try {
            User user = checkUserExist(username);
            if (user != null) {
                user.setUserStatus(UserStatus.INACTIVE.name());
                userRepo.save(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.error("Cannot delete as user doesn't exist");
                throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while deleting user:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOS = new ArrayList<>();
        userRepo.findAll().forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userPopulator.populate(user, userDTO);
            userDTOS.add(userDTO);
        });
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @Override
    public User checkUserExist(String username) {
        logger.info("Check if user exists based on username");
        return userRepo.findAll().stream()
                .filter(user -> user.getUserName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
}
