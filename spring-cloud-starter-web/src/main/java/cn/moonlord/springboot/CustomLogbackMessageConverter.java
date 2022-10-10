package cn.moonlord.springboot;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RefreshScope
@Component
public class CustomLogbackMessageConverter extends MessageConverter {

    private static final CopyOnWriteArrayList<String> sensitiveWords = new CopyOnWriteArrayList<>();

    @Value("${logback.converter.sensitive-words: password, secret, token, phone, email }")
    public void setSensitiveWords(String config) {
        config = config.trim();
        if (StringUtils.hasLength(config)) {
            List<String> words = Arrays.asList(config.split(","));
            for (int i = 0; i < words.size(); i++) {
                words.set(i, words.get(i).trim());
            }
            sensitiveWords.addAllAbsent(words);
            sensitiveWords.removeIf(word -> !words.contains(word));
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        // message
        String message = event.getMessage();

        // arguments
        Object[] argumentArray = event.getArgumentArray();

        // replace arguments
        String formattedMessage = MessageFormatter.arrayFormat(message, argumentArray).getMessage();

        // check message
        formattedMessage = getNoSensitiveWordsString(formattedMessage, sensitiveWords);
        formattedMessage = getControlSafeString(formattedMessage);

        return formattedMessage;
    }

    public static String getControlSafeString(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char tempChar = source.charAt(i);
            if (Character.isISOControl(tempChar)) {
                String safeChar = String.format("[\\u%04x]", (int) tempChar);
                safeChar = safeChar.replace("[\\u0000]", "[\\0]");
                safeChar = safeChar.replace("[\\u0008]", "[\\b]");
                safeChar = safeChar.replace("[\\u0009]", "[\\t]");
                safeChar = safeChar.replace("[\\u000a]", "[\\n]");
                safeChar = safeChar.replace("[\\u000c]", "[\\f]");
                safeChar = safeChar.replace("[\\u000d]", "[\\r]");
                result.append(safeChar);
            } else {
                result.append(tempChar);
            }
        }
        return result.toString();
    }

    public static String getNoSensitiveWordsString(String source, List<String> sensitiveWords) {
        for (String sensitiveWord : sensitiveWords) {
            if (source.contains(sensitiveWord)) {
                return source.substring(0, source.indexOf(sensitiveWord)) + "/*** sensitive data [ " + sensitiveWord + " ] is hidden ***/";
            }
        }
        return source;
    }

}
