package net.tgoroshek.subscriptionsdemo.service.mapper;

import net.tgoroshek.subscriptionsdemo.exception.InvalidDataException;
import net.tgoroshek.subscriptionsdemo.model.Subscription;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.SubscriptionTypes;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.Yandex;
import net.tgoroshek.subscriptionsdemo.payload.SubscriptionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

class SubscriptionMapperTest {
    SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    @Test
    void toSubscriptionMapsToCorrectType() {
        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "MUSIC", "level", "PREMIUM")
        );

        Subscription subscription = subscriptionMapper.toSubscription(dto);

        Assertions.assertInstanceOf(Yandex.class, subscription);
    }

    @Test
    void toSubscriptionMapsAllFields() {
        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "MUSIC", "level", "PREMIUM")
        );

        Yandex subscription = subscriptionMapper.toSubscription(dto);

        Assertions.assertEquals(subscription.getLevel(), Yandex.Level.PREMIUM);
        Assertions.assertEquals(subscription.getType(), Yandex.Type.MUSIC);
    }

    @Test
    void toSubscriptionThrowsOnIncorrectData() {
        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "Helicopter", "level", "PREMIUM")
        );

        Assertions.assertThrows(InvalidDataException.class, () -> subscriptionMapper.toSubscription(dto));

        SubscriptionDto anotherDto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("speed", "MUSIC", "level", "PREMIUM")
        );

        Assertions.assertThrows(InvalidDataException.class, () -> subscriptionMapper.toSubscription(anotherDto));
    }

    @Test
    void toDto() {
        GenericUser user = new GenericUser();
        user.setUsername("user");

        Yandex subscription = new Yandex(Yandex.Level.PREMIUM, Yandex.Type.GO);
        subscription.setOwner(user);
        subscription.setId(UUID.randomUUID());
        subscription.setKey("SDADS");
        subscription.setRenewalUrl("SDADS");
        subscription.setExpiresAt(LocalDateTime.now().minusDays(100));

        SubscriptionDto dto = subscriptionMapper.toDto(subscription);

        Assertions.assertEquals(dto.getUuid(), subscription.getId().toString());
        Assertions.assertEquals(dto.getType().toString(), "YANDEX");
        Assertions.assertEquals(dto.getUniqueParams().get("level"), "PREMIUM");
        Assertions.assertEquals(dto.getUniqueParams().get("type"), "GO");

    }

    @Test
    void toDtoWithoutField() {
        GenericUser user = new GenericUser();
        user.setUsername("user");

        Yandex subscription = new Yandex(Yandex.Level.PREMIUM, Yandex.Type.GO);
        subscription.setType(null);
        subscription.setOwner(user);
        subscription.setId(UUID.randomUUID());
        subscription.setKey("SDADS");
        subscription.setRenewalUrl("SDADS");
        subscription.setExpiresAt(LocalDateTime.now().minusDays(100));

        SubscriptionDto dto = subscriptionMapper.toDto(subscription);

        Assertions.assertEquals(dto.getUuid(), subscription.getId().toString());
        Assertions.assertEquals(dto.getType().toString(), "YANDEX");
        Assertions.assertEquals(dto.getUniqueParams().get("level"), "PREMIUM");
    }

}