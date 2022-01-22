package cn.moonlord.springboot.demo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;

@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping(value = "/**")
    public byte[] index(HttpServletRequest request, HttpServletResponse response, @RequestParam String className) throws Exception {
        /*
        String source = "${hello}";
        // String source = "${hello}<#assign test=\"freemarker.template.utility.Execute\"?new()>\n${test(\"systeminfo\")}";
        Map<String, Object> params = new HashMap<>();
        params.put("hello", "hello world");
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setEncoding(Locale.ROOT, StandardCharsets.UTF_8.toString());

        // 防止命令注入
        // freemarker.core._MiscTemplateException: Instantiating freemarker.template.utility.Execute is not allowed in the template for security reasons.
        cfg.setAPIBuiltinEnabled(false);
        cfg.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);

        Template template = new Template("temp", new StringReader(source), cfg);
        StringWriter out = new StringWriter();
        template.process(params, out);

        logger.info("\r\n" + "info 1: {}" + "\r\n" + "2: {}" + "\r\n", out + "\r\n" + out, new double[3]);
        logger.info("\r\n" + "crlf test" + "\r\n" + "\r\n" + "\r\n");
        logger.error("error", new IllegalArgumentException("test"));
        logger.error("debug password: {}", "huawei123");
        logger.error("warn ID card number: {}", "420922199309030000");
        //logger.error(new String(new char[100000]));
        //logger.error(new String(new char[100000]));
        //logger.error(new String(new char[100000]));
        */

        logger.info("className: {}", className);
        logger.info("package: {}", Class.forName(className).getPackage());

        URL location = Class.forName(className).getProtectionDomain().getCodeSource().getLocation();
        logger.info("location: {}", location);
        logger.info("file: {}", Class.forName(className).getProtectionDomain().getCodeSource().getLocation().getFile());

        String fileName = location.toString().substring(location.toString().lastIndexOf("/BOOT-INF/lib/") + "/BOOT-INF/lib/".length());
        fileName = fileName.replace(".jar!/", ".jar");
        fileName = fileName.substring(fileName.lastIndexOf("/") + "/".length());
        response.setHeader("Content-Type","application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName);

        if(location.toString().contains(".jar!/")){
            logger.info("class is in a lib jar");
            return IOUtils.toByteArray(location.openStream());
        }

        logger.info("class is in a local jar file");
        return FileUtils.readFileToByteArray(new File(location.getFile()));

        // http://127.0.0.1:8080/?className=com.alibaba.fastjson.JSON
        // http://127.0.0.1:8080/?className=org.apache.commons.io.FileUtils
        // ClassUtils.getDefaultClassLoader()
    }

}
