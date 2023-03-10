package me.mprieto.temporal.checkout;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkflowImplementationOptions;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.exceptions.CheckoutException;

public class Worker {

    // Worker that polls for to execute Checkout Workflow
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("arguments:[host:port]");
            System.exit(-1);
        }

        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        var client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers that poll specific Task Queues.
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.CHECKOUT_WF_QUEUE);
        var workflowImplementationOptions = WorkflowImplementationOptions.newBuilder()
                .setFailWorkflowExceptionTypes(CheckoutException.class)
                .build();
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions, CheckoutWorkflowImpl.class);
        // Start listening to the Task Queue.
        factory.start();
    }
}
