package com.weds.devmanages.config.signature;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.util.HMACSHA256;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class SignUtil {
    private static final String DEFAULT_SECRET = "weds@WSX#$%&";

    public static String sign(String body, Map<String, String[]> params, String[] paths, String appSecret) {

        if (StringUtils.isBlank(appSecret)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(body)) {
            // json字符串转map
            JSONObject jsonObject = JSONObject.parseObject(body);
            SortedMap<String, Object> treeMap = new TreeMap<>();
            jsonObject.forEach(treeMap::put);
            sb.append(createSign(treeMap, appSecret)).append("_");
        }

        if (!CollectionUtils.isEmpty(params)) {
            params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(paramEntry -> {
                        String paramValue = String.join(",", Arrays.stream(paramEntry.getValue()).sorted().toArray(String[]::new));
                        sb.append(paramEntry.getKey()).append("=").append(paramValue);
                    });
        }

        if (ArrayUtils.isNotEmpty(paths)) {
            sb.append("_");
            String pathValues = String.join(",", Arrays.stream(paths).sorted().toArray(String[]::new));
            sb.append(pathValues).append("_");
        }
        sb.append("appSecret=").append(appSecret);
        log.info("加密前  ==> [{}]", sb.toString());
        String resultSign = HMACSHA256.sha256_HMAC(appSecret, sb.toString());
        log.info("实际签名 ==> [{}]", resultSign);
        return resultSign;
    }

    public static String createSign(SortedMap<String, Object> parameters, String key) {
        StringBuilder sbKey = new StringBuilder();
        Set es = parameters.entrySet();
        // 所有参与传参的参数按照accsii排序（升序）
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            // 空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sbKey.append(k).append("=").append(v).append("&");
            }
        }
        sbKey = sbKey.append("key=").append(key);
        //MD5加密,结果转换为大写字符
        log.info("排序后字段 ==>[{}]", sbKey);
        String sign = MD5.create().digestHex(sbKey.toString()).toUpperCase();
        return sign;
    }


    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", "123");
        map.put("age", 26);
        Map<String, String[]> params = new HashMap<>();
        params.put("var3", new String[]{"3"});
        params.put("var4", new String[]{"4"});

        String[] paths = new String[]{"1", "2"};


        System.out.println(sign(JSONObject.toJSONString(map), params, paths, "123"));
    }

}