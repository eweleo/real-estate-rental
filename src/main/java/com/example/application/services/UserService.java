package com.example.application.services;

import com.example.application.entity.Address;
import com.example.application.entity.Company;
import com.example.application.entity.Role;
import com.example.application.entity.User;
import com.example.application.repository.CompanyRepository;
import com.example.application.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       CompanyRepository companyRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean nipExists(String nip) {
        return companyRepository.findByNip(nip).isPresent();
    }

    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }

    private byte[] getDefaultAvatar() {
        return new byte[0];
    }

    @Transactional
    public void registerUser(String email, String password, String firstName, String lastName) {
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email już istnieje");
        }

        User user = new User();
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Set.of(Role.USER));
        user.setProfilePicture(getDefaultAvatar());

        userRepository.save(user);
    }

    @Transactional
    public void registerLandlord(String email, String password, String firstName, String lastName,
                                 String phone, String companyName, String nip,
                                 String street, String city, String zipCode) {

        if (emailExists(email)) {
            throw new IllegalArgumentException("Email już istnieje");
        }

        if (nipExists(nip)) {
            throw new IllegalArgumentException("NIP już istnieje w systemie");
        }

        User user = new User();
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTelephoneNumber(phone);
        user.setRoles(Set.of(Role.USER, Role.LANDLORD));
        user.setProfilePicture(getDefaultAvatar());

        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountry("Polska");
        user.setAddress(address);

        Company company = new Company();
        company.setCompanyName(companyName);
        company.setNip(nip);
        company.setUser(user);

        user.setCompany(company);

        userRepository.save(user);
    }


    @Transactional
    public void registerLandlord(String email, String companyName, String nip,
                                 String street, String city, String zipCode) {

        User user = findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Użytkownik nie został znaleziony"));

        if (user.isLandlord()) {
            throw new IllegalArgumentException("To konto jest już zarejestrowane jako wynajmujący");
        }

        if (nipExists(nip)) {
            throw new IllegalArgumentException("NIP już istnieje w systemie");
        }

        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(Role.LANDLORD);
        user.setRoles(roles);

        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setStreet(street);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountry("Polska");
        user.setAddress(address);

        Company company = new Company();
        company.setCompanyName(companyName);
        company.setNip(nip);
        company.setUser(user);

        user.setCompany(company);

        userRepository.save(user);
    }

    @Transactional
    public void registerLandlord(String email, String password, String companyName, String nip,
                                 String street, String city, String zipCode) {

        User user = findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Użytkownik nie został znaleziony"));

        if (user.isLandlord()) {
            throw new IllegalArgumentException("To konto jest już zarejestrowane jako wynajmujący");
        }

        if (nipExists(nip)) {
            throw new IllegalArgumentException("NIP już istnieje w systemie");
        }

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new IllegalArgumentException("Nieprawidłowe hasło");
        }

        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(Role.LANDLORD);
        user.setRoles(roles);

        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
        }
        address.setStreet(street);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountry("Polska");
        user.setAddress(address);

        Company company = new Company();
        company.setCompanyName(companyName);
        company.setNip(nip);
        company.setUser(user);

        user.setCompany(company);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getHashedPassword());
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}