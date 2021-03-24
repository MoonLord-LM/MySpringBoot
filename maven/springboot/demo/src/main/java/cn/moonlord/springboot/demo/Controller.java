package cn.moonlord.springboot.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/**")
    public String index() {
        return "Hello World!";
    }

}
