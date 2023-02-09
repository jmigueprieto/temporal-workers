package me.mprieto.temporal.checkout;

import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import me.mprieto.temporal.Queues;

public class Starter {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("arguments:[host:port sessionId]");
            System.exit(-1);
        }

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the Temporal server.
        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        var sessionId = args[1];
        var options = WorkflowOptions.newBuilder()
                .setTaskQueue(Queues.CHECKOUT_WF_QUEUE)
                // Workflow id to use when starting Prevents duplicate instances.
                .setWorkflowId(sessionId)
                .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                .build();
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        var client = WorkflowClient.newInstance(service);
        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        var workflow = client.newWorkflowStub(CheckoutWorkflow.class, options);
        // Asynchronous execution. This process will exit after making this call.
        var we = WorkflowClient.start(workflow::checkout, sessionId);
        System.out.printf("\nCheckout session: %s", sessionId);
        System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(), we.getRunId());
    }
}
