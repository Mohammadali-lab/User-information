package com.khoo.usermanagement.controller;


import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testRegister_ValidUser() {

        User user = new User();
        user.setNationalCode("1234567890");
        ConfirmationCode confirmationCode = new ConfirmationCode(user.getNationalCode(), "1234");
        when(userService.register(user)).thenReturn(confirmationCode);

        ResponseEntity<Object> response = userController.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(confirmationCode, response.getBody());
    }

    @Test
    public void testRegister_DuplicateUser() {

        User user = new User();
        user.setNationalCode("1234567890");
        when(userService.register(user)).thenThrow(new DuplicateUserException("Duplicate user"));

        ResponseEntity<Object> response = userController.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate user", response.getBody());
    }

    @Test
    public void testLogin_UserFound() {

        User user = new User();
        String nationalCode = "1234567890";
        ConfirmationCode confirmationCode = new ConfirmationCode(nationalCode, "1234");
        when(userService.findByNationalCode(nationalCode)).thenReturn(user);
        when(userService.login(user)).thenReturn(confirmationCode);

        ResponseEntity<Object> response = userController.login(nationalCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(confirmationCode, response.getBody());
    }

    @Test
    public void testLogin_UserNotFound() {

        String nationalCode = "1234567890";
        when(userService.findByNationalCode(nationalCode)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<Object> response = userController.login(nationalCode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testConfirmUser_SuccessfulConfirmation() {

        User user = new User();
        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        UserDTO userDTO = new UserDTO();
        when(userService.findByNationalCode(confirmationCode.getUserNationalCode())).thenReturn(user);
        when(userService.confirmUser(user, confirmationCode.getConfirmedCode())).thenReturn(userDTO);

        ResponseEntity<Object> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testConfirmUser_UserNotFound() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        when(userService.findByNationalCode(confirmationCode.getUserNationalCode())).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<Object> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testConfirmUser_Unauthorized() {

        User user = new User();
        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        when(userService.findByNationalCode(confirmationCode.getUserNationalCode())).thenReturn(user);
        when(userService.confirmUser(user, confirmationCode.getConfirmedCode())).thenThrow(new UnauthorizedException("Unauthorized"));

        ResponseEntity<Object> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    public void testFindById_UserFound() {

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userService.findById(userId)).thenReturn(user);

        ResponseEntity<Object> response = userController.findById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testFindById_UserNotFound() {

        long userId = 1L;
        when(userService.findById(userId)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<Object> response = userController.findById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }


}