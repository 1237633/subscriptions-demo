package net.tgoroshek.subscriptionsdemo.model.subscriptions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tgoroshek.subscriptionsdemo.model.Subscription;

@Entity()
@DiscriminatorValue("YANDEX")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Yandex extends Subscription {

    private Level level;
    private Type type;

    private enum Type{
        MUSIC, VIDEO, DELIVERY, GO
    }

    private enum Level{
        PREMIUM, STANDARD, ENTRY
    }
}
