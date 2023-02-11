package me.mprieto.temporal.mailgun;

import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import lombok.extern.slf4j.Slf4j;
import me.mprieto.temporal.Queues;
import me.mprieto.temporal.mailgun.client.EmailApi;
import me.mprieto.temporal.mailgun.client.EmailApiFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Worker {

    public static void main(String[] args) {
        if (args.length != 6) {
            System.err.println("arguments:[host:port mailgun.baseUrl mailgun.user mailgun.apiKey from split.apiToken]");
            System.exit(-1);
        }

        var service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(args[0])
                .build());

        var client = WorkflowClient.newInstance(service);
        var factory = WorkerFactory.newInstance(client);
        var worker = factory.newWorker(Queues.MAIL_QUEUE);

        var emailApi = initEmailApi(args);
        var splitClient = initSplitClient(args[5]);
        worker.registerActivitiesImplementations(new MailgunActivityImpl(emailApi, args[4], splitClient));
        factory.start();
    }

    private static EmailApi initEmailApi(String[] args) {
        return EmailApiFactory.create(
                args[1],
                args[2],
                args[3]);
    }

    private static SplitClient initSplitClient(String apiToken) {
        try {
            var config = SplitClientConfig.builder()

                    .setBlockUntilReadyTimeout(10000)
                    .build();
            var splitFactory = SplitFactoryBuilder.build(apiToken, config);
            var splitClient = splitFactory.client();
            splitClient.blockUntilReady();
            return splitClient;
        } catch (TimeoutException | InterruptedException | IOException | URISyntaxException e) {
            log.error("Error while initializing split.io client");
            throw new RuntimeException(e);
        }
    }
}
