package net.tgoroshek.subscriptionsdemo.service;

import net.tgoroshek.subscriptionsdemo.config.TestConfig;
import net.tgoroshek.subscriptionsdemo.exception.InvalidDataException;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.payload.UserDto;
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
    void register(){
        UserRegistrationDto dto = new UserRegistrationDto();

        dto.setUsername("username1");
        dto.setPassword("password");

        Mockito.when(userRepo.findByUsername(Mockito.any())).thenReturn(Optional.of(new GenericUser()));

        Assertions.assertInstanceOf(GenericUser.class, userService.register(dto.getUsername(), dto.getPassword()));
    }

    @Test
    void registerExistingUser() {
        UserRegistrationDto dto = new UserRegistrationDto();

        dto.setUsername("username2");
        dto.setPassword("password");

        Mockito.when(userRepo.findByUsername(Mockito.any())).thenReturn(Optional.of(new GenericUser()));
        userService.register(dto.getUsername(), dto.getPassword());

        Assertions.assertThrows(Exception.class, () -> userService.register(dto.getUsername(), dto.getPassword())); //Разные реализации UDM кидают разные исключения, поскольку мы используем InMemory реализацию в тестах исключение не совпадает
    }

    @Test
    @WithMockUser(username = "username3")
    void updatePassword(){

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
    void deleteUser() {

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
    void deleteUserUnauthorized() {

        String USERNAME = "username5";

        Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(Optional.of(new GenericUser()));
        userService.register(USERNAME, "password");
        Assertions.assertTrue(userDetailsManager.userExists(USERNAME));

        Assertions.assertThrows(AuthorizationDeniedException.class, () -> userService.deleteUser(USERNAME));

        Mockito.verify(userDetailsManager, Mockito.never()).deleteUser(USERNAME);
        Assertions.assertTrue(userDetailsManager.userExists(USERNAME));
    }

    @Test
    @WithMockUser("username5")
    void updUser() {

        UserDto dto = new UserDto();
        dto.setAge((short) 21);
        dto.setEmail("a@b.c");
        dto.setGender("MALE");

        String USERNAME = "username5";

        GenericUser user = new GenericUser();
        user.setUsername(USERNAME);

        Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        Mockito.when(userRepo.save(Mockito.any())).thenReturn(user);
        GenericUser updUser = userService.updateUser(dto, USERNAME);

        Assertions.assertEquals(updUser.getAge(), dto.getAge());
        Assertions.assertEquals(updUser.getGender().toString(), dto.getGender().toString());
        Assertions.assertEquals(updUser.getEmail(), dto.getEmail());
    }

    @Test
    @WithMockUser("username6")
    void updUserWithWrongData() {

        UserDto dto = new UserDto();
        dto.setAge((short) 21);
        dto.setEmail("a@b.c");
        dto.setGender("Helicopter");

        String USERNAME = "username6";
        Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(Optional.of(new GenericUser()));
        Assertions.assertThrows(InvalidDataException.class, () -> userService.updateUser(dto, USERNAME));
    }

    @Test
    @WithAnonymousUser
    void updUserUnauthorized() {
        UserDto dto = new UserDto();
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> userService.updateUser(dto, "USERNAME"));
    }


}