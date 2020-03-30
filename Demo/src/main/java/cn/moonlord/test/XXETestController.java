package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Api(tags = "XXE 测试")
@RestController
@RequestMapping("/xxe")
public class XXETestController {

    private static final String testCase1 =
            "<!DOCTYPE example [" + "\r\n" +
                    "<!ENTITY file \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY xxe1 SYSTEM \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY http \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
                    "<!ENTITY xxe2 SYSTEM \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
                    "<!ENTITY send \"http://localhost:8080/xxe/sendDataBack?data=&file;&xxe1;\" >" + "\r\n" +
                    "<!ENTITY xxe3 SYSTEM \"http://localhost:8080/xxe/sendDataBack?data=&file;&xxe1;\" >" + "\r\n" +
            " ]>" + "\r\n" +
            "<example>" + "\r\n" +
                    "<file>&file;</file>" + "\r\n" +
                    "<xxe1>&xxe1;</xxe1>" + "\r\n" +
                    "<http>&http;</http>" + "\r\n" +
                    "<xxe2>&xxe2;</xxe2>" + "\r\n" +
                    "<send>&send;</send>" + "\r\n" +
                    "<xxe3>&xxe3;</xxe3>" + "\r\n" +
            "</example>";

    private static final String testCase2 =
            "<!DOCTYPE recursion [" + "\r\n" +
                    "<!ENTITY a \"This is a simple element.\r\n\" >" + "\r\n" +
                    "<!ENTITY b \"&a;&a;&a;&a;&a;&a;&a;&a;&a;&a;\" >" + "\r\n" +
                    "<!ENTITY c \"&b;&b;&b;&b;&b;&b;&b;&b;&b;&b;\" >" + "\r\n" +
                    "<!ENTITY d \"&c;&c;&c;&c;&c;&c;&c;&c;&c;&c;\" >" + "\r\n" +
                    "<!ENTITY e \"&d;&d;&d;&d;&d;&d;&d;&d;&d;&d;\" >" + "\r\n" +
                    "<!ENTITY f \"&e;&e;&e;&e;&e;&e;&e;&e;&e;&e;\" >" + "\r\n" +
                    "<!ENTITY g \"&f;&f;&f;&f;&f;&f;&f;&f;&f;&f;\" >" + "\r\n" +
                    "<!ENTITY h \"&g;&g;&g;&g;&g;&g;&g;&g;&g;&g;\" >" + "\r\n" +
                    "<!ENTITY i \"&h;&h;&h;&h;&h;&h;&h;&h;&h;&h;\" >" + "\r\n" +
            " ]>" + "\r\n" +
            "<recursion>" + "\r\n" +
                    "<a>&a;</a>" + "\r\n" +
                    "<b>&b;</b>" + "\r\n" +
                    "<c>&b;</c>" + "\r\n" +
                    "<d>&d;</d>" + "\r\n" +
                    "<e>&e;</e>" + "\r\n" +
                    "<f>&f;</f>" + "\r\n" +
                    "<g>&g;</g>" + "\r\n" +
                    "<h>&h;</h>" + "\r\n" +
                    "<i>&i;</i>" + "\r\n" +
            "</recursion>";

    private static void initJAXP(){
        System.setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );
        System.setProperty(
                "javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"
        );
    }

    @ApiIgnore
    @GetMapping(value = "/sendDataBack")
    public String sendDataBack(@RequestParam String data, HttpServletRequest request) {
        data =
                "<![CDATA[" + "\r\n" +
                "sendDataBack" + "\r\n" +
                "[URL: " + request.getRequestURL() + "?" + request.getQueryString() +"]" + "\r\n" +
                "[data length: " + data.length() +"]" + "\r\n" +
                data + "\r\n" +
                "]]>"
        ;
        System.err.println(data);
        return data;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 1，使用默认的设置解析 XML，存在 XXE 漏洞")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/test1")
    public String JAXPTest1(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 2，在测试用例 1 的基础上，关闭实体引用扩展 setExpandEntityReferences(false)，但是实体仍会被解析，http 请求仍会发送")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/test2")
    public String JAXPTest2(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        documentBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 3，禁用外部实体和 DTD 引用，可防御 XXE 攻击")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/test3")
    public String JAXPTest3(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 4，禁用 DTD 声明，直接报错，可防御 XXE 攻击")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/test4")
    public String JAXPTest4(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 5，受到 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，默认的设置下，最多支持 64000 个实体扩展，超出时会直接报错")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase2))
    @GetMapping(value = "/japx/test5")
    public String JAXPTest5(@RequestParam String inputXML) throws Exception {
        return JAXPTest2(inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 6，关闭 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，堆内存溢出，导致拒绝服务")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase2))
    @GetMapping(value = "/japx/test6")
    public String JAXPTest6(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 7，在测试用例 6 的基础上，关闭实体引用扩展 setExpandEntityReferences(false)，但是实体仍会被解析，堆内存溢出，导致拒绝服务")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase2))
    @GetMapping(value = "/japx/test7")
    public String JAXPTest7(@RequestParam String inputXML) throws Exception {
        // input
        initJAXP();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
        // output
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return result;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 8，禁用 DTD 声明，直接报错，实体不会被解析")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase2))
    @GetMapping(value = "/japx/test8")
    public String JAXPTest8(@RequestParam String inputXML) throws Exception {
        return JAXPTest4(inputXML);
    }

}
