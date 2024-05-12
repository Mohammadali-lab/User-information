package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    ConfirmationCode register(User user) throws DuplicateUserException;

    User create(User user);

    User findById(Long id);

    User findByNationalCode(String nationalCode);

    User update(Long id, User updatedUser);

    void delete(Long id);

    List<Object[]> numberOfUsersPerCity();

    List<Object[]> numberOfUsersPerCityFilteredByAge(int age);

    int numberOfUsersFilteredByAgeAndCity(int age, String cityName);

    String confirmUser(ConfirmationCode confirmationCode);

    UserDetails loadUserByUsername(String username);
}
