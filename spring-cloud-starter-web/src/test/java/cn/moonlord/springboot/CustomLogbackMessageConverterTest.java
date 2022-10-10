package cn.moonlord.springboot;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomLogbackMessageConverterTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogbackMessageConverterTest.class);

    public static final List<Character> controlCharacters = Arrays.asList(
            (char) 0x00,  //  NUL     \0
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
            (char) 0x0F,  //  SI      \u000f
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
            (char) 0x9D,  //  OSC     \u009d
            (char) 0x9E,  //  PM      \u009e
            (char) 0x9F   //  APC     \u009f
    );

    @Test
    public void test() {
        logger.info("控制字符测试 \0\b\t\n\f\r {}", "\0\b\t\n\f\r");
        logger.info("敏感数据测试 password: {}", "123456");
        logger.info("敏感数据测试 secret: {}", "123456");
        logger.info("敏感数据测试 token: {}", "123456");
        logger.info("敏感数据测试 phone: {}", "123456");
        logger.info("敏感数据测试 email: {}", "123456");
        for (Character controlCharacter : controlCharacters) {
            logger.info("{}", controlCharacter);
        }
    }

}
