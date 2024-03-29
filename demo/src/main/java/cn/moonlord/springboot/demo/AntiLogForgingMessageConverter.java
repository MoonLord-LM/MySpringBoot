package cn.moonlord.springboot.demo;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.lang.reflect.Field;

public class AntiLogForgingMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        if(event instanceof LoggingEvent) {
            // clear formattedMessage
            try {
                Field formattedMessageField = event.getClass().getDeclaredField("formattedMessage");
                formattedMessageField.setAccessible(true);
                formattedMessageField.set(event, null);
            }
            catch (Exception ignored) { }
            // replace message
            try {
                String message = event.getMessage();
                message = message.replace("\r", "[\\r]");
                message = message.replace("\n", "[\\n]");
                Field messageField = event.getClass().getDeclaredField("message");
                messageField.setAccessible(true);
                messageField.set(event, message);
            }
            catch (Exception ignored) { }
            // replace arguments
            try {
                Object[] arguments = event.getArgumentArray();
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] instanceof String) {
                        arguments[i] = ((String) arguments[i]).replace("\r", "[\\r]");
                        arguments[i] = ((String) arguments[i]).replace("\n", "[\\n]");
                    }
                }
            }
            catch (Exception ignored) { }
        }
        return event.getFormattedMessage();
    }

}
