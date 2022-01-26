package cn.moonlord.springboot.demo;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;

@SpringBootApplication
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    private static volatile MyPreferURLClassLoader loader = new MyPreferURLClassLoader();

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatServletContainerCustomizer() {
        logger.info("create WebServerFactoryCustomizer<TomcatServletWebServerFactory>");
        return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
            @Override
            public void customize(TomcatServletWebServerFactory factory) {
                logger.info("customize TomcatServletWebServerFactory");
                factory.setResourceLoader(new DefaultResourceLoader(){
                    @Override
                    public ClassLoader getClassLoader() {
                        return loader;
                    }
                });
                factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                    @Override
                    public void customize(Connector connector) {
                        logger.info("customize TomcatConnectorCustomizer");
                    }
                });
            }
        };
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> tmp = loader.loadClass("com.alibaba.fastjson.JSON");
        logger.info("loader location: {}", tmp.getProtectionDomain().getCodeSource().getLocation());
        logger.info("loader: {}", tmp.getProtectionDomain().getClassLoader());

        System.setProperty("loader.path", "D:/Software/apache-maven-3.6.0/mvnRepository/com/alibaba/fastjson/1.2.62/");

        SpringApplication app = new SpringApplication(DemoApplication.class);
        logger.info("app.getResourceLoader(): {}", app.getResourceLoader());
        logger.info("app.getResourceLoader().getClassLoader(): {}", app.getResourceLoader() == null ? null : app.getResourceLoader().getClassLoader());
        logger.info("app.getClassLoader(): {}", app.getClassLoader());

        Thread.currentThread().setContextClassLoader(app.getClassLoader());
        loader = new MyPreferURLClassLoader();
        Thread.currentThread().setContextClassLoader(loader);

        logger.info("app.getResourceLoader(): {}", app.getResourceLoader());
        logger.info("app.getResourceLoader().getClassLoader(): {}", app.getResourceLoader() == null ? null : app.getResourceLoader().getClassLoader());
        logger.info("app.getClassLoader(): {}", app.getClassLoader());

        tmp = loader.loadClass("com.alibaba.fastjson.JSON");
        logger.info("loader location: {}", tmp.getProtectionDomain().getCodeSource().getLocation());
        logger.info("loader: {}", tmp.getProtectionDomain().getClassLoader());

        app.setResourceLoader(new DefaultResourceLoader(){
            @Override
            public ClassLoader getClassLoader() {
                return loader;
            }
        });

        logger.info("app.getResourceLoader(): {}", app.getResourceLoader());
        logger.info("app.getResourceLoader().getClassLoader(): {}", app.getResourceLoader() == null ? null : app.getResourceLoader().getClassLoader());
        logger.info("app.getClassLoader(): {}", app.getClassLoader());
        app.run(args);
    }

}
