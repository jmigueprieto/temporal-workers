package me.mprieto.temporal.model.session;


import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    public enum Status {
        OPEN, CLOSED
    }

    @Builder.Default
    private Status status = Status.OPEN;

    @NonNull
    private String userId;

    @NonNull
    private String stripeCustomerId;

    @NonNull
    private String email;

    private long amount;
}
