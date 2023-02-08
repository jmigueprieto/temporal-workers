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

#STRIPE_API_KEY= os.getenv("STRIPE_API_KEY")
#TEMPORAL_TARGET= os.getenv("TEMPORAL_TARGET")

yaml = helm('./deploy/charts/workers',
         name='temporal-workers',
#        set=['temporal.target={TEMPORAL_TARGET}', 'stripe.apiKey={STRIPE_API_KEY}']
         values=['./values.yaml']
         )
k8s_yaml(yaml)
