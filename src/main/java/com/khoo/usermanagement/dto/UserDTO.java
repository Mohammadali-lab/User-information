package com.khoo.usermanagement.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private String firstName;

    private String lastName;

    private String nationalCode;

    private LocalDate brithDate;


}
