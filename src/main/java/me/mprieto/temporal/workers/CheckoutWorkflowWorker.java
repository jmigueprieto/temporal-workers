package me.mprieto.temporal.workers;

import me.mprieto.temporal.Queues;
import me.mprieto.temporal.workflows.CheckoutWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;

public class CheckoutWorkflowWorker {

    // Worker that polls for to execute Checkout Workflow
    public static void main(String[] args) {
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        var service = WorkflowServiceStubs.newLocalServiceStubs();
        var client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers that poll specific Task Queues.
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.CHECKOUT_WF_QUEUE);
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(CheckoutWorkflowImpl.class);
        // Start listening to the Task Queue.
        factory.start();
    }
}
