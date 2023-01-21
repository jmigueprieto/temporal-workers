package me.mprieto.temporal.workers;

import me.mprieto.temporal.Queues;
import me.mprieto.temporal.activities.SessionActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;

public class SessionWorker {

    public static void main(String[] args) {
        var service = WorkflowServiceStubs.newLocalServiceStubs();
        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.SESSION_QUEUE);
        worker.registerActivitiesImplementations(new SessionActivityImpl());
        factory.start();
    }

}
