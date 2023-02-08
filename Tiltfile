# Temporal server - this is not currently working
docker_compose("./deploy/docker-compose.yml")

local_resource(
  'gradle-build',
  './gradlew fatJar -x test',
  deps=['src', 'build.gradle'],
  resource_deps = ['deploy'])

docker_build(
  'checkout-workflow-worker',
  './build/libs',
  dockerfile='./deploy/checkout-workflow-worker.dockerfile')

docker_build(
  'session-worker',
  './build/libs',
  dockerfile='./deploy/session-worker.dockerfile')

docker_build(
  'stripe-worker',
  './build/libs',
  dockerfile='./deploy/stripe-worker.dockerfile')

k8s_yaml('./deploy/checkout-workflow-worker-k8s.yaml')
k8s_yaml('./deploy/stripe-worker-k8s.yaml')
k8s_yaml('./deploy/session-worker-k8s.yaml')