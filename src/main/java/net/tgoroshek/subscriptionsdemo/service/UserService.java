package net.tgoroshek.subscriptionsdemo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tgoroshek.subscriptionsdemo.exception.InvalidDataException;
import net.tgoroshek.subscriptionsdemo.exception.UserAlreadyExistsException;
import net.tgoroshek.subscriptionsdemo.model.authorization.Authority;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.payload.UserDto;
import net.tgoroshek.subscriptionsdemo.repo.UserRepo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Transactional
    public GenericUser register(String username, String password) {

        UserDetails newUser = User
                .withUsername(username)
                .password(passwordEncoder.encode(password))
                .disabled(false)
                .authorities(Authority.AuthoritiesConstants.READ.name())
                .build();
        try {
            userDetailsManager.createUser(newUser);
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage());
            throw new UserAlreadyExistsException(e);
        }

        return userRepo.findByUsername(username).orElseThrow(() -> new NoSuchElementException(username + " не найден."));
    }

    public GenericUser getUser(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new NoSuchElementException(username + " не найден."));
    }

    public void updatePassword(String oldPass, String newPass) {
        userDetailsManager.changePassword(oldPass, passwordEncoder.encode(newPass));
    }

    @PreAuthorize("#username == authentication.name or hasAuthority('DELETE_USERS')")
    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }

    @PreAuthorize("#username == authentication.name")
    @Transactional
    public GenericUser updateUser(UserDto userDto, String username) {

        GenericUser user = userRepo.findByUsername(username).orElseThrow(() -> new NoSuchElementException(username + " не найден."));

        user.setAge(userDto.getAge());
        try {
            user.setGender(GenericUser.Gender.valueOf(userDto.getGender()));
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Неверно указан пол");
        }
        user.setEmail(userDto.getEmail());

        return userRepo.save(user);

    }

}
