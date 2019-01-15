package cn.moonlord.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Api
@RestController
public class BaseController implements ErrorController {

    @Override
    public String getErrorPath() { return null; }

    @ApiOperation(value="基础 index 服务", notes="用于处理根路径的请求")
    @RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST} )
    public String index() {
        return "This is Index Page. <br/> Hello World!";
    }

    @ApiOperation(value="基础 error 服务", notes="用于处理出错的请求")
    @RequestMapping(value = "/error", method = { RequestMethod.GET, RequestMethod.POST} )
    public String error(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        return "This is Error Page. <br/> Request URL: " + request.getRequestURI() + "Error Code: " + response.getStatus() + ".";
    }

}
