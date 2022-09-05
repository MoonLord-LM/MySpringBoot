package cn.moonlord.springboot;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAdminServer
@EnableEurekaClient
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public InstanceExchangeFilterFunction requestFilter() {
        return (instance, request, next) -> next.exchange(request).doOnSubscribe((s) -> {
            logger.info("requestFilter method: {} instance: {} url: {}", request.method(), instance.getId(), request.url());
        });
    }

}
