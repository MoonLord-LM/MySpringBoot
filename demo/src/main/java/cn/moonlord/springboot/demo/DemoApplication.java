package cn.moonlord.springboot.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@SpringBootApplication
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException {
        URLClassLoader preferLoader = new URLClassLoader(new URL[]{
                new URL("file:///D:/Software/apache-maven-3.6.0/mvnRepository/com/alibaba/fastjson/1.2.62/fastjson-1.2.62.jar")
        },null);

        MyPreferURLClassLoader loader = new MyPreferURLClassLoader(preferLoader);
        Thread.currentThread().setContextClassLoader(loader);

        Class<?> tmp = loader.loadClass("com.alibaba.fastjson.JSON");
        logger.info("location: {}", tmp.getProtectionDomain().getCodeSource().getLocation());
        logger.info("loader: {}", tmp.getProtectionDomain().getClassLoader());

        SpringApplication app = new SpringApplication(DemoApplication.class){
            @Override
            public ResourceLoader getResourceLoader() {
                return new DefaultResourceLoader(){
                    @Override
                    public ClassLoader getClassLoader() {
                        return loader;
                    }
                };
            }
        };
        app.setResourceLoader(new DefaultResourceLoader(){
            @Override
            public ClassLoader getClassLoader() {
                return loader;
            }
        });
        logger.info("app.loader: {}", app.getClassLoader());
        app.run(args);
    }

}
