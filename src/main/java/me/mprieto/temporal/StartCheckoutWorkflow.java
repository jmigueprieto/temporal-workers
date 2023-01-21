package me.mprieto.temporal;

import me.mprieto.temporal.workflows.CheckoutWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;


public class StartCheckoutWorkflow {

    public static void main(String[] args) {
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        var service = WorkflowServiceStubs.newLocalServiceStubs();
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                // Workflow id to use when starting Prevents duplicate instances.
                //.setWorkflowId("checkout-session-123")
                .build();
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        var client = WorkflowClient.newInstance(service);
        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        var workflow = client.newWorkflowStub(CheckoutWorkflow.class, options);
        // Asynchronous execution. This process will exit after making this call.
        //TODO make this an arg
        var sessionId = "session_01";
        var we = WorkflowClient.start(workflow::checkout, sessionId);
        System.out.printf("\nCheckout session: %s", sessionId);
        System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(), we.getRunId());
    }
}
