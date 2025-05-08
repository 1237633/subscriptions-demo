package net.tgoroshek.subscriptionsdemo.service.mapper;

import lombok.extern.slf4j.Slf4j;
import net.tgoroshek.subscriptionsdemo.exception.InvalidDataException;
import net.tgoroshek.subscriptionsdemo.model.Subscription;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.SubscriptionTypes;
import net.tgoroshek.subscriptionsdemo.payload.SubscriptionDto;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SubscriptionMapper {

    private static Map<Class, SubscriptionTypes> typesMap = new HashMap<>();

    public SubscriptionMapper() {
        for (SubscriptionTypes t : SubscriptionTypes.values()) {
            typesMap.put(t.getTargetType(), t);
        }
    }

    public <T extends Subscription> T toSubscription(SubscriptionDto dto) {

        try {
            Class<? extends Subscription> type = dto.getType().getTargetType();
            T subscription = (T) type.getDeclaredConstructor().newInstance();

            subscription.setExpiresAt(dto.getExpiresAt());
            subscription.setKey(dto.getKey());
            subscription.setRenewalUrl(dto.getRenewalUrl());

            for (String v : dto.getUniqueParams().keySet()) {
                Field field = subscription.getClass().getDeclaredField(v);
                field.setAccessible(true);

                field.set(subscription,
                        mapParameter(dto.getUniqueParams().get(v), field));
            }

            return subscription;

        } catch (ReflectiveOperationException e) {
            log.error("Ошибка маппинга уникальных полей подписки", e);
            throw new InvalidDataException("Неверные детали подтипа подписки");

        }
    }

    public <T extends Subscription> SubscriptionDto toDto(T subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setExpiresAt(subscription.getExpiresAt());
        dto.setKey(subscription.getKey());
        dto.setRenewalUrl(subscription.getRenewalUrl());
        dto.setUuid(subscription.getId().toString());
        dto.setType(typesMap.get(subscription.getClass()));

        Map<String, String> uniqueFields = new HashMap<>();

        for (Field f : subscription.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object o = f.get(subscription);
                if (o == null) {
                    continue;
                }
                uniqueFields.put(f.getName(), o.toString());
            } catch (IllegalAccessException e) {
                log.error("Ошибка маппинга уникальных полей подписки", e);
                throw new InvalidDataException("Ошибка получения деталей подписки");
            }
        }

        dto.setUniqueParams(uniqueFields);

        return dto;
    }

    public List<SubscriptionDto> toDtoList(List<Subscription> subscriptions) {
        return subscriptions
                .stream()
                .map(s -> toDto(s))
                .toList();
    }

    public <T extends  Subscription> SubscriptionTypes getSubType(T subscription) {
        return typesMap.get(subscription.getClass());
    }


    private <T> T mapParameter(String param, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        return (T) field.getType()
                .getMethod("valueOf", String.class)
                .invoke(field, param);
    }
}
