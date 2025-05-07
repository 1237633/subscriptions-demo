package net.tgoroshek.subscriptionsdemo.service.mapper;

import lombok.extern.slf4j.Slf4j;
import net.tgoroshek.subscriptionsdemo.exception.InvalidDataException;
import net.tgoroshek.subscriptionsdemo.model.authorization.Authority;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.payload.UserDto;
import net.tgoroshek.subscriptionsdemo.payload.UserRegistrationDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserMapper {

    public UserRegistrationDto toRegistrationDto(GenericUser user) {

        return new UserRegistrationDto(
                user.getUsername(),
                user.getPassword(),
                mapAuthoritiesSet(user.getAuthorities())
        );
    }

    public UserDto toUserDto(GenericUser user) {

        return new UserDto(
                user.getUsername(),
                user.isEnabled(),
                mapAuthoritiesSet(user.getAuthorities()),
                user.getAge(),
                user.getGender() == null ? null : user.getGender().name(),
                user.getEmail()
        );
    }

    private Set<String> mapAuthoritiesSet(Set<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            log.error("Пользователь не валиден. Отстутствуют авторизованные действия");
            throw new InvalidDataException("Пользователь не валиден");
        }

        return authorities
                .stream().map(authority -> authority.getName().name())
                .collect(Collectors.toSet());
    }

}
