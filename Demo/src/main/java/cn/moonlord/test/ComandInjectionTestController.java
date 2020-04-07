package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Api(tags = "命令注入 测试")
@RestController
@RequestMapping("/ComandInjection")
public class ComandInjectionTestController {

    private static final String testCaseA1 = "D:\\Software\\Git\\usr\\bin\\echo.exe";
    private static final String testCaseA2 = "Hello";
    private static final String testCaseA3 = "World";
    private static final String testCaseA4 = "&";
    private static final String testCaseA5 = "ipconfig";

    private static final String testCaseB1 = "cmd";
    private static final String testCaseB2 = "/c";
    private static final String testCaseB3 = "D:\\Software\\Git\\usr\\bin\\echo.exe";
    private static final String testCaseB4 = "Hello";
    private static final String testCaseB5 = "World";

    private static final String testCaseC1 = "ipconfig";
    private static final String testCaseC2 = "ipconfig -all";

    @ApiOperation(value="测试用例 A，控制所有参数来执行命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inputCommand1", value = "命令参数1", example = testCaseA1),
            @ApiImplicitParam(name = "inputCommand2", value = "命令参数2", example = testCaseA2),
            @ApiImplicitParam(name = "inputCommand3", value = "命令参数3", example = testCaseA3),
            @ApiImplicitParam(name = "inputCommand4", value = "命令参数4", example = testCaseA4),
            @ApiImplicitParam(name = "inputCommand5", value = "命令参数5", example = testCaseA5)
    })
    @GetMapping(value = "/WindowsTestA")
    public String WindowsTestA(
            @RequestParam String inputCommand1,
            @RequestParam String inputCommand2,
            @RequestParam String inputCommand3,
            @RequestParam String inputCommand4,
            @RequestParam String inputCommand5
    ) throws Exception {
        String[] command = new String[]{inputCommand1, inputCommand2, inputCommand3, inputCommand4, inputCommand5};
        Process p = Runtime.getRuntime().exec(command);
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "GBK");
        BufferedReader reader = new BufferedReader(isr);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\r\n");
        }
        p.waitFor();
        reader.close();
        isr.close();
        is.close();
        p.destroy();
        System.out.println(sb);
        return sb.toString();
    }

    @ApiOperation(value="测试用例 B，控制 Shell 解析器的参数来执行命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inputCommand1", value = "命令参数1", example = testCaseB1),
            @ApiImplicitParam(name = "inputCommand2", value = "命令参数2", example = testCaseB2),
            @ApiImplicitParam(name = "inputCommand3", value = "命令参数3", example = testCaseB3),
            @ApiImplicitParam(name = "inputCommand4", value = "命令参数4", example = testCaseB4),
            @ApiImplicitParam(name = "inputCommand5", value = "命令参数5", example = testCaseB5)
    })
    @GetMapping(value = "/WindowsTestB")
    public String WindowsTestB(
            @RequestParam String inputCommand1,
            @RequestParam String inputCommand2,
            @RequestParam String inputCommand3,
            @RequestParam String inputCommand4,
            @RequestParam String inputCommand5
    ) throws Exception {
        return WindowsTestA(inputCommand1, inputCommand2, inputCommand3, inputCommand4, inputCommand5);
    }

    @ApiOperation(value="测试用例 C1，只能控制第一个参数时，只能执行不带参数的命令")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inputCommand1", value = "命令参数1", example = testCaseC1)
    })
    @GetMapping(value = "/WindowsTestC1")
    public String WindowsTestC1(@RequestParam String inputCommand1) throws Exception {
        return WindowsTestA(inputCommand1, "", "", "", "");
    }

    @ApiOperation(value="测试用例 C2，只能控制第一个参数时，无法执行带参数的命令，会提示找不到指定的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inputCommand1", value = "命令参数1", example = testCaseC2)
    })
    @GetMapping(value = "/WindowsTestC2")
    public String WindowsTestC2(@RequestParam String inputCommand1) throws Exception {
        return WindowsTestA(inputCommand1, "", "", "", "");
    }

}
