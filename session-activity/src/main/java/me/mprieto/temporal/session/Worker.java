package me.mprieto.temporal.session;

import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import me.mprieto.temporal.Queues;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;
import me.mprieto.temporal.session.client.SessionApiFactory;

public class Worker {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("arguments:[host:port] baseUrl");
            System.exit(-1);
        }

        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.SESSION_QUEUE);
        var sessionsApi = SessionApiFactory.create(args[1]);
        worker.registerActivitiesImplementations(new SessionActivityImpl(sessionsApi));
        factory.start();
    }

}
