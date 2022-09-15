package cn.moonlord.springboot;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class CustomLogbackMessageConverter extends MessageConverter {

    @Value("logback.sensitive.words")
    public void setSensitiveWords(String sensitiveWords){

    }

    @Override
    public String convert(ILoggingEvent event) {
        // clear formattedMessage
        try {
            Field formattedMessageField = event.getClass().getDeclaredField("formattedMessage");
            formattedMessageField.setAccessible(true);
            formattedMessageField.set(event, null);
        }
        catch (Exception ignored) {
        }

        // replace message
        String message = event.getMessage();

        // replace arguments
        Object[] arguments = event.getArgumentArray();
        for (Object argument : arguments) {
            if (argument instanceof String) {
            }
        }

        String formattedMessage = event.getFormattedMessage();
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
