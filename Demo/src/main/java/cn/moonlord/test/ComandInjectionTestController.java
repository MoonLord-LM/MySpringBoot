package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Api(tags = "命令注入 测试")
@RestController
@RequestMapping("/ComandInjection")
public class ComandInjectionTestController {

    private static final String testCaseA = "echo HelloWorld";

    @ApiOperation(value="测试用例 A1")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入参数", example = testCaseA))
    @GetMapping(value = "/ComandInjectionTestA1")
    public String ComandInjectionTestA1(@RequestParam String inputXML) throws Exception {
        String cmd = "echo HelloWorld";
        Process p = Runtime.getRuntime().exec("ping", new String[] {"www.moonlord.cn"});
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        p.waitFor();
        is.close();
        reader.close();
        p.destroy();
        System.out.println(sb);
        return sb.toString();
    }

}
