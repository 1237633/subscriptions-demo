package net.tgoroshek.subscriptionsdemo.service;

import net.tgoroshek.subscriptionsdemo.config.TestConfig;
import net.tgoroshek.subscriptionsdemo.model.Subscription;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.SubscriptionTypes;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.Yandex;
import net.tgoroshek.subscriptionsdemo.payload.SubscriptionDto;
import net.tgoroshek.subscriptionsdemo.repo.SubscriptionRepo;
import net.tgoroshek.subscriptionsdemo.service.mapper.SubscriptionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest(classes = {SubscriptionService.class, SubscriptionMapper.class})
@Import(TestConfig.class)
class SubscriptionServiceTest {

    @MockitoBean
    SubscriptionRepo subscriptionRepo;
    @MockitoBean
    UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    @WithMockUser("user")
    void addSubscription() {

        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "MUSIC", "level", "PREMIUM")
        );

        GenericUser user = new GenericUser();
        user.setUsername("user");
        Mockito.when(userService.getUser(Mockito.any())).thenReturn(user);

        ArgumentCaptor<Yandex> captor = ArgumentCaptor.forClass(Yandex.class);

        subscriptionService.addSubscription(dto);
        Mockito.verify(subscriptionRepo, Mockito.atLeastOnce()).save(captor.capture());

        Yandex subscription = captor.getValue();

        Assertions.assertEquals(subscription.getOwner().getUsername(), "user");

    }

    @Test
    @WithMockUser("user2")
    void getTop() {
        int typeNo;
        SecureRandom random = new SecureRandom();
        Map<SubscriptionTypes, Integer> exactCounts = new HashMap<>();
        GenericUser user = new GenericUser();
        user.setUsername("user2");

        for (int i = 0; i < 58; i++) {

            typeNo = random.nextInt(SubscriptionTypes.values().length);

            SubscriptionTypes type = SubscriptionTypes.values()[typeNo];

            if (!exactCounts.containsKey(type)) {
                exactCounts.put(type, 1);
            } else {
                int currCount = exactCounts.get(type);
                exactCounts.put(type, currCount + 1);
            }

            SubscriptionDto dto = new SubscriptionDto(
                    null,
                    type,
                    "www.com/upd",
                    UUID.randomUUID().toString(),
                    LocalDateTime.now().plusHours(10),
                    Map.of()
            );

            Mockito.when(userService.getUser(Mockito.any())).thenReturn(user);
            subscriptionService.addSubscription(dto);
        }
        List<String> topSubs = subscriptionService.getTopSubscriptions();

        boolean isOrdered = true;

        for (int i = 0; i < topSubs.size(); i++) {
            SubscriptionTypes currType = SubscriptionTypes.valueOf(topSubs.get(i));
            int countForSub = exactCounts.get(currType);
            int maxForAll = exactCounts.values().stream().max(Integer::compareTo).get();

            if (countForSub >= maxForAll) {
                exactCounts.remove(currType);
            } else {
                isOrdered = false;
                break;
            }
        }

        Assertions.assertTrue(isOrdered);

    }

    @Test
    @WithMockUser("user")
    void getUsersSubs() {
        String USERNAME = "user";
        int typeNo;
        SecureRandom random = new SecureRandom();
        List<Subscription> subscriptions = new ArrayList<>();
        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        //Складываем все в лист, вместо БД
        Mockito.when(subscriptionRepo.save(captor.capture())).thenAnswer((Answer<Void>) invocation -> {
            subscriptions.add(captor.getValue());
            return null;
        });

        GenericUser user = new GenericUser();
        user.setUsername("user");
        Mockito.when(userService.getUser(Mockito.any())).thenReturn(user);

        int subsCount = 10;
        for (int i = 0; i < subsCount; i++) {

            typeNo = random.nextInt(SubscriptionTypes.values().length);

            SubscriptionTypes type = SubscriptionTypes.values()[typeNo];

            SubscriptionDto dto = new SubscriptionDto(
                    null,
                    type,
                    "www.com/upd",
                    UUID.randomUUID().toString(),
                    LocalDateTime.now().plusHours(10),
                    Map.of()
            );

            subscriptionService.addSubscription(dto);
        }

        // Mockito.when(subscriptionRepo.getAllByOwnerUsername(Mockito.anyString())).;

        subscriptionService.getUsersSubscriptions(USERNAME); //Результат будет пуст, поскольку репозиторий - мок и в БД ничего не пишем
        Mockito.verify(subscriptionRepo, Mockito.atLeastOnce()).getAllByOwnerUsername(USERNAME);

        boolean ownerMatch = true;

        for (int i = 0; i < subscriptions.size(); i++) {
            if (!subscriptions.get(i).getOwner().getUsername().equals(USERNAME)) {
                ownerMatch = false;
                break;
            }
        }

        Assertions.assertTrue(ownerMatch);
        Assertions.assertEquals(subsCount, subscriptions.size());

    }

    @Test
    @WithMockUser(value = "user", authorities = {"READ"})
    void deleteSubscriptionWrongUser() {
        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "MUSIC", "level", "PREMIUM")
        );

        Subscription subscription = new Subscription();
        GenericUser owner = new GenericUser();
        GenericUser user = new GenericUser();

        owner.setUsername("resu");
        user.setUsername("user");

        subscription.setOwner(owner);
        subscription.setId(UUID.randomUUID());

        Mockito.when(userService.getUser(Mockito.any())).thenReturn(user);
        subscriptionService.addSubscription(dto);

        Mockito.when(subscriptionRepo.findById(Mockito.any())).thenReturn(Optional.of(subscription));
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> subscriptionService.deleteSubscription(subscription.getId().toString()));
        //Mockito.verify(subscriptionRepo, Mockito.atLeastOnce()).delete(Mockito.any());
    }

    @Test
    @WithMockUser(value = "user", authorities = {"READ"})
    void deleteSubscription() {
        SubscriptionDto dto = new SubscriptionDto(
                null,
                SubscriptionTypes.YANDEX,
                "ya.ry/upd",
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(10),
                Map.of("type", "MUSIC", "level", "PREMIUM")
        );

        Subscription subscription = new Yandex();
        GenericUser owner = new GenericUser();
        owner.setUsername("user");

        subscription.setOwner(owner);
        subscription.setId(UUID.randomUUID());

        Mockito.when(userService.getUser(Mockito.any())).thenReturn(owner);
        subscriptionService.addSubscription(dto);


        Mockito.when(subscriptionRepo.findById(Mockito.any())).thenReturn(Optional.of(subscription));

        subscriptionService.deleteSubscription(subscription.getId().toString());
        Mockito.verify(subscriptionRepo, Mockito.atLeastOnce()).delete(Mockito.any());
    }
}