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

    // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Control_codes
    // https://en.wikipedia.org/wiki/C0_and_C1_control_codes
    // %00%01%02%03%04%05%06%07%08%09%0A%0B%0C%0D%0E%0F%10%11%12%13%14%15%16%17%18%19%1A%1B%1C%1D%1E%1F%7F%80%81%82%83%84%85%86%87%88%89%8A%8B%8C%8D%8E%8F%90%91%92%93%94%95%96%97%98%99%9A%9B%9C%9D%9E%9F
    // %C2%85
    private static final List<Character> controlCharacters = Arrays.asList(
            (char) 0x00,  //  NUL     \u0000
            (char) 0x01,  //  SOH     \u0001
            (char) 0x02,  //  STX     \u0002
            (char) 0x03,  //  ETX     \u0003
            (char) 0x04,  //  EOT     \u0004
            (char) 0x05,  //  ENQ     \u0005
            (char) 0x06,  //  ACK     \u0006
            (char) 0x07,  //  BEL     \u0007
            (char) 0x08,  //  BS      \b
            (char) 0x09,  //  HT      \t
            (char) 0x0A,  //  LF      \n
            (char) 0x0B,  //  VT      \u000b
            (char) 0x0C,  //  FF      \f
            (char) 0x0D,  //  CR      \r
            (char) 0x0E,  //  SO      \u000e
            (char) 0x0F,  //  SI       \u000f
            (char) 0x10,  //  DLE     \u0010
            (char) 0x11,  //  DC1     \u0011
            (char) 0x12,  //  DC2     \u0012
            (char) 0x13,  //  DC3     \u0013
            (char) 0x14,  //  DC4     \u0014
            (char) 0x15,  //  NAK     \u0015
            (char) 0x16,  //  SYN     \u0016
            (char) 0x17,  //  ETB     \u0017
            (char) 0x18,  //  CAN     \u0018
            (char) 0x19,  //  EM      \u0019
            (char) 0x1A,  //  SUB     \u001a
            (char) 0x1B,  //  ESC     \u001b
            (char) 0x1C,  //  FS      \u001c
            (char) 0x1D,  //  GS      \u001d
            (char) 0x1E,  //  RS      \u001e
            (char) 0x1F,  //  US      \u001f
            (char) 0x7F,  //  DEL     \u007f
            (char) 0x80,  //  PAD     \u0080
            (char) 0x81,  //  HOP     \u0081
            (char) 0x82,  //  BPH     \u0082
            (char) 0x83,  //  NBH     \u0083
            (char) 0x84,  //  IND     \u0084
            (char) 0x85,  //  NEL     \u0085
            (char) 0x86,  //  SSA     \u0086
            (char) 0x87,  //  ESA     \u0087
            (char) 0x88,  //  HTS     \u0088
            (char) 0x89,  //  HTJ     \u0089
            (char) 0x8A,  //  VTS     \u008a
            (char) 0x8B,  //  PLD     \u008b
            (char) 0x8C,  //  PLU     \u008c
            (char) 0x8D,  //  RI      \u008d
            (char) 0x8E,  //  SS2     \u008e
            (char) 0x8F,  //  SS3     \u008f
            (char) 0x90,  //  DCS     \u0090
            (char) 0x91,  //  PU1     \u0091
            (char) 0x92,  //  PU2     \u0092
            (char) 0x93,  //  STS     \u0093
            (char) 0x94,  //  CCH     \u0094
            (char) 0x95,  //  MW      \u0095
            (char) 0x96,  //  SPA     \u0096
            (char) 0x97,  //  EPA     \u0097
            (char) 0x98,  //  SOS     \u0098
            (char) 0x99,  //  SGC     \u0099
            (char) 0x9A,  //  SCI     \u009a
            (char) 0x9B,  //  CSI     \u009b
            (char) 0x9C,  //  ST      \u009c
            (char) 0x9D,  //  OSC      \u009d
            (char) 0x9E,  //  PM      \u009e
            (char) 0x9F   //  APC     \u009f
    );

    private static final CopyOnWriteArrayList<String> sensitiveWords = new CopyOnWriteArrayList<>();

    private static volatile String sensitiveMessage = Strings.EMPTY;

    @Value("logback.sensitive.words:password,secret,token,session,access")
    public void configSensitiveWords(String configSensitiveWords) {
        char c = '\b';
        if (StringUtils.hasLength(configSensitiveWords)) {
            List<String> words = Arrays.asList(configSensitiveWords.split(","));
            for (int i = 0; i < words.size(); i++) {
                words.set(i, words.get(i).trim());
            }
            sensitiveWords.removeIf(word -> !words.contains(word));
            sensitiveWords.addAllAbsent(words);
        }
    }

    @Value("logback.sensitive.message:******")
    public void configSensitiveMessage(String configSensitiveMessage) {
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
