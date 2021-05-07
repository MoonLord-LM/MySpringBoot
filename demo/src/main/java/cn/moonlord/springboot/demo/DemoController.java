package cn.moonlord.springboot.demo;

import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class DemoController {

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
        return out.toString();
    }

}
