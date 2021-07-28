package com.weds.devmanages.config.signature;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.util.HMACSHA256;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            jsonObject.forEach((k, v) -> sb.append(k).append("=").append(v).append('&'));
        }

        if (!CollectionUtils.isEmpty(params)) {
            params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(paramEntry -> {
                        String paramValue = String.join(",", Arrays.stream(paramEntry.getValue()).sorted().toArray(String[]::new));
                        sb.append(paramEntry.getKey()).append("=").append(paramValue).append('&');
                    });
        }

        if (ArrayUtils.isNotEmpty(paths)) {
            String pathValues = String.join(",", Arrays.stream(paths).sorted().toArray(String[]::new));
            sb.append(pathValues);
        }

        log.info("签名 ==> [{}]", sb.toString());
        return HMACSHA256.sha256_HMAC(appSecret, sb.toString());
    }

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
        map.put("name", "123");
        map.put("age", 26);
        Map<String, String[]> params = new HashMap<>();
        params.put("var3", new String[]{"3"});
        params.put("var4", new String[]{"4"});

        String[] paths = new String[]{"1", "2"};


        System.out.println(sign(JSONObject.toJSONString(map), params, paths, DEFAULT_SECRET));
    }

}