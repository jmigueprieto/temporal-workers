package me.mprieto.temporal.workers;

import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.SessionActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;

public class SessionWorker {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("arguments:[host:port]");
            System.exit(-1);
        }

        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.SESSION_QUEUE);
        worker.registerActivitiesImplementations(new SessionActivityImpl());
        factory.start();
    }

}
