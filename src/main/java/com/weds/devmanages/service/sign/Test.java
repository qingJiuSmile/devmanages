package com.weds.devmanages.service.sign;

import cn.hutool.crypto.digest.MD5;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        SortedMap<String, Object> treeMap = new TreeMap<>();
     /*   treeMap.put("suc","123");
        treeMap.put("app","123");*/
        String sign = createSign(treeMap, "123");
        System.out.println(sign);

    }

    public static String createSign(SortedMap<String, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbkey = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        // 所有参与传参的参数按照accsii排序（升序）
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            // 空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
                sbkey.append(k + "=" + v + "&");
            }
        }
        sbkey = sbkey.append("key=" + key);
        System.out.println("字符串:" + sbkey.toString());
        //MD5加密,结果转换为大写字符
        String sign = MD5.create().digestHex(sbkey.toString()).toUpperCase();
        System.out.println("MD5加密值:" + sign);
        return sign;
    }


}
