package com.khoo.usermanagement.service;

import com.khoo.usermanagement.dao.UserRepository;
import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.dto.UserDTO;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.exception.UnauthorizedException;
import com.khoo.usermanagement.exception.UserNotFoundException;
import com.khoo.usermanagement.security.jwt.JwtUtil;
import com.khoo.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<Long, User> redisTemplate;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    public void testRegister_NewUser() {

        User user = new User();
        user.setNationalCode("1234567890");
        when(userRepository.findByNationalCode(user.getNationalCode())).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setNationalCode(user.getNationalCode());

        when(userRepository.save(user)).thenReturn(savedUser);


        ConfirmationCode confirmationCode = userService.register(user);

        assertNotNull(confirmationCode);
        assertFalse(user.isEnable());
        assertNotNull(user.getConfirmedCode());
        assertNotNull(user.getConfirmCodeRegisterTime());
    }

    @Test
    public void testRegister_DuplicateUser() {

        User user = new User();
        user.setNationalCode("1234567890");
        when(userRepository.findByNationalCode(user.getNationalCode())).thenReturn(Optional.of(user));

        assertThrows(DuplicateUserException.class, () -> userService.register(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testLogin() {

        String nationalCode = "1234567890";
        User mockUser = new User();
        mockUser.setNationalCode(nationalCode);

        User savedUser = new User();
        savedUser.setNationalCode(mockUser.getNationalCode());

        when(userRepository.save(mockUser)).thenReturn(savedUser);

        ConfirmationCode confirmationCode = userService.login(mockUser);

        assertNotNull(confirmationCode);
        assertFalse(mockUser.isEnable());
        assertNotNull(mockUser.getConfirmedCode());
        assertNotNull(mockUser.getConfirmCodeRegisterTime());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testConfirmUser_SuccessfulConfirmation() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        User user = new User();
        user.setNationalCode(confirmationCode.getUserNationalCode());
        user.setConfirmedCode(confirmationCode.getConfirmedCode());
        user.setEnable(false);
        Date confirmationTime = new Date(System.currentTimeMillis() - 2*60*1000);
        user.setConfirmCodeRegisterTime(confirmationTime);
        when(userRepository.save(user)).thenReturn(user);

        when(jwtUtil.generateToken(user.getNationalCode())).thenReturn("someToken");

        UserDTO userDTO = userService.confirmUser(user, confirmationCode.getConfirmedCode());

        assertNotNull(userDTO);
        assertTrue(user.isEnable());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testConfirmUser_Unauthorized() {

        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "1234");
        User user = new User();
        user.setNationalCode(confirmationCode.getUserNationalCode());
        user.setConfirmedCode("5678"); // Incorrect confirmation code
        user.setEnable(false);
        user.setConfirmCodeRegisterTime(new Date(System.currentTimeMillis() - 2*60*1000));

        assertThrows(UnauthorizedException.class, () -> userService.confirmUser(user, confirmationCode.getConfirmedCode()));
        assertFalse(user.isEnable());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testFindById_UserFound() {

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userService.readUserFromRedis(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    public void testFindById_UserNotFound() {

        long userId = 1L;
        when(userService.readUserFromRedis(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    public void testFindByNationalCode_UserFound() {

        String nationalCode = "1234567890";
        User user = new User();
        user.setNationalCode(nationalCode);
        when(userRepository.findByNationalCode(nationalCode)).thenReturn(Optional.of(user));

        User foundUser = userService.findByNationalCode(nationalCode);

        assertNotNull(foundUser);
        assertEquals(nationalCode, foundUser.getNationalCode());
    }

    @Test
    public void testFindByNationalCode_UserNotFound() {

        String nationalCode = "1234567890";
        when(userRepository.findByNationalCode(nationalCode)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByNationalCode(nationalCode));
    }
}