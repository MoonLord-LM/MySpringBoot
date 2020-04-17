package cn.moonlord.test;

import io.swagger.annotations.*;
import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
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

    public static class TaskEngine extends UnicastRemoteObject implements Task, Serializable {
        public TaskEngine() throws RemoteException { }
        @Override
        public Object execute(Message message) throws RemoteException {
            logger.info("TaskEngine.execute : " + message + " " + message.message);
            return message.message;
        }
    }

    @ApiOperation(value="测试用例 A1，启动 RMI 服务端")
    @GetMapping(value = "/TestA1")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "listenPort", example = "9000")})
    public String TestA1(@RequestParam Integer registryPort) throws Exception {
        TaskEngine taskEngine = new TaskEngine();
        logger.info("Server taskEngine : " + taskEngine);
        Registry registry = LocateRegistry.createRegistry(registryPort);
        logger.info("Server registry : " + registry);
        registry.bind("task", taskEngine);
        for(String name : registry.list()){
            logger.info("Server registry name : " + name);
        }
        return taskEngine.toString();
    }

    @ApiOperation(value="测试用例 A2，启动 RMI 客户端")
    @GetMapping(value = "/TestA2")
    @ApiImplicitParams({@ApiImplicitParam(name = "registryPort", value = "serverPort", example = "9000")})
    public String TestA2(@RequestParam Integer registryPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", registryPort);
        logger.info("Client registry : " + registry);
        for(String name : registry.list()){
            logger.info("Client registry name : " + name);
        }
        Task task = (Task) registry.lookup("task");
        logger.info("Client task : " + task);
        Message message = new Message("Hello World");
        logger.info("Client message : " + message + " " + message.message);
        return task.execute(message).toString();
    }

}
