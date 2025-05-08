package net.tgoroshek.subscriptionsdemo.service;

import net.tgoroshek.subscriptionsdemo.model.Subscription;
import net.tgoroshek.subscriptionsdemo.model.authorization.Authority;
import net.tgoroshek.subscriptionsdemo.model.authorization.GenericUser;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.SubscriptionTypes;
import net.tgoroshek.subscriptionsdemo.payload.SubscriptionDto;
import net.tgoroshek.subscriptionsdemo.repo.SubscriptionRepo;
import net.tgoroshek.subscriptionsdemo.service.mapper.SubscriptionMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepo subscriptionRepo;
    private final UserService userService;
    private final SubscriptionMapper mapper;
    private final ReentrantReadWriteLock lock;


    //Значения будут не отсортированы, но мы всегда сможем мгновенно положить или достать новую подписку с обновленным количеством, и нам гарантирована уникальность подписок, т.к. сравнение по equals, а не comparator
    private ConcurrentHashMap<SubscriptionTypes, ScoreboardNode> scoreBoard;

    public SubscriptionService(SubscriptionRepo subscriptionRepo, UserService userService, SubscriptionMapper mapper) {
        this.subscriptionRepo = subscriptionRepo;
        this.userService = userService;
        this.mapper = mapper;
        this.lock = new ReentrantReadWriteLock();
        this.scoreBoard = new ConcurrentHashMap<>();

        for (int i = 0; i < SubscriptionTypes.values().length; i++) {
            //Количество подписок каждого типа
            int count = subscriptionRepo.countAllByDiscriminator(SubscriptionTypes.values()[i].name());
            addToScoreBoard(SubscriptionTypes.values()[i], count);
        }
    }


    public Subscription addSubscription(SubscriptionDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        GenericUser subscriber = userService.getUser(username);

        Subscription subscription = mapper.toSubscription(dto);
        subscription.setOwner(subscriber);

        updateScores(dto.getType(), 1);
        return subscriptionRepo.save(subscription);
    }

    public List<String> getTopSubscriptions() {
        lock.readLock().lock();
        try {
            return scoreBoard.values().stream()
                    .sorted(Comparator.reverseOrder())
                    .map(node -> node.subscriptionType.name())
                    .limit(3)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @PreAuthorize("#username == authentication.name or hasAuthority('MODERATE')")
    public List<Subscription> getUsersSubscriptions(String username) {
        return subscriptionRepo.getAllByOwnerUsername(username);
    }

    public void deleteSubscription(String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isModerator = authentication.getAuthorities()
                .stream()
                .map(a -> Authority.AuthoritiesConstants.valueOf(a.getAuthority()))
                .anyMatch(a -> a.equals(Authority.AuthoritiesConstants.MODERATE));

        Subscription subscription = subscriptionRepo.findById(UUID.fromString(id)).orElseThrow(() -> new NoSuchElementException("Подписка не найдена!"));
        SubscriptionTypes type = mapper.getSubType(subscription);

        if (isModerator) {
            subscriptionRepo.delete(subscription);
            updateScores(type, -1);
            return;
        } else {
            if (authentication.getName().equals(subscription.getOwner().getUsername())) {
                subscriptionRepo.delete(subscription);
                updateScores(type, -1);
                return;
            }
        }
        throw new AuthorizationDeniedException("Access denied");
    }

    /**
     * Увеличивает или уменьшает в списке количество подписок выбранного типа на +1 или -1
     *
     * @param type  Тип изменяемой строки списка
     * @param count Можно передать только +1 или -1 (подписка добавилась, или удалилась)
     * @throws IllegalArgumentException в случае передачи неверного аргумента count
     */
    private void updateScores(SubscriptionTypes type, int count) throws IllegalArgumentException {
        if (count != 1 && count != -1) {
            throw new IllegalArgumentException();
        }
        lock.writeLock().lock();
        try {
            int oldCount = scoreBoard.get(type).count;
            //В этом промежутке может произойти запись другим пользователем другого значения в ту же ноду (10 + 1 + 1 = 11) и потокобезопасная коллекция не спасет
            addToScoreBoard(type, oldCount + count);
        } finally {
            lock.writeLock().unlock();
        }

    }

    private void addToScoreBoard(SubscriptionTypes type, int count) {
        scoreBoard.put(type, new ScoreboardNode(type, count));
    }

    private static class ScoreboardNode implements Comparable<ScoreboardNode> {

        public ScoreboardNode(SubscriptionTypes subscriptionType, Integer count) {
            this.subscriptionType = subscriptionType;
            this.count = count;
        }

        private SubscriptionTypes subscriptionType;
        private Integer count;

        @Override
        public int compareTo(ScoreboardNode o) {
            return Integer.compare(count, o.count);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScoreboardNode that = (ScoreboardNode) o;
            return subscriptionType == that.subscriptionType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(subscriptionType);
        }
    }


}
