package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.UserDTO;
import com.pollub.cookie.dto.UserUpdateDTO;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.UserMapper;
import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.UserRepository;
import com.pollub.cookie.service.UserService;
import com.pollub.cookie.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        userValidator.validateEmailUniqueness(userDTO.getEmail());
        User user = userMapper.mapToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userMapper.mapRoleStringToEnum(userDTO.getRole() != null ? userDTO.getRole() : "USER"));
        return userMapper.mapToDTO(userRepository.save(user));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono użytkownika o ID: " + id));
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userMapper.mapToDTO(findUserById(id));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = findUserById(id);
        if (userUpdateDTO.getEmail() != null ) {
            if(!userUpdateDTO.getEmail().equals(user.getEmail())) {
                userValidator.validateEmailUniqueness(userUpdateDTO.getEmail());
                user.setEmail(userUpdateDTO.getEmail());
            }
        }
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }
        if (userUpdateDTO.getFirstName() != null) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null) user.setLastName(userUpdateDTO.getLastName());
        if (userUpdateDTO.getAddress() != null) user.setAddress(userUpdateDTO.getAddress());
        if (userUpdateDTO.getPhoneNumber() != null) user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        return userMapper.mapToDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(findUserById(id));
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie znaleziony o email: " + email));

        return userMapper.mapToDTO(user);
    }
}
