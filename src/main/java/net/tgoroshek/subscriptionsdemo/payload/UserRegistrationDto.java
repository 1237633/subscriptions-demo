package net.tgoroshek.subscriptionsdemo.payload;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.RequestTypes;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.ResponseSegregation;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {

    public UserRegistrationDto(String username, String password, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @JsonView({ResponseSegregation.ShortDetails.class})
    @NotNull(groups = {RequestTypes.Default.class})
    private String username;

    @JsonView({ResponseSegregation.NeverShown.class})
    @NotNull(groups = {RequestTypes.New.class, RequestTypes.Update.class})
    @Null(groups = {RequestTypes.Existing.class}, message = "Поле должно использоваться только при регистрации и изменении")
    private String password;

    @JsonView({ResponseSegregation.NeverShown.class})
    @NotNull(groups = {RequestTypes.Update.class})
    @Null(groups = {RequestTypes.Existing.class, RequestTypes.New.class}, message = "Поле должно использоваться только при изменении")
    private String oldPassword;

    @JsonView({ResponseSegregation.ShortDetails.class})
    @Null(groups = {RequestTypes.Default.class}, message = "Поле не должно использоваться")
    private Set<String> authorities;
}
