package com.real.estate.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.real.estate.user.entity.Role;
import com.real.estate.user.entity.User;
import com.real.estate.user.enums.UserStatus;
import com.real.estate.user.exception.UserNotFoundException;
import com.real.estate.user.model.UserDTO;
import com.real.estate.user.populator.UserPopulator;
import com.real.estate.user.repository.UserRepo;
import com.real.estate.user.repository.RoleRepo;
import com.real.estate.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDTO registerUser(User user) throws JsonProcessingException {
        logger.info("Register User Service Started!!!");
        try {
            if (checkUserExist(user.getUserName()) == null) {
                user.setUserStatus(UserStatus.ACTIVE.name());
                userRepo.save(user);
                UserDTO userDTO = new UserDTO();
                userPopulator.populate(user, userDTO);
                logger.info("User registration successful!");
                return userDTO;
            } else {
                throw new IllegalArgumentException(messageSource.getMessage("user.username.exists", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("Error occurred during user registration: ", e);
            throw new JsonProcessingException(messageSource.getMessage("registration.error", null, Locale.getDefault())) {};
        }
    }

    @Override
    public String loginUser(String username, String password) {
        logger.info("Login User Method Started::");
        boolean valid = userRepo.findAll()
                .stream()
                .anyMatch(user -> user.getUserName().equalsIgnoreCase(username) && user.getPassword().equals(password));

        if (valid) {
            logger.info("Login completed::");
            return "Login Successful";
        }
        logger.error("Login failed::");
        return "Login Failed: " + messageSource.getMessage("login.error", null, Locale.getDefault());
    }

    @Override
    public UserDTO updateUser(User user) throws UserNotFoundException {
        try {
            User existingUser = checkUserExist(user.getUserName());
            if (existingUser != null) {
                existingUser.setEmail(user.getEmail());
                userRepo.save(existingUser);
                UserDTO userDTO = new UserDTO();
                userPopulator.populate(existingUser, userDTO);
                return userDTO;
            } else {
                logger.error("Cannot update details as user does not exist.");
                throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while updating user details:", e);
            throw new RuntimeException(messageSource.getMessage("update.error", null, Locale.getDefault()), e);
        }
    }

    @Override
    public void deleteUser(String username) throws UserNotFoundException {
        logger.info("Delete user method::");
        try {
            User user = checkUserExist(username);
            if (user != null) {
                user.setUserStatus(UserStatus.INACTIVE.name());
                userRepo.save(user);
            } else {
                logger.error("Cannot delete as user doesn't exist");
                throw new UserNotFoundException(messageSource.getMessage("user.not.found", null, Locale.getDefault()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while deleting user:", e);
            throw new RuntimeException(messageSource.getMessage("delete.error", null, Locale.getDefault()), e);
        }
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
