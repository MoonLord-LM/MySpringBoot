package cn.moonlord.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableEurekaServer
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Value("${eureka.dashboard.path}")
    String eurekaDashBoardPath;

    @RequestMapping("/")
    String home() {
        return "This is Index Page. <br/>"
                + "<a href=\"" + eurekaDashBoardPath + "\">" + eurekaDashBoardPath + "</a>";
    }

}
