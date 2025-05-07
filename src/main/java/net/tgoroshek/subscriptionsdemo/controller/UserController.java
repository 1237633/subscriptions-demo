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

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping()
    @JsonView(ResponseSegregation.ShortDetails.class)
    public ResponseEntity<UserRegistrationDto> register(@Validated(RequestTypes.New.class) @RequestBody UserRegistrationDto user) throws IOException {
        return ResponseEntity.ok(userMapper.toRegistrationDto(
                userService.register(user.getUsername(), user.getPassword())));
    }

    @GetMapping()
    public ResponseEntity<UserDto> getUser(@Validated(RequestTypes.Existing.class) @RequestBody UserRegistrationDto user) {
        return ResponseEntity.ok(userMapper.toUserDto(
                userService.getUser(user.getUsername())));
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Validated(RequestTypes.Update.class) @RequestBody UserRegistrationDto userDto) {
        userService.updatePassword(userDto.getOldPassword(), userDto.getPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUSer(@Validated(RequestTypes.Existing.class) @RequestBody UserRegistrationDto userDto) {
        userService.deleteUser(userDto.getUsername());
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<UserDto> updateUser(@Validated(RequestTypes.Update.class) @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userMapper.toUserDto(
                userService.updateUser(userDto)));
    }

}
