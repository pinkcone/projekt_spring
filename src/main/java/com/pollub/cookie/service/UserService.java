package com.pollub.cookie.service;

import com.pollub.cookie.dto.UserDTO;
import com.pollub.cookie.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    void deleteUser(Long id);

    UserDTO getUserByEmail(String email);
}
