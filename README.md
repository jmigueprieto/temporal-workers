# Sample Temporal Workers

This project contains a sample Checkout Workflow and related activities (to find a session and to charge with Stripe).

## Build the project

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

- To start a new checkout workflow, either run the `checkout-workflow/me.mprieto.temporal.checkout.Worker` from your IDE or from the project 
root run:

```shell
./gradlew checkout-workflow:start --args="localhost:7233 session_01"
```

**Notes**
`session_01` is a session id. The sessions are just hardcoded (and the Stripe customer id in them) in `SessionActivityImpl`

- To start a worker to process the workflows, either run `checkout-workflow/me.mprieto.temporal.checkout.Worker` from your IDE or from the 
project root run:

```shell
./gradlew checkout-workflow:worker --args="localhost:7233"
```

- To start a Session Worker, either run `session-activity/me.mprieto.temporal.session.Worker` from your IDE or from the project root run:

```shell
./gradlew session-activity:worker --args="localhost:7233" 
```

- To start a Stripe Worker, either run `stripe-activity/me.mprieto.temporal.stripe.Worker` from your IDE or from the project root run:

```shell
./gradlew stripe-activity:worker --args="localhost:7233 ${YOUR_STRIPE_API_KEY}"
```

### Using Tilt

You need to make sure you have Docker and a Kubernetes cluster up & running (and Tilt installed, of course)

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