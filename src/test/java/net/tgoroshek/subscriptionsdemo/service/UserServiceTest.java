package net.tgoroshek.subscriptionsdemo.service;

import net.tgoroshek.subscriptionsdemo.config.UserServiceTestConfig;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.payload.UserRegistrationDto;
import net.tgoroshek.subscriptionsdemo.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
@Import(UserServiceTestConfig.class)
class UserServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Test
    void register() throws IOException {
        UserRegistrationDto dto = new UserRegistrationDto();

        dto.setUsername("username1");
        dto.setPassword("password");

        //  Mockito.when(userRepo.findByUsername(Mockito.any())).thenReturn(Optional.of(new GenericUser()));

        Assertions.assertInstanceOf(GenericUser.class, userService.register(dto.getUsername(), dto.getPassword()));
    }

    @Test
    void registerExistingUser() throws IOException {
        UserRegistrationDto dto = new UserRegistrationDto();

        dto.setUsername("username2");
        dto.setPassword("password");

        Mockito.when(userRepo.findByUsername(Mockito.any())).thenReturn(Optional.of(new GenericUser()));
        userService.register(dto.getUsername(), dto.getPassword());

        Assertions.assertThrows(Exception.class, () -> userService.register(dto.getUsername(), dto.getPassword())); //Разные реализации UDM кидают разные исключения, поскольку мы используем InMemory реализацию в тестах исключение не совпадает
    }

    @Test
    @WithMockUser(username = "username3")
    void updatePasswordFromAnotherUser() throws IOException {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setUsername("username3");
        userDto.setPassword("password");
        Mockito.when(userRepo.findByUsername(userDto.getUsername())).thenReturn(Optional.of(new GenericUser()));
        userService.register(userDto.getUsername(), userDto.getPassword());

        userDto.setPassword("newPass");
        userService.updatePassword(userDto.getOldPassword(), userDto.getPassword());

        UserDetails userDetails = userDetailsManager.loadUserByUsername(userDto.getUsername());

        Assertions.assertTrue(passwordEncoder.matches(userDto.getPassword(), userDetails.getPassword()));
    }

    @Test
    void deleteUser() throws IOException {
        userService.deleteUser("Username");
        Mockito.verify(userRepo, Mockito.atLeastOnce()).deleteByUsername(Mockito.any());
    }

}