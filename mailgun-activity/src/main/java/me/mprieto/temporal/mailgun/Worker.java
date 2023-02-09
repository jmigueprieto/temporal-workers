package me.mprieto.temporal.mailgun;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.mailgun.client.EmailApiFactory;

public class Worker {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("arguments:[host:port baseUrl user apiKey from]");
            System.exit(-1);
        }

        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.MAIL_QUEUE);
        var emailApi = EmailApiFactory.create(
                args[1],
                args[2],
                args[3]);

        worker.registerActivitiesImplementations(new MailgunActivityImpl(emailApi, args[4]));
        factory.start();
    }
}
