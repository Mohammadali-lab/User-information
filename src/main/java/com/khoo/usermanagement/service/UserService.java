package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    ConfirmationCode register(User user);

    ConfirmationCode login(User user);

    void logout(String nationalCode);

    User create(User user);

    User findById(Long id);

    User findByNationalCode(String nationalCode);

    Page<User> findAll(Pageable pageable);

    User updateProfile(String nationalCode, User updatedUser);

    User update(Long id, User user);

    void delete(Long id);

    List<Object[]> numberOfUsersPerCity();

    List<Object[]> numberOfUsersPerCityFilteredByAge(int age);

    int numberOfUsersFilteredByAgeAndCity(int age, Long cityId);

    UserDTO confirmUser(User user, String code);

    UserDetails loadUserByUsername(String username);
}
