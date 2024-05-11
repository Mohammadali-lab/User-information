package com.khoo.usermanagement.controller;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.security.jwt.CheckJwtToken;
import com.khoo.usermanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ConfirmationCode> create(@RequestBody User user) {
        ConfirmationCode confirmationCode = userService.create(user);
        return ResponseEntity.ok(confirmationCode);
    }


    @PostMapping("/confirm-code")
    public ResponseEntity<String> confirmUser(@RequestBody ConfirmationCode confirmationCode) {

        String code = userService.confirmUser(confirmationCode);
        return ResponseEntity.ok(code);

    }

    @CheckJwtToken
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @CheckJwtToken
    @GetMapping("/national-code/{nationalCode}")
    public ResponseEntity<User> findByNationalCode(@PathVariable String nationalCode) {
        User user = userService.findByNationalCode(nationalCode);
        return ResponseEntity.ok(user);
    }

    @CheckJwtToken
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User updatedUser) {
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
    @GetMapping("/count-per-city")
    public List<Object[]> getNumberOfUsersByCity() {
        return userService.getNumberOfUsersPerCity();
    }

    @CheckJwtToken
    @GetMapping("/users/age/{age}")
    public List<Object[]> getNumberOfUsersByCityAndAge(@PathVariable int age) {

        return userService.numberOfUsersPerCityFilteredByAge(age);
    }
}
