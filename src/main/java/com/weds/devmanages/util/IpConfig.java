package com.weds.devmanages.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;
import java.util.regex.Pattern;

public class IpConfig {

    private Pattern numberPattern = Pattern.compile("^[-\\+]?[\\d]*$");
    /**
     * 判断是否是数字
     *
     * @param str 字符串
     * @return boolean
     **/
    private boolean isInteger(String str) {
        Pattern pattern = this.numberPattern;
        return pattern.matcher(str).matches();
    }
    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断 return true，合法
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        Thumbnails.of("D:\\J-USER\\desktop7\\项目定制\\123.jpg").size(800,1280).toFile(System.getProperty("user.dir")+"/test.jpg");
    }
}
