package me.mprieto.temporal.workers;

import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.StripeActivityImpl;
import com.stripe.Stripe;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;

public class StripeWorker {

    // Worker that polls for tasks to charge with stripe
    public static void main(String[] args) {
        var stripeApiKey = System.getenv("STRIPE_API_KEY");
        if (stripeApiKey == null) {
            System.out.println("Please define the Stripe API Key in the env variable `STRIPE_API_KEY`");
            System.exit(-1);
        }

        Stripe.apiKey = stripeApiKey;
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget("192.168.68.67:7233")
                .build());

        var client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers that poll specific Task Queues.
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.STRIPE_WORKER_QUEUE);
        // Activities are stateless and thread safe so a shared instance is used.
        worker.registerActivitiesImplementations(new StripeActivityImpl());
        // Start listening to the Task Queue.
        factory.start();
    }
}
