:begin

:cd "spring-cloud-config-server" && call mvn clean package && cd "../"
:cd "spring-cloud-eureka-server" && call mvn clean package && cd "../"
:cd "spring-cloud-gateway-server" && call mvn clean package && cd "../"

cd "spring-cloud-config-server"
start java -jar "target/spring-cloud-config-server-0.0.1-SNAPSHOT.jar" --server.port=9000
cd "../"

cd "spring-cloud-eureka-server"
start java -jar "target/spring-cloud-eureka-server-0.0.1-SNAPSHOT.jar" --server.port=9001
cd "../"

cd "spring-cloud-gateway-server"
start java -jar "target/spring-cloud-gateway-server-0.0.1-SNAPSHOT.jar" --server.port=8080
cd "../"

pause
cls
goto :begin
