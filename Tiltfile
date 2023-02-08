docker_compose("./deploy/docker-compose-temporal.yml")

local_resource('gradle-build',
    './gradlew fatJar -x test',
    deps=['src', 'build.gradle'])

docker_build('temporal-workers',
    './build/libs',
    dockerfile='./deploy/workers.dockerfile')

yaml = helm('./deploy/charts/workers',
    name='temporal-workers',
    values=['./values.yaml'])

k8s_yaml(yaml)
