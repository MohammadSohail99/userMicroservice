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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserPopulator userPopulator;
    Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserDTO registerUser(User user) throws JsonProcessingException {
        logger.info("Register User Service Started!!!");
        try {
            User existingUser = checkUserExist(user);
            if (existingUser == null) {
                user.setUserStatus(String.valueOf(UserStatus.ACTIVE));
                UserDTO userDTO = new UserDTO();
//                BeanUtils.copyProperties(user, userDTO);
                userPopulator.populate(user,userDTO);
                userRepo.save(user);
                logger.info("User registration successful!");
                return userDTO;
            } else {
                throw new Exception("User already exists");
            }
        } catch (Exception e) {
            logger.error("Error occurred during user registration: ", e);
            throw new JsonProcessingException("Error occurred during user registration") {};
        }
    }

    @Override
    public String loginUser(String userName, String password) {
        logger.info("login User Method Started::");
        boolean valid = userRepo.findAll()
                .stream()
                .anyMatch(user -> user.getUserName().equalsIgnoreCase(userName) && user.getPassword().equals(password));

        if (valid) {
            logger.info("login completed::");
            return "Login Successful";
        }
        logger.error("Login failed::");
        return "Login Failed :Invalid UserName or Password";
    }

    @Override
    public UserDTO updateUser(User user)throws UserNotFoundException{
        try {
            User existuser=checkUserExist(user);
            if(!StringUtils.isEmpty(existuser)){
                existuser.setEmail(user.getEmail());
                existuser.setRoleType(user.getRoleType());
                existuser.setMobile(user.getMobile());
                userRepo.save(existuser);
                UserDTO userDTO =new UserDTO();
//                BeanUtils.copyProperties(existuser, userDTO);
                userPopulator.populate(existuser,userDTO);
                return userDTO;

            }
            else {
                logger.error("Cannot update details as user does not exist.");
                throw new UserNotFoundException("User does not exist.");
            }
        } catch (Exception e) {
            logger.error("An error occurred while updating user details:", e);
            throw new RuntimeException("Failed to update user details.", e);
        }
    }

    @Override
    public User checkUserExist(String userName) {
        return userRepo.findByUserName(userName);
    }

    @Override
    public void deleteUser(String userName) throws UserNotFoundException {
       logger.info("delete user method::");
       try {
           User user=userRepo.findByUserName(userName);
           if(user!=null){
               user.setUserStatus(String.valueOf(UserStatus.INACTIVE));
               userRepo.save(user);
           } else {
               logger.error("Cannot delete as user doesn't exist");
               throw new UserNotFoundException("User does not exist.");
           }
       } catch (Exception e) {
           logger.error("An error occurred while deleting user:", e);
           throw new RuntimeException("Failed to delete user.", e);
       }
    }

    private User checkUserExist(User user) {
        logger.info("check User Exist Based on UserName");
        String userName = user.getUserName();
        return userRepo.findAll().stream()
                .filter(user2 -> user2.getUserName().equalsIgnoreCase(userName))
                .findFirst()
                .orElse(null);
    }
}
