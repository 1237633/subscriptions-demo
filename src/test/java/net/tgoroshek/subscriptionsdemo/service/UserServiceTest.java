package net.tgoroshek.subscriptionsdemo.service;

import net.tgoroshek.subscriptionsdemo.config.TestConfig;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.payload.UserRegistrationDto;
import net.tgoroshek.subscriptionsdemo.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.util.Optional;


@SpringBootTest(classes = {PasswordEncoder.class, InMemoryUserDetailsManager.class, UserService.class})
@Import(TestConfig.class)
class UserServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @MockitoSpyBean
    private UserDetailsManager userDetailsManager;

    @MockitoBean
    private UserRepo userRepo;

    @Autowired
    @InjectMocks
    private UserService userService;

    @Test
    void register() throws IOException {
        UserRegistrationDto dto = new UserRegistrationDto();

        dto.setUsername("username1");
        dto.setPassword("password");

        Mockito.when(userRepo.findByUsername(Mockito.any())).thenReturn(Optional.of(new GenericUser()));

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
    void updatePassword() throws IOException {

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
    @WithMockUser("username4")
    void deleteUser() throws IOException {

        String USERNAME = "username4";

        Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(Optional.of(new GenericUser()));
        userService.register(USERNAME, "password");
        Assertions.assertTrue(userDetailsManager.userExists(USERNAME));

        userService.deleteUser(USERNAME);
        Mockito.verify(userDetailsManager, Mockito.atLeastOnce()).deleteUser(USERNAME);
        Assertions.assertFalse(userDetailsManager.userExists(USERNAME));
    }

    @Test
    @WithAnonymousUser
    void deleteUserUnauthorized() throws IOException {

        String USERNAME = "username5";

        Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(Optional.of(new GenericUser()));
        userService.register(USERNAME, "password");
        Assertions.assertTrue(userDetailsManager.userExists(USERNAME));

        Assertions.assertThrows(AuthorizationDeniedException.class, () -> userService.deleteUser(USERNAME));

        Mockito.verify(userDetailsManager, Mockito.never()).deleteUser(USERNAME);
        Assertions.assertTrue(userDetailsManager.userExists(USERNAME));
    }

    //todo UpdUserTest

}