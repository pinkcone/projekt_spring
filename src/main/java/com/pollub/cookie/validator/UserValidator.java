package com.pollub.cookie.validator;

import com.pollub.cookie.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email już istnieje: " + email);
        }
    }

}
