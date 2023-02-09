package me.mprieto.temporal.checkout;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface CheckoutWorkflow {

    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    void checkout(String sessionId);
}
