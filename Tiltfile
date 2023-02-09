#TODO this Tiltfile could be improved/optimized see: https://docs.tilt.dev/example_java.html
docker_compose("./deploy/docker-compose-temporal.yml")

local_resource(
    'gradle-build-checkout-workflow',
    './gradlew checkout-workflow:fatJar -x test',
    deps=[
            'checkout-workflow/src',
            'checkout-workflow/build.gradle',
            'common/src',
            'common/build.gradle',
            'build.gradle'
         ]
)

local_resource('gradle-build-session-activity',
    './gradlew session-activity:fatJar -x test',
    deps=[
            'session-activity/src',
            'session-activity/build.gradle',
            'common/src',
            'common/build.gradle',
            'build.gradle'
         ]
)

local_resource('gradle-build-stripe-activity',
    './gradlew stripe-activity:fatJar -x test',
    deps=[
            'stripe-activity/src',
            'stripe-activity/build.gradle',
            'common/src',
            'common/build.gradle',
            'build.gradle'
         ])

docker_build('worker-checkout-workflow',
    './checkout-workflow/build/libs',
    dockerfile='./deploy/checkout-workflow/worker.dockerfile')

docker_build('worker-stripe-activity',
    './stripe-activity/build/libs',
    dockerfile='./deploy/stripe-activity/worker.dockerfile')

docker_build('worker-session-activity',
    './session-activity/build/libs',
    dockerfile='./deploy/session-activity/worker.dockerfile')

yaml = helm('./deploy/charts/workers',
    name='temporal-workers',
    values=['./values.yaml'])

k8s_yaml(yaml)
