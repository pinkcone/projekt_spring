package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.UserDTO;
import com.pollub.cookie.dto.UserUpdateDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.UserMapper;
import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.UserRepository;
import com.pollub.cookie.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
    }

    @Test
    void givenValidUserData_whenCreateUser_thenReturnCreatedUser() {
        when(userMapper.mapToEntity(any(UserDTO.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.mapToDTO(any(User.class))).thenReturn(userDTO);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("user@example.com", createdUser.getEmail());
        verify(userValidator).validateEmailUniqueness("user@example.com");
    }

    @Test
    void givenExistingUserId_whenGetUserById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void givenNonExistingUserId_whenGetUserById_thenThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void givenValidUpdateData_whenUpdateUser_thenReturnUpdatedUser() {
        // Given
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToDTO(any(User.class))).thenReturn(userDTO);

        UserDTO updatedUser = userService.updateUser(1L, updateDTO);

        assertNotNull(updatedUser);
        verify(userValidator).validateEmailUniqueness("newemail@example.com");
    }

    @Test
    void givenExistingUserId_whenDeleteUser_thenUserDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}