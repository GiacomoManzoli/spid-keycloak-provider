```bash
docker build -t maven-builder -f Dockerfile.builder .


docker run -d \
  --name maven-build \
  -v "$(pwd)":/workspace \
  maven-builder \
  sleep infinity

docker exec maven-build mvn clean package

```