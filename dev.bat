:begin

cd "spring-cloud-config-server" && call mvn clean package && cd "../"
cd "spring-cloud-eureka-server" && call mvn clean package && cd "../"
cd "spring-cloud-gateway-server" && call mvn clean package && cd "../"

cd "spring-cloud-config-server"
start java -jar "target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar" --server.port=9000 ^
 --spring.application.name=spring-cloud-config-server ^
 --management.endpoints.web.exposure.include=*
cd "../"

cd "spring-cloud-eureka-server"
start java -jar "target/spring-cloud-eureka-server-0.0.1-SNAPSHOT.jar" --server.port=9001 ^
 --spring.application.name=spring-cloud-eureka-server ^
 --management.endpoints.web.exposure.include=* ^
 --eureka.client.service-url.defaultZone=http://localhost:9001/eureka/,http://127.0.0.1:9002/eureka/ ^
 --eureka.client.fetch-registry=true --eureka.client.register-with-eureka=true ^
 --eureka.instance.hostname=localhost
cd "../"

cd "spring-cloud-eureka-server"
start java -jar "target/spring-cloud-eureka-server-0.0.1-SNAPSHOT.jar" --server.port=9002 ^
 --spring.application.name=spring-cloud-eureka-server ^
 --management.endpoints.web.exposure.include=* ^
 --eureka.client.service-url.defaultZone=http://localhost:9001/eureka/,http://127.0.0.1:9002/eureka/ ^
 --eureka.client.fetch-registry=true --eureka.client.register-with-eureka=true ^
 --eureka.instance.hostname=127.0.0.1
cd "../"

cd "spring-cloud-gateway-server"
start java -jar "target/spring-cloud-gateway-server-0.0.1-SNAPSHOT.jar" --server.port=8080 ^
 --spring.application.name=spring-cloud-gateway-server ^
 --management.endpoints.web.exposure.include=*
cd "../"

pause
cls
goto :begin
