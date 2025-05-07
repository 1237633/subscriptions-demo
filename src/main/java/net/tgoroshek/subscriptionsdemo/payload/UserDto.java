package net.tgoroshek.subscriptionsdemo.payload;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.RequestTypes;


import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String username;

    @Null(groups = {RequestTypes.Default.class}, message = "Поле не поддерживает редактирование")
    private boolean enabled;

    @Null(groups = {RequestTypes.Default.class}, message = "Поле не поддерживает редактирование")
    private Set<String> authorities;

    private String age;

    private String gender;

    private String email;

}
