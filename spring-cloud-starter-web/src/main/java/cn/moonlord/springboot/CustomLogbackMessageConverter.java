package cn.moonlord.springboot;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RefreshScope
@Component
public class CustomLogbackMessageConverter extends MessageConverter {

    private static final CopyOnWriteArrayList<String> sensitiveWords = new CopyOnWriteArrayList<>();

    private static volatile String sensitiveMessage = Strings.EMPTY;

    @Value("logback.sensitive.words:password,secret,token,session,access")
    public void configSensitiveWords(String configSensitiveWords){
        if(StringUtils.hasLength(configSensitiveWords)){
            List<String> words = Arrays.asList(configSensitiveWords.split(","));
            for (int i = 0; i < words.size(); i++) {
                words.set(i, words.get(i).trim());
            }
            sensitiveWords.removeIf(word -> !words.contains(word));
            sensitiveWords.addAllAbsent(words);
        }
    }

    @Value("logback.sensitive.message:******")
    public void configSensitiveMessage(String configSensitiveMessage){
        sensitiveMessage = configSensitiveMessage;
    }

    @Override
    public String convert(ILoggingEvent event) {
        // replace message
        String message = event.getMessage();
        for (String sensitiveWord : sensitiveWords) {
            if (message.contains(sensitiveWord)) {
                message = sensitiveMessage;
                break;
            }
        }

        // replace arguments
        Object[] argumentArray = event.getArgumentArray();
        for (Object argument : argumentArray) {
            if (argument instanceof String) {
            }
        }

        String formattedMessage = MessageFormatter.arrayFormat(message, argumentArray).getMessage();
        formattedMessage = formattedMessage.replace("\r", "[\\r]"); //
        formattedMessage = formattedMessage.replace("\n", "[\\n]");
        formattedMessage = formattedMessage.replace("\t", "[\\t]");
        formattedMessage = formattedMessage.replace("\f", "[\\f]");
        formattedMessage = formattedMessage.replace("\b", "[\\b]");
        formattedMessage = formattedMessage.replace("\u000b", "[\\u000b]");
        formattedMessage = formattedMessage.replace("\u007f", "[\\u007f]");
        return formattedMessage;
    }

}
