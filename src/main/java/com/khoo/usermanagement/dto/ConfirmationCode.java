package com.khoo.usermanagement.dto;

import com.khoo.usermanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ConfirmationCode {

    private String userNationalCode;

    private String confirmedCode;
}
