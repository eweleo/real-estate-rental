package com.example.application.security;

import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        System.out.println("AuthenticatedUser created - UserRepository: " + (userRepository != null ? "OK" : "NULL"));
    }

    @Transactional
    public Optional<User> get() {
        System.out.println("AuthenticatedUser.get() called");
        System.out.println("UserRepository is: " + (userRepository != null ? "OK" : "NULL"));

        Optional<UserDetails> userDetailsOpt = authenticationContext.getAuthenticatedUser(UserDetails.class);
        System.out.println("UserDetails present: " + userDetailsOpt.isPresent());

        if (userDetailsOpt.isPresent()) {
            String email = userDetailsOpt.get().getUsername();
            System.out.println("Email from UserDetails: " + email);

            User user = userRepository.findByEmail(email);
            System.out.println("User found: " + (user != null ? user.getEmail() : "NULL"));

            return Optional.ofNullable(user);
        }

        return Optional.empty();
    }

    public void logout() {
        authenticationContext.logout();
    }
}