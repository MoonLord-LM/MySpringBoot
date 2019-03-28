package cn.moonlord.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api
@RestController
public class BaseController implements ErrorController {

    @Override
    public String getErrorPath() { return null; }

    @Value("swagger-ui.html")
    String swaggerDashBoardPath;

    @ApiOperation(value="基础 index 服务", notes="用于处理根路径的请求")
    @RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST} )
    public String index(){
        return "This is Index Page. <br/>"
                + "<a href=\"" + swaggerDashBoardPath + "\">./" + swaggerDashBoardPath + "</a><br/>";
    }

    @ApiOperation(value="基础 error 服务", notes="用于处理出错的请求")
    @RequestMapping(value = "/error", method = { RequestMethod.GET, RequestMethod.POST} )
    public String error(HttpServletRequest request) {
        String request_uri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (request_uri == null){
            request_uri = request.getRequestURI();
        }
        Integer status_code = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (status_code == null){
            status_code = HttpStatus.OK.value();
        }
        return "This is Error Page. <br/>"
                + "Request URI: " + request_uri + " <br/>"
                + "Status Code: " + status_code + " <br/>";
    }

}
