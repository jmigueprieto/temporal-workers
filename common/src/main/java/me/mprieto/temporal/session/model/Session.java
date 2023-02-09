package me.mprieto.temporal.session.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String stripeCustomerId;

    private long amount;
}
