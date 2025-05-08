package net.tgoroshek.subscriptionsdemo.model.subscriptions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import net.tgoroshek.subscriptionsdemo.model.Subscription;

@Entity()
@DiscriminatorValue("YANDEX")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString(callSuper = true)
public class Yandex extends Subscription {

    private Level level;
    private Type type;

    public enum Type{
        MUSIC, VIDEO, DELIVERY, GO
    }

    public enum Level{
        PREMIUM, STANDARD, ENTRY
    }
}
