package cn.moonlord.springboot.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.DefaultResourceLoader;

import java.net.MalformedURLException;

@SpringBootApplication
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) throws ClassNotFoundException {

        MyPreferURLClassLoader loader = new MyPreferURLClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> tmp = loader.loadClass("com.alibaba.fastjson.JSON");
        logger.info("location: {}", tmp.getProtectionDomain().getCodeSource().getLocation());
        logger.info("loader: {}", tmp.getProtectionDomain().getClassLoader());

        //Thread.currentThread().setContextClassLoader(loader);

        SpringApplication app = new SpringApplication();
        logger.info("app.getClassLoader(): {}", app.getClassLoader());
        logger.info("app.getResourceLoader(): {}", app.getResourceLoader());
        logger.info("app.getResourceLoader().getClassLoader(): {}", app.getResourceLoader() == null ? null : app.getResourceLoader().getClassLoader());

        loader = new MyPreferURLClassLoader(app.getClassLoader());
        tmp = loader.loadClass("com.alibaba.fastjson.JSON");
        logger.info("location: {}", tmp.getProtectionDomain().getCodeSource().getLocation());
        logger.info("loader: {}", tmp.getProtectionDomain().getClassLoader());

        MyPreferURLClassLoader finalLoader = loader;
        app.setResourceLoader(new DefaultResourceLoader(){
            @Override
            public ClassLoader getClassLoader() {
                return finalLoader;
            }
        });
        logger.info("app.loader: {}", app.getClassLoader());
        app.run(args);
    }

}
