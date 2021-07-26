package com.weds.devmanages.util;

import cn.hutool.core.codec.Base64;
import net.coobird.thumbnailator.Thumbnails;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils{

    /**
     * 将base64 的图片压缩
     * @param base64Img
     * @return
     */
    public static String resizeImageTo200K(String base64Img) {
        try {
            BufferedImage src = base64String2BufferedImage(base64Img);
            BufferedImage output = Thumbnails.of(src)
                    .size(800, 1280)
                    .keepAspectRatio(false)
                    .asBufferedImage();
            /* if (base64.length() - base64.length() / 8 * 2 > 200000) {
                output = Thumbnails.of(output).scale(1f).asBufferedImage();
                base64 = imageToBase64(output);
            }*/
            return imageToBase64(output);
        } catch (Exception e) {
            return base64Img;
        }
    }

    public static String imageToBase64(BufferedImage bufferedImage) {
        Base64 encoder = new Base64();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", baos);
        } catch (IOException e) {
        }
        return new String(Base64.encode((baos.toByteArray())));
    }

    /**
     * 将base64 转为流
     * @param base64string
     * @return
     */
    public static BufferedImage base64String2BufferedImage(String base64string) {
        BufferedImage image = null;
        try {
            InputStream stream = BaseToInputStream(base64string);
            image = ImageIO.read(stream);
        } catch (IOException e) {
        }
        return image;
    }

    private static InputStream BaseToInputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return stream;
    }


    /**
     * 将网络图片 base64
     * @param remark2
     * @return
     */
    public static String encodeImgageToBase64ByNetWork(String remark2) {
        System.out.println("remark2 == " + remark2);
        String remark = remark2.replace("https", "http");
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] buffer = null;
        try {
            // 创建URL
            URL url = new URL(remark);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(100000);
            inputStream = conn.getInputStream();
            outputStream = new ByteArrayOutputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        return new BASE64Encoder().encode(buffer);
    }

    public static Integer imageSize(String imageBase64Str){

        //1.找到等号，把等号也去掉(=用来填充base64字符串长度用)
        Integer equalIndex= imageBase64Str.indexOf("=");
        if(imageBase64Str.indexOf("=")>0) {
            imageBase64Str=imageBase64Str.substring(0, equalIndex);
        }
        //2.原来的字符流大小，单位为字节
        Integer strLength=imageBase64Str.length();
        System.out.println("imageBase64Str Length = "+strLength);
        //3.计算后得到的文件流大小，单位为字节
        Integer size=strLength-(strLength/8)*2;
        return size;
    }

    public static String bytesToKB(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 1, BigDecimal.ROUND_DOWN).floatValue();
        if (returnValue > 1) {
            return (returnValue + "MB");
        }

        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 1, BigDecimal.ROUND_DOWN).floatValue();
        return (returnValue + "KB");
    }

    public static void main(String[] args) {
        String urlpath = "https://bjjg.oss-cn-beijing.aliyuncs.com/20201225/4171ad9fb7664138a1dba6ebed47b372.jpeg";
        // 网络图片转base64
        String base64 = encodeImgageToBase64ByNetWork(urlpath);
        // 压缩之前获取图片大小
        Integer size12 = imageSize(base64);
        System.out.println("压缩之<<<前>获取图片字节数 = "+size12);
        //把字节转换单位为kb或mb
        String size22 = bytesToKB(size12);
        System.out.println("压缩之前把字节数转为KB或MB = "+size22);

        //开始压缩
        String base642 = resizeImageTo200K(base64);
        // 压缩之后获取图片大小
        Integer size = imageSize(base642);
        System.out.println("压缩之<后>>>获取图片字节数 = "+size);
        //把字节转换单位为kb或mb
        String size2 = bytesToKB(size);
        System.out.println("压缩之前把字节数转为KB或MB = "+size2);
    }
}

