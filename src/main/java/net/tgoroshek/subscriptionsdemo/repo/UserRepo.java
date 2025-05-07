package net.tgoroshek.subscriptionsdemo.repo;

import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface UserRepo extends JpaRepository<GenericUser, String> {
    Optional<GenericUser> findByUsername(String username);

    @PreAuthorize("#username == authentication.name || hasAuthority('DELETE_USERS')")
    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
