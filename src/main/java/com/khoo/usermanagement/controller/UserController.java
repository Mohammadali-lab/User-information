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
    public ResponseEntity<Object> register(@Valid @RequestBody User user) {
        try{
            ConfirmationCode confirmationCode = userService.register(user);
            return ResponseEntity.ok(confirmationCode);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("login/{nationalCode}")
    public ResponseEntity<Object> login(@PathVariable String nationalCode) {

        User user;
        try {
            user = userService.findByNationalCode(nationalCode);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        ConfirmationCode confirmationCode = userService.login(user);
        return ResponseEntity.ok(confirmationCode);
    }

    @CheckJwtToken
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.logout(username);
        return ResponseEntity.ok("logged out successfully");
    }


    @PostMapping("/confirm-code")
    public ResponseEntity<Object> confirmUser(@RequestBody ConfirmationCode confirmationCode) {

        try {
            User user = userService.findByNationalCode(confirmationCode.getUserNationalCode());
            UserDTO userDTO = userService.confirmUser(user, confirmationCode.getConfirmedCode());
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody User inputUser) {
        try {
            User user = userService.create(inputUser);
            return ResponseEntity.ok(user);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try{
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    @PatchMapping
    public ResponseEntity<User> updateProfile(@Valid @RequestBody User updatedUser) {

        String nationalCode = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.updateProfile(nationalCode, updatedUser);
        return ResponseEntity.ok(user);
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody User updatedUser) {

        try {
            User user = userService.update(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @CheckJwtToken
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        try{
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
    @GetMapping("/count-by-age-and-cityId/{age}/{cityId}")
    public int getNumberOfUsersByAgeAndCity(@PathVariable int age, @PathVariable Long cityId) {
        return userService.numberOfUsersFilteredByAgeAndCity(age, cityId);
    }
}
