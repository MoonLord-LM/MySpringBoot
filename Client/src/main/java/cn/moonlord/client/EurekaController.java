package cn.moonlord.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
public class EurekaController{

    @Value("${server.port}")
    private Integer server_port;

    @ApiOperation(value="基础 Eureka 测试", notes="用于处理 /eureka 的请求")
    @RequestMapping(value = "/eureka", method = { RequestMethod.GET, RequestMethod.POST} )
    public String index(){
        return "请求端口号：" + server_port;
    }

}
