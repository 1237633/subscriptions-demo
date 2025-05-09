package net.tgoroshek.subscriptionsdemo.model.authorization;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericUser {

    @Id
    private String username;

    private String password;

    private boolean enabled;
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Authority> authorities;

    private Short age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String email;


    public enum Gender{
        MALE, FEMALE
    }


}
