package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setFirstName("MohammadAli");
        user.setLastName("Khoo");
        user.setNationalCode("1234567890");
        user.setBirthDate(LocalDate.parse("1994-03-06"));
        when(userRepository.findByNationalCode(user.getNationalCode())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        ConfirmationCode confirmationCode = userService.register(user);

        assertNotNull(confirmationCode);
        assertEquals(user.getNationalCode(), confirmationCode.getUserNationalCode());
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUser_DuplicateNationalCode() {
        User user = new User();
        user.setFirstName("MohammadAli");
        user.setLastName("Khoo");
        user.setNationalCode("1234567890");
        user.setBirthDate(LocalDate.parse("1994-03-06"));
        when(userRepository.findByNationalCode(user.getNationalCode())).thenReturn(Optional.of(new User()));

        try {
            userService.register(user);
            assert false;
        } catch (RuntimeException e) {
            assertEquals("User with national code already exists", e.getMessage());
        }
    }

    @Test
    public void testFindByNationalCode() {
        String nationalCode = "1234567890";
        User user = new User();
        user.setNationalCode(nationalCode);

        when(userRepository.findByNationalCode(nationalCode)).thenReturn(java.util.Optional.of(user));

        User foundUser = userService.findByNationalCode(nationalCode);
        assertEquals(nationalCode, foundUser.getNationalCode());
    }

    @Test
    public void testFindByNationalCodeNotFound() {
        String nationalCode = "1234567890";

        when(userRepository.findByNationalCode(nationalCode)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByNationalCode(nationalCode));
    }


    @Test
    public void testUpdate() {
        Long id = 1L;
        User updatedUser = new User();
        updatedUser.setFirstName("MohammadAli");
        updatedUser.setLastName("Khoo");
        updatedUser.setNationalCode("1234567890");

        User user = new User();
        user.setId(id);
        user.setFirstName("Ali");
        user.setLastName("Khou");
        user.setNationalCode("0987654321");

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.update(id, updatedUser);

        assertEquals(id, result.getId());
        assertEquals("MohammadAli", result.getFirstName());
        assertEquals("Khoo", result.getLastName());
        assertEquals("1234567890", result.getNationalCode());

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }
}
