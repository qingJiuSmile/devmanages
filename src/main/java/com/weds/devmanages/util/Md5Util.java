package com.weds.devmanages.util;

import java.security.MessageDigest;

/**
 * md5
 *
 * @since: 2020年2月8日  下午2:43:15
 * @history:
 */
public class Md5Util {

    /**
     *对字符串进行md5,各个语言均有md5
     *
     * @param str
     * @return 
     * @create  2020年2月18日 下午4:11:01 luochao
     * @history
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //原因在于汉字编码，在加密时设置一下编码UTF-8，问题解决。
            md.update(str.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}