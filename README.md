## Build the project

Either open the project in your IDE and build it or in the project's root directory run:

```bash
./gradlew build
```

## Running things

First, make sure the [Temporal server](https://docs.temporal.io/docs/server/quick-install) is running.

To start a new Checkout Workflow, either run the `StartCheckoutWorkflow` class from your IDE or from the project root run:

```bash
./gradlew checkout
```

To start the Checkout Workflow Worker, either run the `CheckoutWorkflowWorker` class from your IDE or from the project root run:

```bash
./gradlew checkoutWorkflowWorker
```

To start the Session Worker, either run the `SessionWorker` class from your IDE or from the project root run:

```bash
./gradlew sessionWorker
```

To start the Stripe Worker, either run the `StripeActivityWorker` class from your IDE or from the project root run:

```bash
export STRIPE_API_KEY = ${YOUR_STRIPE_API_KEY}
./gradlew stripeWorker
```

**NOTES**

- The sessions are just hardcoded (and the Stripe customer id in them) in `SessionActivity`.
- The session id used to start a workflow is also just hardcoded in `StartCheckoutWorkflow`.
