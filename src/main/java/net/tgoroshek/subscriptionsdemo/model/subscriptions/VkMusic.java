package net.tgoroshek.subscriptionsdemo.model.subscriptions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import net.tgoroshek.subscriptionsdemo.model.Subscription;

@Entity()
@DiscriminatorValue("VK_MUSIC")
@Getter
@Setter
public class VkMusic extends Subscription {

}
