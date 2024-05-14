package com.khoo.usermanagement.controller;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.security.jwt.CheckJwtToken;
import com.khoo.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ConfirmationCode> register(@Valid @RequestBody User user) {
        try{
            ConfirmationCode confirmationCode = userService.register(user);
            return ResponseEntity.ok(confirmationCode);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("login/{nationalCode}")
    public ResponseEntity<ConfirmationCode> login(@PathVariable String nationalCode) {
        try {
            ConfirmationCode confirmationCode = userService.login(nationalCode);
            return ResponseEntity.ok(confirmationCode);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @CheckJwtToken
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.logout(username);
        return ResponseEntity.ok("logged out successfully");
    }


    @PostMapping("/confirm-code")
    public ResponseEntity<UserDTO> confirmUser(@RequestBody ConfirmationCode confirmationCode) {

        try {
            UserDTO userDTO = userService.confirmUser(confirmationCode);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User inputUser) {
        try {
            User user = userService.create(inputUser);
            return ResponseEntity.ok(user);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        try{
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @CheckJwtToken
    @GetMapping("/get-profile")
    public ResponseEntity<User> getProfile() {

        String nationalCode = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findByNationalCode(nationalCode);
        return ResponseEntity.ok(user);
    }

    @CheckJwtToken
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        User user = userService.update(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count-per-city")
    public List<Object[]> getNumberOfUsersPerCity() {
        return userService.numberOfUsersPerCity();
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count-per-city-by-age/{age}")
    public List<Object[]> getNumberOfUsersPerCityByAge(@PathVariable int age) {

        return userService.numberOfUsersPerCityFilteredByAge(age);
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count-by-age-and-city/{age}/{city}")
    public int getNumberOfUsersByAgeAndCity(@PathVariable int age, @PathVariable String city) {
        return userService.numberOfUsersFilteredByAgeAndCity(age, city);
    }
}
