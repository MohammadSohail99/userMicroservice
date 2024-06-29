package com.real.estate.user.model;

import com.real.estate.user.entity.Role;
import com.real.estate.user.validations.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long UUID;
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<Role> roles;
    private String userStatus;
}
