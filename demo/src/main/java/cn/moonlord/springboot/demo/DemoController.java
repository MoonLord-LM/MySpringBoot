package cn.moonlord.springboot.demo;

import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping("/**")
    public String index() throws Exception {
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
        logger.error(new String(new char[100000]));
        logger.error(new String(new char[100000]));
        logger.error(new String(new char[100000]));
        return out.toString();
    }

}
