package net.tgoroshek.subscriptionsdemo.repo;

import net.tgoroshek.subscriptionsdemo.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepo extends JpaRepository<Subscription, UUID> {
    @Query(nativeQuery = true, value = "SELECT count(s) FROM subscriptions s WHERE s.dtype = :discriminator")
    Integer countAllByDiscriminator(@Param("discriminator") String discriminator);

    @Query("SELECT s FROM Subscription s WHERE s.owner.username = :username")
    List<Subscription> getAllByOwnerUsername(String username);

}
