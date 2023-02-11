package me.mprieto.temporal.checkout;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.ChargeCustomerActivity;
import me.mprieto.temporal.activities.EmailSenderActivity;
import me.mprieto.temporal.activities.SessionActivity;
import me.mprieto.temporal.exceptions.CheckoutException;
import me.mprieto.temporal.model.email.EmailRequest;
import me.mprieto.temporal.model.session.Session;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;

public class CheckoutWorkflowImpl implements CheckoutWorkflow {

    private final ChargeCustomerActivity chargeCustomerActivity;

    private final SessionActivity sessionActivity;

    private final EmailSenderActivity emailSenderActivity;

    public CheckoutWorkflowImpl() {
        // retry up to 3 times with an initial interval of 1 second between retries,
        // and with a maximum interval of 60 seconds between retries.
        var retryOptions = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setMaximumInterval(Duration.ofSeconds(60))
                .setBackoffCoefficient(2)
                .setMaximumAttempts(10)
                .build();

        chargeCustomerActivity = Workflow.newActivityStub(ChargeCustomerActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .setRetryOptions(retryOptions)
                        .build(),
                new HashMap<>() {{
                    put("Charge", ActivityOptions.newBuilder()
                            .setTaskQueue(Queues.STRIPE_QUEUE)
                            .setStartToCloseTimeout(Duration.ofSeconds(10))
                            .setRetryOptions(retryOptions)
                            .build());
                }});

        sessionActivity = Workflow.newActivityStub(SessionActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .setRetryOptions(retryOptions)
                        .setTaskQueue(Queues.SESSION_QUEUE)
                        .build());

        emailSenderActivity = Workflow.newActivityStub(EmailSenderActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .setRetryOptions(retryOptions)
                        .setTaskQueue(Queues.MAIL_QUEUE)
                        .build());
    }

    @Override
    public void checkout(String sessionId) {
        var session = sessionActivity.findSessionById(sessionId);
        if (session != null && session.getStatus() == Session.Status.OPEN) {
            chargeCustomerActivity.charge(session.getStripeCustomerId(), session.getAmount());
            sessionActivity.closeSession(sessionId);
            emailSenderActivity.sendEmail(EmailRequest.builder()
                    .to(session.getEmail())
                    .userId(session.getUserId())
                    .subject(String.format("Receipt for session %s", sessionId))
                    .text(String.format("Here's your receipt for the amount of $%s\n\nThank you!", new BigDecimal(session.getAmount()).movePointLeft(2)))
                    .build());
        } else if (session != null) {
            throw new CheckoutException(String.format("Session '%s' is not open", sessionId));
        } else {
            throw new CheckoutException(String.format("Session '%s' not found", sessionId));
        }
    }
}
