package net.tgoroshek.subscriptionsdemo.payload;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Null;
import lombok.*;
import net.tgoroshek.subscriptionsdemo.model.subscriptions.SubscriptionTypes;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.RequestTypes;
import net.tgoroshek.subscriptionsdemo.payload.transferConditions.ResponseSegregation;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class SubscriptionDto {

    @JsonView({ResponseSegregation.Details.class})
    @Null(groups = {RequestTypes.Default.class}, message = "Поле не поддерживает редактирование")
    private String uuid;

    @JsonView({ResponseSegregation.Details.class})
    private SubscriptionTypes type;

    @JsonView({ResponseSegregation.Details.class})
    private String renewalUrl;

    @JsonView({ResponseSegregation.FullDetails.class})
    private String key;

    @JsonView({ResponseSegregation.Details.class})
    private LocalDateTime expiresAt;

    @JsonView({ResponseSegregation.Details.class})
    private Map<String, String> uniqueParams;
}
