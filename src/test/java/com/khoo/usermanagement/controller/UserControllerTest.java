package com.khoo.usermanagement.controller;


import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        ResponseEntity<ConfirmationCode> response = userController.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(confirmationCode, response.getBody());
    }

    @Test
    public void testRegister_DuplicateUser() {

        User user = new User();
        user.setNationalCode("1234567890");
        when(userService.register(user)).thenThrow(new DuplicateUserException("Duplicate user"));

        ResponseEntity<ConfirmationCode> response = userController.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testLogin_UserFound() {

        String nationalCode = "1234567890";
        ConfirmationCode confirmationCode = new ConfirmationCode(nationalCode, "1234");
        when(userService.login(nationalCode)).thenReturn(confirmationCode);

        ResponseEntity<ConfirmationCode> response = userController.login(nationalCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(confirmationCode, response.getBody());
    }

    @Test
    public void testLogin_UserNotFound() {

        String nationalCode = "1234567890";
        when(userService.login(nationalCode)).thenThrow(new UserNotFoundException("user not found"));

        ResponseEntity<ConfirmationCode> response = userController.login(nationalCode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testConfirmUser_SuccessfulConfirmation() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        UserDTO userDTO = new UserDTO();
        when(userService.confirmUser(confirmationCode)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testConfirmUser_UserNotFound() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        when(userService.confirmUser(confirmationCode)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<UserDTO> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testConfirmUser_Unauthorized() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        when(userService.confirmUser(confirmationCode)).thenThrow(new UnauthorizedException("Unauthorized"));

        ResponseEntity<UserDTO> response = userController.confirmUser(confirmationCode);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testFindById_UserFound() {

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userService.findById(userId)).thenReturn(user);

        ResponseEntity<User> response = userController.findById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testFindById_UserNotFound() {

        long userId = 1L;
        when(userService.findById(userId)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<User> response = userController.findById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }


}