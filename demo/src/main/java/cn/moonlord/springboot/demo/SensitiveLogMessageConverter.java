package cn.moonlord.springboot.demo;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class SensitiveLogMessageConverter extends MessageConverter {

    private static final List<String> SENSITIVE_MESSAGE_WORDS = Arrays.asList (
            "Password",
            "Secret",
            "Key",
            "Token",
            "Phone",
            "Email",
            "Card Number",
            "License Number",
            "Passport Number",
            "Social Security Number",
            "Location",
            "Position",
            "Session Id",
            "Cookie"
    );

    private static final List<String> SENSITIVE_MESSAGE_SUFFIXES = Arrays.asList (
            ":{}",
            ": {}",
            " :{}",
            " : {}",
            ":  {}",
            " :  {}",
            "  :{}",
            "  : {}",
            "  :  {}",
            "={}",
            "= {}",
            " ={}",
            " = {}",
            "=={}",
            "== {}",
            " =={}",
            " == {}",
            "==={}",
            "=== {}",
            " ==={}",
            " === {}"
    );

    private static final CopyOnWriteArrayList<String> SENSITIVE_MESSAGE_PATTERNS = new CopyOnWriteArrayList<>();

    private static final int SENSITIVE_MESSAGE_MAX_SEARCH_LENGTH = 256;

    private static final List<String> SENSITIVE_ARGUMENT_PREFIXES = Arrays.asList (
            "{cipher}",
            "{saas-cipher}",
            "{isdp-cipher}"
    );

    private static final String SENSITIVE_LOG_MESSAGE = "[*** sensitive log masked by SDCloud ***]";

    static {
        for (String sensitiveMessageWord : SENSITIVE_MESSAGE_WORDS) {
            for (String sensitiveMessageSuffix : SENSITIVE_MESSAGE_SUFFIXES) {
                String pattern = sensitiveMessageWord + sensitiveMessageSuffix;
                String lowerPattern = pattern.toLowerCase(Locale.ROOT);
                String upperPattern = pattern.toUpperCase(Locale.ROOT);
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(pattern);
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(lowerPattern);
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(upperPattern);
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(pattern.replace(" ", ""));
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(lowerPattern.replace(" ", ""));
                SENSITIVE_MESSAGE_PATTERNS.addIfAbsent(upperPattern.replace(" ", ""));
            }
        }
    }

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
                if(message.length() <= SENSITIVE_MESSAGE_MAX_SEARCH_LENGTH) {
                    for (String pattern : SENSITIVE_MESSAGE_PATTERNS) {
                        if (message.contains(pattern)) {
                            message = message.replace(pattern, pattern.replace("{}", SENSITIVE_LOG_MESSAGE));
                        }
                    }
                    Field messageField = event.getClass().getDeclaredField("message");
                    messageField.setAccessible(true);
                    messageField.set(event, message);
                }
            }
            catch (Exception ignored) { }
            // replace arguments
            try {
                Object[] arguments = event.getArgumentArray();
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] instanceof String) {
                        for (String prefix : SENSITIVE_ARGUMENT_PREFIXES) {
                            if (((String) arguments[i]).startsWith(prefix)) {
                                arguments[i] = SENSITIVE_LOG_MESSAGE;
                            }
                        }
                    }
                }
            }
            catch (Exception ignored) { }
        }
        return event.getFormattedMessage();
    }

}
