## Build the project

Either open the project in your IDE and build it or in the project's root directory run:

```shell
./gradlew clean build
```

To generate a fat, all-in-one jar
```shell
./gradlew fatJar
```

## Running things

### Manually - Gradle or IDE

First, make sure the [Temporal server](https://docs.temporal.io/docs/server/quick-install) is running.

- To start a new Checkout Workflow, either run the `StartCheckoutWorkflow` class from your IDE or from the project 
root run:

```shell
./gradlew checkout --args="localhost:7233 session_01"
```

**Notes**
`session_01` is a session id. The sessions are just hardcoded (and the Stripe customer id in them) in `SessionActivity`

- To start the Checkout Workflow Worker, either run the `CheckoutWorkflowWorker` class from your IDE or from the 
project root run:

```shell
./gradlew checkoutWorkflowWorker --args="localhost:7233"
```

- To start the Session Worker, either run the `SessionWorker` class from your IDE or from the project root run:

```shell
./gradlew sessionWorker --args="localhost:7233" 
```

- To start the Stripe Worker, either run the `StripeActivityWorker` class from your IDE or from the project root run:

```shell
./gradlew stripeWorker --args="localhost:7233 ${YOUR_STRIPE_API_KEY}"
```

### Using Tilt

You need to make sure you have Docker up & running and a Kubernetes cluster (and Tilt installed, of course)

The `Tiltfile` uses a Helm chart to deploy the workers to a K8S cluster. 

You need to set the following values in `values.yaml` at the root of the project (added to `.gitignore` to avoid 
committing the Stripe Api key)

```yaml
stripe:
  apiKey: sk_test_xxxxx

temporal:
    target: HOST:PORT
```

Once you've set the above file, you can run

```shell
tilt up
```