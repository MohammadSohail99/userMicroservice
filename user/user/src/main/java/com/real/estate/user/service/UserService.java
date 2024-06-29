package com.real.estate.user.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.real.estate.user.entity.User;
import com.real.estate.user.model.UserDTO;


public interface UserService {
    UserDTO registerUser(User user)throws JsonProcessingException;

    String loginUser(String userName, String password);

    UserDTO updateUser(User user);

    User checkUserExist(String userName);

    void deleteUser(String userName);
}
