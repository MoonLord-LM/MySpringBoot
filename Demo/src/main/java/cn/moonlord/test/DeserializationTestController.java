package cn.moonlord.test;

import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Api(tags = "反序列化 测试")
@RestController
@RequestMapping("/Deserialization")
public class DeserializationTestController {

    private static class SimpleObject implements Serializable {
        public String name;
        public SimpleObject(String name) { this.name = name; }
        public String toString(){ return "{\"name\":\"" + name + "\"}"; }
    }

    private static final String testCaseA1 = "Hello World";

    @ApiOperation(value="测试用例 A1，序列化一个对象，并保存到文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestA1")
    public String TestA1(@RequestParam String name) throws Exception {
        String fileName = "A1.class";
        SimpleObject obj = new SimpleObject(name);
        FileOutputStream file = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(obj);
        out.close();
        file.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 A2，从文件中，反序列化对象")
    @GetMapping(value = "/TestA2")
    public String TestA2() throws Exception {
        String fileName = "A1.class";
        FileInputStream file = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(file);
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        file.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

}
