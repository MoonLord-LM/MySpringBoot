package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Api("使用不同的 XML 解析库，解析参数中的 xml，测试是否存在 XXE 漏洞")
@RestController
@RequestMapping("/xxe")
public class XXETestController {

    @ApiOperation(value="DOM 库，测试 1", notes="使用默认设置")
    @PostMapping(value = "/dom/test1")
    public String domTest1(@RequestBody String inputXML) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        // output
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

    @ApiOperation(value="DOM 库，测试 2", notes="使用安全的设置")
    @PostMapping(value = "/dom/test2")
    public String domTest2(@RequestBody String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        // output
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

}
