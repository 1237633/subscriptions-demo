package net.tgoroshek.subscriptionsdemo.model;

import jakarta.persistence.*;
import lombok.*;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity()
@Table(name = "subscriptions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "owner")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private GenericUser owner;

    private String renewalUrl;

    private String key;

    private LocalDateTime expiresAt;


}
