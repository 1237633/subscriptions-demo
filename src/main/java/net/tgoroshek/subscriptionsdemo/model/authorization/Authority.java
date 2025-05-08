/*
package net.tgoroshek.subscriptionsdemo.model.authentication;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private AuthoritiesConstants name;
    @ManyToMany(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<GenericUser> users;

    public Authority(AuthoritiesConstants name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public Authority(GrantedAuthority grantedAuthority) {
        this.name = AuthoritiesConstants.valueOf(grantedAuthority.getAuthority());
        this.users = new ArrayList<>();

    }

    public void addUser(GenericUser user) {
        users.add(user);
    }

    public enum AuthoritiesConstants {
        READ, WRITE, EDIT, DELETE, EDIT_AUTHORITIES, DISABLE_USERS
    }
}
*/

package net.tgoroshek.subscriptionsdemo.model.authorization;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(of = "name")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "authority")
    private AuthoritiesConstants name;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GenericUser user;

    public Authority(AuthoritiesConstants name) {
        this.name = name;
    }

    public Authority(GrantedAuthority grantedAuthority) {
        this.name = AuthoritiesConstants.valueOf(grantedAuthority.getAuthority());

    }

    public enum AuthoritiesConstants {
        READ, WRITE, EDIT, DELETE, EDIT_AUTHORITIES, DISABLE_USERS, DELETE_USERS, MODERATE
    }
}

