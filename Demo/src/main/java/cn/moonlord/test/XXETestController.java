package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.*;

@Api("测试 XXE 漏洞")
@RestController
@RequestMapping("/xxe")
public class XXETestController {

    @ApiOperation(value="测试 JAXP 库", notes="解析参数中的 xml，测试是否存在 XXE 漏洞")
    @PostMapping(value = "/xstream")
    public String testXStream() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse("???");
        return "OK";
    }

}
