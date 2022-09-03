package cn.moonlord.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableConfigServer
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class CustomEnvironmentRepository implements EnvironmentRepository {
        @Override
        public Environment findOne(String application, String profile, String label) {
            Environment environment = new Environment(application, profile);
            Map<String, String> properties1 = new HashMap<>();
            properties1.put("application", application);
            properties1.put("profile", profile);
            properties1.put("label", label);
            Map<String, String> properties2= new HashMap<>();
            properties2.put("Hello", "World");
            environment.add(new PropertySource("properties1", properties1));
            environment.add(new PropertySource("properties2", properties2));
            return environment;
        }
    }

}
