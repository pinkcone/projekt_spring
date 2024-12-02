package com.pollub.cookie.service.impl;

import com.pollub.cookie.model.User;
import com.pollub.cookie.repository.UserRepository;
import com.pollub.cookie.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Ładowanie użytkownika o emailu: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Nie znaleziono użytkownika o emailu: {}", email);
                    return new UsernameNotFoundException("Nie znaleziono użytkownika o emailu: " + email);
                });

        return new CustomUserDetails(user);
    }
}
