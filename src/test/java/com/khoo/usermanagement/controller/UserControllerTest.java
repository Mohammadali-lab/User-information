package com.khoo.usermanagement.controller;

import com.khoo.usermanagement.dto.ConfirmationCode;
import com.khoo.usermanagement.entity.User;
import com.khoo.usermanagement.exception.DuplicateUserException;
import com.khoo.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void testRegisterUser() throws DuplicateUserException {
        // Arrange
        User user = new User("1234567890", "John Doe", "johndoe@example.com");
        ConfirmationCode confirmationCode = new ConfirmationCode("1234567890", "123456");
        when(userService.register(user)).thenReturn(confirmationCode);

        // Act
        ResponseEntity<ConfirmationCode> response = userController.register(user);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(confirmationCode, response.getBody());
    }

    @Test
    void testRegisterUser_DuplicateNationalCode() throws DuplicateUserException {
        // Arrange
        User user = new User("1234567890", "John Doe", "johndoe@example.com");
        when(userService.register(user)).thenThrow(new DuplicateUserException("User with national code already exists"));

        // Act and Assert
        ResponseEntity<ConfirmationCode> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindByNationalCode() throws Exception {
        String nationalCode = "1234567890";
        User user = new User();
        user.setNationalCode(nationalCode);

        when(userService.findByNationalCode(nationalCode)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/national-code/{nationalCode}", nationalCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nationalCode").value(nationalCode));

        verify(userService, times(1)).findByNationalCode(nationalCode);
    }

    @Test
    public void testUpdate() throws Exception {
        Long id = 1L;
        User updatedUser = new User();
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        updatedUser.setNationalCode("1234567890");

        User user = new User();
        user.setId(id);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setNationalCode("0987654321");

        when(userService.update(id, updatedUser)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"nationalCode\":\"1234567890\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Jane"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nationalCode").value("0987654321"));

        verify(userService, times(1)).update(id, updatedUser);
    }

    @Test
    public void testDelete() throws Exception {
        Long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/delete/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(userService, times(1)).delete(id);
    }
}
