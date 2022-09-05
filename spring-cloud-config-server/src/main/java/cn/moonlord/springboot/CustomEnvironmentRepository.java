package cn.moonlord.springboot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomEnvironmentRepository implements EnvironmentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomEnvironmentRepository.class);

    @Override
    public Environment findOne(String application, String profile, String label) {
        logger.info("findOne application: {}, profile: {}, label: {}", application, profile, label);
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
