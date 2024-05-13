package com.khoo.usermanagement.dto;

import com.khoo.usermanagement.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private User user;

    private String token;

    private String message;
}
