package com.example.application.services;

import com.example.application.entity.Role;
import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User registerUser(String email, String password, String firstName, String lastName) {
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email jest już zajęty");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setHashedPassword(passwordEncoder.encode(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setRoles(Collections.singleton(Role.USER));

        // Ustaw domyślny avatar
        newUser.setProfilePicture(getDefaultAvatar());

        return userRepository.save(newUser);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    private byte[] getDefaultAvatar() {
        try (InputStream is = getClass().getResourceAsStream("/META-INF/resources/icons/default-avatar.png")) {
            if (is != null) {
                return is.readAllBytes();
            }
        } catch (IOException e) {
            // Ignore
        }
        return new byte[0];
    }
}