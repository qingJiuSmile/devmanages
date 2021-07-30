package com.weds.devmanages.config.signature;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.util.HMACSHA256;
import com.weds.devmanages.util.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Slf4j
public class SignUtil {

    private static final int TIME_OUT = 60;

    private static final String DEFAULT_SECRET = "weds@WSX#$%&";

    public static String sign(String body, Map<String, String[]> params, String[] paths, String appId, String appSecret, String timestamp) {

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
        sb.append("appId=").append(appId).append("appSecret=").append(appSecret).append("timestamp=").append(timestamp);
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
        return MD5.create().digestHex(sbKey.toString()).toUpperCase();
    }


    /**
     * 检查校验参数签名
     *
     * @author tjy
     **/
    public static void checkSign(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String oldSign = request.getHeader("WEDS-SIGN");
        String appId = request.getHeader("appId");
        String appSecret = request.getHeader("appSecret");
        String timestamp = request.getHeader("timestamp");

        if (StringUtils.isBlank(oldSign) || StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret) || StringUtils.isBlank(timestamp)) {
            throw new Exception("header required verification parameters are missing");
        }

        if (!isInteger(timestamp)) {
            throw new Exception("timestamp not a number");
        }

        // 检查appId是否存在 TODO 查库/缓存/配置文件
        if (!"321".equals(appId)) {
            throw new Exception("appId not found");
        }
        // 检查appSecret是否合法 TODO 查库/缓存/配置文件
        if (!"123".equals(appSecret)) {
            throw new Exception("appSecret not found");
        }

        // 检查header头上的timestamp时间与当前时间对比是否超出5分钟
        long min = LocalDateTimeUtils.betweenTwoTime(LocalDateTimeUtil.of(Long.parseLong(timestamp)), LocalDateTime.now(), "second");
        log.info("时间戳对比 ==> [{}]", min);
        if (min > TIME_OUT) {
            throw new Exception("request time out");
        }

        // 获取body（对应@RequestBody）
        String body = "";
        body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        // 获取parameters（对应@RequestParam）
        Map<String, String[]> params = null;
        if (!CollectionUtils.isEmpty(request.getParameterMap())) {
            params = request.getParameterMap();
        }

        // 获取path variable（对应@PathVariable）
        String[] paths = null;
        ServletWebRequest webRequest = new ServletWebRequest(request, null);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            paths = uriTemplateVars.values().toArray(new String[]{});
        }

        String newSign = SignUtil.sign(body, params, paths, appId, appSecret, timestamp);
        log.info("请求签名为 ==> [{}] <==> [{}]", oldSign, newSign);
        if (!newSign.equals(oldSign)) {
            throw new Exception("inconsistent signatures...");
        }
    }


    /**
     * 判断是否是数字
     *
     * @param str 字符串
     * @return boolean
     **/
    private static boolean isInteger(String str) {
        Pattern pattern = compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
        map.put("appId", 1);
        map.put("appSecret", 5);
        map.put("timestamp","123");
        Map<String, String[]> params = new HashMap<>();
      /*  params.put("var3", new String[]{"3"});
        params.put("var4", new String[]{"4"});*/

        String[] paths = new String[]{"10.17.1.126"};
        long ks = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis());

        System.out.println(sign(map.isEmpty() ? null : JSONObject.toJSONString(map), null, null, "321", "123", ks + ""));

    }

}