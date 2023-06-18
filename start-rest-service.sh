docker run -d -p 5455:5432 -e POSTGRES_USER=cinema -e POSTGRES_PASSWORD=cinema -e POSTGRES_DB=cinema -e POSTGRES_HOST_AUTH_METHOD=trust  postgres

cd cinema-rest-api

sbt assembly

java -jar target/scala-2.10/cinema-api-rest-assembly-1.0.jar
