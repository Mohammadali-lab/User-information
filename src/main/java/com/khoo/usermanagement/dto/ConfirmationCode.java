package com.khoo.usermanagement.dto;

import com.khoo.usermanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class ConfirmationCode {

    private String userNationalCode;

    private String confirmedCode;
}
