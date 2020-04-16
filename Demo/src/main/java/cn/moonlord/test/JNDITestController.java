package cn.moonlord.test;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Api(tags = "JNDI 测试")
@RestController
@RequestMapping("/JNDI")
public class JNDITestController {

    public interface Task extends Remote {
        public Object execute(String input) throws RemoteException;
    }

    public class TaskEngine implements Task {
        @Override
        public Object execute(String input) throws RemoteException {
            System.out.println("input : " + input);
            return input;
        }
    }

    @ApiOperation(value="测试用例 A1，启动 RMI 服务端")
    @GetMapping(value = "/TestA1")
    @ApiImplicitParams({@ApiImplicitParam(name = "listenPort", value = "listenPort", example = "9000")})
    public String TestA1(@RequestParam Integer listenPort) throws Exception {
        TaskEngine taskEngine = new TaskEngine();
        Registry registry = LocateRegistry.createRegistry(listenPort);
        registry.rebind("task", taskEngine);
        return taskEngine.toString();
    }

    @ApiOperation(value="测试用例 A2，启动 RMI 客户端")
    @GetMapping(value = "/TestA2")
    @ApiImplicitParams({@ApiImplicitParam(name = "serverPort", value = "serverPort", example = "9000")})
    public String TestA2(@RequestParam Integer serverPort) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", serverPort);
        Task task = (Task) registry.lookup("task");
        return task.execute("HelloWorld").toString();
    }

}
