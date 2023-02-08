package me.mprieto.temporal.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.SessionActivity;
import me.mprieto.temporal.activities.StripeActivity;
import me.mprieto.temporal.activities.exceptions.CheckoutException;
import me.mprieto.temporal.activities.model.Session;

import java.time.Duration;
import java.util.HashMap;

public class CheckoutWorkflowImpl implements CheckoutWorkflow {

    private final StripeActivity stripeActivity;

    private final SessionActivity sessionActivity;

    public CheckoutWorkflowImpl() {
        // retry up to 3 times with an initial interval of 1 second between retries,
        // and with a maximum interval of 60 seconds between retries.
        var retryOptions = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setMaximumInterval(Duration.ofSeconds(60))
                .setBackoffCoefficient(2)
                .setMaximumAttempts(100)
                .build();

        stripeActivity = Workflow.newActivityStub(StripeActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .setRetryOptions(retryOptions)
                        .build(),
                new HashMap<>() {{
                    put("Charge", ActivityOptions.newBuilder()
                            .setTaskQueue(Queues.STRIPE_WORKER_QUEUE)
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
    }

    @Override
    public void checkout(String sessionId) {
        var session = sessionActivity.findSessionById(sessionId);
        // In a real workflow we should acquire a (distributed) lock over the session
        // to avoid double charges
        if (session != null && session.getStatus() == Session.Status.OPEN) {
            stripeActivity.charge(session.getStripeCustomerId(), session.getAmount());
            sessionActivity.closeSession(sessionId);
        } else if (session != null) {
            throw new CheckoutException(String.format("Session '%s' is not open", sessionId));
        } else {
            throw new CheckoutException(String.format("Session '%s' not found", sessionId));
        }
    }
}
