package net.tgoroshek.subscriptionsdemo.model.subscriptions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.tgoroshek.subscriptionsdemo.model.Subscription;

@Entity()
@DiscriminatorValue("VK_MUSIC")
@Getter
@Setter
@ToString(callSuper = true)
public class VkMusic extends Subscription {

}
