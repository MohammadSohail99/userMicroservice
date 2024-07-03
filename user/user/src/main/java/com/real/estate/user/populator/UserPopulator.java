package com.real.estate.user.populator;

import com.real.estate.user.entity.User;
import com.real.estate.user.model.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {
    public void populate(User user, UserDTO userDTO){
        userDTO.setUUID(user.getUUID());
        userDTO.setUserName(user.getUserName());
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
    }

}
