package me.mprieto.temporal.workflows;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.testing.TestWorkflowEnvironment;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.SessionActivityImpl;
import me.mprieto.temporal.activities.StripeActivity;
import me.mprieto.temporal.activities.StripeActivityImpl;
import me.mprieto.temporal.activities.exceptions.CheckoutException;
import me.mprieto.temporal.activities.model.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class StartCheckoutWorkflowTest {

    private TestWorkflowEnvironment testEnv;

    private StripeActivity stripeActivityMock;

    private SessionActivityImpl sessionActivity;

    private WorkflowClient workflowClient;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        workflowClient = testEnv.getWorkflowClient();

        var workflowWorker = testEnv.newWorker(Queues.CHECKOUT_WF_QUEUE);
//      var workflowImplementationOptions = WorkflowImplementationOptions.newBuilder()
//              .setFailWorkflowExceptionTypes(CheckoutException.class)
//              .build();
        workflowWorker.registerWorkflowImplementationTypes(CheckoutWorkflowImpl.class);

        var stripeWorker = testEnv.newWorker(Queues.STRIPE_WORKER_QUEUE);
        var sessionWorker = testEnv.newWorker(Queues.SESSION_QUEUE);

        sessionActivity = new SessionActivityImpl();
        sessionWorker.registerActivitiesImplementations(sessionActivity);

        stripeActivityMock = mock(StripeActivityImpl.class);
        stripeWorker.registerActivitiesImplementations(stripeActivityMock);

        testEnv.start();
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    @DisplayName("It should charge the customer with stripe and close the session")
    public void charge() {
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                .build();
        var workflow = workflowClient.newWorkflowStub(
                CheckoutWorkflow.class, options);

        var sessionId = "session_01";
        var sessionBeforeCheckout = sessionActivity.findSessionById(sessionId);
        assertEquals(Session.Status.OPEN, sessionBeforeCheckout.getStatus());

        workflow.checkout(sessionId);
        verify(stripeActivityMock).charge(eq("cus_IzssscT57x9e8K"), eq(79700L));

        var sessionAfterCheckout = sessionActivity.findSessionById(sessionId);
        assertEquals(Session.Status.CLOSED, sessionAfterCheckout.getStatus());
    }

    @Test
    @DisplayName("It should stop workflow execution if session is not found")
    public void sessionNotFound() {
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                .build();

        var workflow = workflowClient.newWorkflowStub(
                CheckoutWorkflow.class, options);

        var sessionId = "session_X";
        var session = sessionActivity.findSessionById(sessionId);
        assertNull(session);

        var thrown = assertThrows(WorkflowFailedException.class, () -> workflow.checkout(sessionId));

        var appFailure = (ApplicationFailure) thrown.getCause();
        assertEquals(CheckoutException.class.getTypeName(), appFailure.getType());
        assertEquals(String.format("Session '%s' not found", sessionId), appFailure.getOriginalMessage());
    }

    @Test
    @DisplayName("It should stop workflow execution if session is not found")
    public void sessionNotOpen() {
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                .build();

        var workflow = workflowClient.newWorkflowStub(
                CheckoutWorkflow.class, options);

        var sessionId = "session_01";
        var session = sessionActivity.findSessionById(sessionId);
        assertNotNull(session);
        session.setStatus(Session.Status.CLOSED);

        var thrown = assertThrows(WorkflowFailedException.class, () -> workflow.checkout(sessionId));

        var appFailure = (ApplicationFailure) thrown.getCause();
        assertEquals(CheckoutException.class.getTypeName(), appFailure.getType());
        assertEquals(String.format("Session '%s' is not open", sessionId), appFailure.getOriginalMessage());
    }
}
