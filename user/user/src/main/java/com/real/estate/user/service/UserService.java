package com.real.estate.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.real.estate.user.entity.User;
import com.real.estate.user.model.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserDTO> registerUser(User user) throws JsonProcessingException;

    ResponseEntity<String> loginUser(String userName, String password);

    ResponseEntity<UserDTO> updateUser(User user);

    User checkUserExist(String userName);

    ResponseEntity<Void> deleteUser(String userName);

    ResponseEntity<List<UserDTO>> getAllUsers();
}
