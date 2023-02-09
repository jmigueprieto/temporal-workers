package me.mprieto.temporal.checkout;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.WorkflowImplementationOptions;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.ChargeCustomerActivity;
import me.mprieto.temporal.activities.EmailSenderActivity;
import me.mprieto.temporal.activities.SessionActivity;
import me.mprieto.temporal.exceptions.CheckoutException;
import me.mprieto.temporal.model.email.EmailRequest;
import me.mprieto.temporal.model.session.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class CheckoutWorkflowTest {

    private TestWorkflowEnvironment testEnv;

    private ChargeCustomerActivity chargeCustomerActivity;

    private SessionActivity sessionActivity;

    private EmailSenderActivity emailSenderActivity;

    private WorkflowClient workflowClient;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        workflowClient = testEnv.getWorkflowClient();

        var workflowWorker = testEnv.newWorker(Queues.CHECKOUT_WF_QUEUE);
        var workflowImplementationOptions = WorkflowImplementationOptions.newBuilder()
                .setFailWorkflowExceptionTypes(CheckoutException.class)
                .build();
        workflowWorker.registerWorkflowImplementationTypes(workflowImplementationOptions, CheckoutWorkflowImpl.class);

        var stripeWorker = testEnv.newWorker(Queues.STRIPE_QUEUE);
        var sessionWorker = testEnv.newWorker(Queues.SESSION_QUEUE);
        var emailSenderWorker = testEnv.newWorker(Queues.MAIL_QUEUE);

        sessionActivity = mock(SessionActivity.class, withSettings().withoutAnnotations());
        sessionWorker.registerActivitiesImplementations(sessionActivity);

        chargeCustomerActivity = mock(ChargeCustomerActivity.class, withSettings().withoutAnnotations());
        stripeWorker.registerActivitiesImplementations(chargeCustomerActivity);

        emailSenderActivity = mock(EmailSenderActivity.class, withSettings().withoutAnnotations());
        emailSenderWorker.registerActivitiesImplementations(emailSenderActivity);

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
        when(sessionActivity.findSessionById(sessionId)).thenReturn(Session.builder()
                .status(Session.Status.OPEN)
                .stripeCustomerId("cus_IzssscT57x9e8K")
                .email("someone@mail.com")
                .amount(79700)
                .build());

        workflow.checkout(sessionId);

        verify(sessionActivity, times(1)).findSessionById(eq("session_01"));
        verify(chargeCustomerActivity, times(1)).charge(eq("cus_IzssscT57x9e8K"), eq(79700L));

        var argument = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailSenderActivity, times(1)).sendEmail(argument.capture());

        assertNull(argument.getValue().getFrom());
        assertEquals("someone@mail.com", argument.getValue().getTo());
        assertEquals("Receipt for session session_01", argument.getValue().getSubject());
        assertEquals("Here's your receipt for the amount of $797.00\n\nThank you!", argument.getValue().getText());
    }

    @Test
    @DisplayName("It should stop workflow execution if session is not found")
    public void sessionNotFound() {
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                .build();

        var workflow = workflowClient.newWorkflowStub(
                CheckoutWorkflow.class, options);

        var sessionId = "session_01";
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
        when(sessionActivity.findSessionById(sessionId)).thenReturn(Session.builder()
                .status(Session.Status.CLOSED)
                .stripeCustomerId("cus_IzssscT57x9e8K")
                .email("someone@mail.com")
                .amount(79700)
                .build());

        var thrown = assertThrows(WorkflowFailedException.class, () -> workflow.checkout(sessionId));

        var appFailure = (ApplicationFailure) thrown.getCause();
        assertEquals(CheckoutException.class.getTypeName(), appFailure.getType());
        assertEquals(String.format("Session '%s' is not open", sessionId), appFailure.getOriginalMessage());
    }
}
