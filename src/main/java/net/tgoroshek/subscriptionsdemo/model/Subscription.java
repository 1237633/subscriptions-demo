package net.tgoroshek.subscriptionsdemo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity()
@Table(name = "subscriptions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String renewalUrl;
}
