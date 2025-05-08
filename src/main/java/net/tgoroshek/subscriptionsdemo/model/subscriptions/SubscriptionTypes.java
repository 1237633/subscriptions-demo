package net.tgoroshek.subscriptionsdemo.model.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tgoroshek.subscriptionsdemo.model.Subscription;

@AllArgsConstructor
@Getter
public enum SubscriptionTypes {
    YANDEX(Yandex.class), VK_MUSIC(VkMusic.class), AMAZON(Amazon.class);

    private final Class<? extends Subscription> targetType;


}
