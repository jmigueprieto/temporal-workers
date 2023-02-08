
STRIPE_API_KEY= os.getenv("STRIPE_API_KEY")
TEMPORAL_TARGET= os.getenv("TEMPORAL_TARGET")
# Temporal server - this is not currently working
docker_compose("./deploy/docker-compose-temporal.yml")

local_resource(
  'gradle-build',
  './gradlew fatJar -x test',
  deps=['src', 'build.gradle'],
  resource_deps = ['deploy'])

docker_build(
  'temporal-workers',
  './build/libs',
  dockerfile='./deploy/workers.dockerfile')

k8s_yaml('./deploy/k8s-checkout-workflow-worker.yaml')
k8s_yaml('./deploy/k8s-stripe-worker.yaml')
k8s_yaml('./deploy/k8s-session-worker.yaml')