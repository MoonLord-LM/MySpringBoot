package cn.moonlord.test;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Type;

@Api(tags = "FastJson 测试")
@RestController
@RequestMapping("/FastJson")
public class FastJsonTestController {

    public static class SimpleObject implements Serializable {
        private String name;
        SimpleObject(){
            System.out.println("构造方法被调用！");
        }
        public String getName() {
            System.out.println("getName 方法被调用！");
            return name;
        }
        public void setName(String name) {
            System.out.println("setName 方法被调用！");
            this.name = name;
        }
    }

    private static final String testCaseA1 = "Hello World";
    private static final String testCaseA2 = "{\"@type\":\"cn.moonlord.test.FastJsonTestController$SimpleObject\",\"name\":\"Hello World\"}";

    @ApiOperation(value="测试用例 A1，使用 SerializerFeature.WriteClassName 特性，将对象转换为 Json 字符串")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "name", example = testCaseA1)})
    @GetMapping(value = "/TestA1")
    public String TestA1(@RequestParam String name) {
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.setName(name);
        String jsonString = JSON.toJSONString(simpleObject, SerializerFeature.WriteClassName);
        System.out.println("simpleObject : " + jsonString);
        return jsonString;
    }

    @ApiOperation(value="测试用例 A2，使用 ParserConfig.global.setAutoTypeSupport，将包含 @type 的 Json 字符串解析为对象，对象的构造方法和 getter / setter 方法都会被执行")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA2")
    public String TestA2(@RequestParam String JsonString) {
        ParserConfig.global.setAutoTypeSupport(true);
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A3，使用 ParserConfig.global.addAccept，限制 @type 的 Json 对象的类型的允许范围")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA3")
    public String TestA3(@RequestParam String JsonString) {
        ParserConfig.global.clearDeserializers();
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.addAccept("cn.moonlord.");
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A4，使用 ParserConfig.global.addDeny，限制 @type 的 Json 对象的类型的禁止范围")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA4")
    public String TestA4(@RequestParam String JsonString) {
        ParserConfig.global.clearDeserializers();
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.addAccept("cn.moonlord.safe.");
        ParserConfig.global.addDeny("cn.moonlord.test.");
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

}
