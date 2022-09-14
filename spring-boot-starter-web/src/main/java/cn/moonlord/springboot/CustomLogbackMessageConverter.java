package cn.moonlord.springboot;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.lang.reflect.Field;

public class CustomLogbackMessageConverter extends MessageConverter {

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
