package com.real.estate.user.populator;

import com.real.estate.user.entity.User;
import com.real.estate.user.model.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {
    public UserDTO populate(User user, UserDTO userDTO){
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setMobile(user.getMobile());
        userDTO.setRoleType(user.getRoleType());
        return userDTO;
    }

}
