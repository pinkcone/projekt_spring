package com.pollub.cookie.repository;

import com.pollub.cookie.model.Role;
import com.pollub.cookie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}
