:begin


cd "spring-cloud-config-server" && call mvn clean package && cd "../"
cd "spring-cloud-eureka-server" && call mvn clean package && cd "../"
cd "spring-cloud-gateway-server" && call mvn clean package && cd "../"


cd "spring-cloud-config-server"
start java -jar "./target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar" --server.port=8888 ^
 --management.endpoints.web.exposure.include=* ^
 --logging.file.name=./target/spring-cloud-config-server-0.0.1-SNAPSHOT.log
cd "../"


cd "spring-cloud-eureka-server"
start java -jar "./target/spring-cloud-eureka-server-0.0.1-SNAPSHOT.jar" --server.port=8761 ^
 --management.endpoints.web.exposure.include=* ^
 --logging.file.name=./target/spring-cloud-eureka-server-0.0.1-SNAPSHOT-8761.log ^
 --eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/ ^
 --eureka.client.fetch-registry=true ^
 --eureka.client.register-with-eureka=true ^
 --eureka.instance.prefer-ip-address=false ^
 --eureka.instance.hostname=localhost
cd "../"

cd "spring-cloud-eureka-server"
start java -jar "./target/spring-cloud-eureka-server-0.0.1-SNAPSHOT.jar" --server.port=8762 ^
 --management.endpoints.web.exposure.include=* ^
 --logging.file.name=./target/spring-cloud-eureka-server-0.0.1-SNAPSHOT-8762.log ^
 --eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/ ^
 --eureka.client.fetch-registry=true ^
 --eureka.client.register-with-eureka=true ^
 --eureka.instance.prefer-ip-address=false ^
 --eureka.instance.hostname=127.0.0.1
cd "../"


ping -n 30 127.0.0.1 > nul


cd "spring-cloud-gateway-server"
start java -jar "./target/spring-cloud-gateway-server-0.0.1-SNAPSHOT.jar" --server.port=80 ^
 --management.endpoints.web.exposure.include=* ^
 --logging.file.name=./target/spring-cloud-gateway-server-0.0.1-SNAPSHOT.log ^
 --eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/
cd "../"

cd "spring-boot-admin-server"
start java -jar "./target/spring-boot-admin-server-0.0.1-SNAPSHOT.jar" --server.port=9000 ^
 --management.endpoints.web.exposure.include=* ^
 --logging.file.name=./target/spring-boot-admin-server-0.0.1-SNAPSHOT.log ^
 --eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://127.0.0.1:8762/eureka/
cd "../"


pause
cls
goto :begin
