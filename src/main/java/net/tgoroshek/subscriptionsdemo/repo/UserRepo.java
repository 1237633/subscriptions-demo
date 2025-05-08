package net.tgoroshek.subscriptionsdemo.repo;

import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<GenericUser, String> {
    Optional<GenericUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
