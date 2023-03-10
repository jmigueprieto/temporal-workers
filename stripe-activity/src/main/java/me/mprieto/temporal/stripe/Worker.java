package me.mprieto.temporal.stripe;

import com.stripe.Stripe;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import me.mprieto.temporal.Queues;

public class Worker {

    // Worker that polls for tasks to charge with stripe
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("arguments:[host:port stripe_api_key]");
            System.exit(-1);
        }

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the Temporal server.
        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        Stripe.apiKey = args[1];

        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.STRIPE_QUEUE);
        worker.registerActivitiesImplementations(new StripeActivityImpl());
        factory.start();
    }
}
