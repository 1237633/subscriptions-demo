package net.tgoroshek.subscriptionsdemo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import net.tgoroshek.subscriptionsdemo.payload.SubscriptionDto;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.RequestTypes;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.ResponseSegregation;
import net.tgoroshek.subscriptionsdemo.service.SubscriptionService;
import net.tgoroshek.subscriptionsdemo.service.mapper.SubscriptionMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper mapper;

    @JsonView(ResponseSegregation.Details.class)
    @PostMapping(Router.Subscriptions.ROOT)
    public ResponseEntity<SubscriptionDto> newSubscription(@Validated(RequestTypes.New.class) @RequestBody SubscriptionDto dto) {
        return ResponseEntity.ok(
                mapper.toDto(
                        subscriptionService.addSubscription(dto)));
    }

    @GetMapping(Router.Subscriptions.USER_WISE)
    @JsonView(ResponseSegregation.Details.class)
    public ResponseEntity<List<SubscriptionDto>> getUsersSubs(@PathVariable String username) {

        return ResponseEntity.ok(
                mapper.toDtoList(
                        subscriptionService.getUsersSubscriptions(username)));
    }

    @DeleteMapping(Router.Subscriptions.BY_ID)
    public ResponseEntity<?> delete(@PathVariable String id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(Router.Subscriptions.TOP)
    public ResponseEntity<List<String>> getTopSubs() {
        return ResponseEntity.ok(
                subscriptionService.getTopSubscriptions());
    }

}
