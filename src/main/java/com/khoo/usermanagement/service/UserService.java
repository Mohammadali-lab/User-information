package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    ConfirmationCode create(User user);

    User findById(Long id);

    User findByNationalCode(String nationalCode);

    User update(Long id, User updatedUser);

    void delete(Long id);

    List<Object[]> getNumberOfUsersPerCity();

    List<Object[]> numberOfUsersPerCityFilteredByAge(int age);

    String confirmUser(ConfirmationCode confirmationCode);
}
