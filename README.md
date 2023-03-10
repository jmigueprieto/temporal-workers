# Sample Temporal Workflow and Activities

This project contains a sample Checkout Workflow and related activities to find a session, charge with Stripe and send a receipt email.

## Running things

### Using Gradle

> **Make sure the [Temporal server](https://docs.temporal.io/docs/server/quick-install) is running!**


#### Start a new workflow

```shell
./gradlew checkout-workflow:start --args="localhost:7233 ${sessionId}"
```

#### Checkout Workflow Worker

```shell
./gradlew checkout-workflow:worker --args="localhost:7233"
```

#### Session Worker

```shell
./gradlew session-activity:worker --args="localhost:7233 ${BASE_URL}" 
```

*`SessionActivityImpl` just makes an HTTP `GET` request to `baseURL + "session/${id}"` to get a session. I'm currently using [Wiremock Cloud](https://www.wiremock.io/) to
simulate a "Session Service".*

#### Stripe Worker

```shell
./gradlew stripe-activity:worker --args="localhost:7233 ${STRIPE_API_KEY}"
```

#### Mailgun Worker

```shell
./gradlew mailgun-activity:worker --args="localhost:7233 ${MAILGUN_BASE_URL} ${MAILGUN_USER} ${MAILGUN_API_KEY} ${FROM} ${SPLIT_IO_API_TOKEN}"
```

*`FROM` is a default email, e.g.: noreply@mydomain.com.*

---

### Using Tilt

You need to make sure you have Docker and a Kubernetes cluster up & running (and [Tilt](https://tilt.dev/) installed, 
of course)

**Tilt takes care of spinning up a Temporal server** and uses a [Helm chart](https://helm.sh/docs/topics/charts/) to 
deploy the workers to Kubernetes. Take a look at the `Tiltfile`.

You need to set the following values in `values.yaml` (added to `.gitignore` to avoid
committing api keys) in the project's root dir: 

```yaml
stripe:
  apiKey: {CHANGE_ME}

temporal:
    target: ${HOST}:${PORT}

mailgun:
  user: api
  apiKey: {CHANGE_ME}
  baseUrl: https://api.mailgun.net/v3/{CHANGE_ME}/
  from: {CHANGE_ME}

sessions:
  baseUrl: https://{CHANGE_ME}.wiremockapi.cloud/

split:
  apiToken: {CHANGE_ME}
```

Once you've set the above file, you can run:

```shell
tilt up
```