package net.tgoroshek.subscriptionsdemo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import net.tgoroshek.subscriptionsdemo.payload.UserDto;
import net.tgoroshek.subscriptionsdemo.payload.UserRegistrationDto;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.RequestTypes;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.ResponseSegregation;
import net.tgoroshek.subscriptionsdemo.service.UserService;
import net.tgoroshek.subscriptionsdemo.service.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping(Router.Users.REGISTER)
    @JsonView(ResponseSegregation.ShortDetails.class)
    public ResponseEntity<UserRegistrationDto> register(@Validated(RequestTypes.New.class) @RequestBody UserRegistrationDto user) {
        return ResponseEntity.ok(userMapper.toRegistrationDto(
                userService.register(user.getUsername(), user.getPassword())));
    }

    @GetMapping(Router.Users.BY_USERNAME)
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userMapper.toUserDto(
                userService.getUser(username)));
    }

    @PutMapping(Router.Users.PASSWORD)
    public ResponseEntity<?> updatePassword(@Validated(RequestTypes.Update.class) @RequestBody UserRegistrationDto userDto) {
        userService.updatePassword(userDto.getOldPassword(), userDto.getPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(Router.Users.BY_USERNAME)
    public ResponseEntity<?> deleteUSer(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping(Router.Users.BY_USERNAME)
    public ResponseEntity<UserDto> updateUser(
            @Validated(RequestTypes.Update.class) @RequestBody UserDto userDto,
            @PathVariable String username) {
        return ResponseEntity.ok(userMapper.toUserDto(
                userService.updateUser(userDto, username)));
    }

}
