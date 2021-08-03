/**
 * 
 */
package com.weds.devmanages.util;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** 
* 调用一卡通签名工具类
* @since: 2018年5月4日  下午2:58:13
* @history:
*/
public class SignUtil {

    public static final String PARAM_SIGN       = "sign";
    public static final String PARAM_STARTWITH  = "t_";
    public static final String serialVersionUID = "serialVersionUID";
    public static final String PARAM_OPERATORID = "operatorid";
    public static final String PARAM_SPLIT      = "|";

    /**
         *    校验签名(RSA+MD5),相等返回true，不相等返回false
     * 
     * @param map 参数
     * @param key 密钥
     * @return
     */
    public static boolean checkSignRsa(Map<String, Object> map, String key) {
        Object signObj = map.get(PARAM_SIGN);
        if (signObj == null) {
            throw new RuntimeException("未获取到sign参数");
        }
        String md5Params = Md5Util.md5(getSignParams(map));
        String decryptByPublicKey = getSignByRsa(map, key);
        if (StringUtils.equals(decryptByPublicKey, md5Params)) {
            return true;
        }
        return false;
    }

    /**
         *  校验签名(Md5两次)，相等返回true，不相等返回false
     *
     * @param map 参数
     * @param key 密钥
     * @return 
     * @create  2020年7月16日 下午1:14:22 luochao
     * @history
     */
    public static boolean checkSignMd5(Map<String, Object> map, String key) {
        Object signObj = map.get(PARAM_SIGN);
        if (signObj == null) {
            throw new RuntimeException("未获取到sign参数");
        }
        String signByMd5 = getSignByMd5(map, key);
        if (signObj.toString().equals(signByMd5)) {
            return true;
        }
        return false;
    }

    /**
     * 获取需要签名的数据,未进行MD5
     *
     * @param map 参数集合
     * @return 
     * @create  2020年2月8日 下午2:00:35 luochao
     * @history
     */
    public static String getSignParams(Map<String, Object> map) {
        // 根据KEY排序
        Map<String, String> sortedParamMap = new TreeMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //略过sign、serialVersionUID、以t_开头的参数
            if (entry.getKey().equalsIgnoreCase(PARAM_SIGN) || entry.getKey().toLowerCase().startsWith(PARAM_STARTWITH)
                || entry.getKey().equalsIgnoreCase(serialVersionUID)) {
                continue;
            }
            sortedParamMap.put(entry.getKey(), entry.getValue().toString());
        }
        // 最终结果
        List<String> allList = new LinkedList<>();
        for (String value : sortedParamMap.values()) {
            if (StringUtils.isNotEmpty(value)) {
                allList.add(value);
            }
        }
        return StringUtils.join(allList.iterator(), PARAM_SPLIT);
    }

    /**
     * 获取签名后字符串(MD5两次)
     * 
     * @param paramMap
     * @param key
     * @return 
     * @create  2020年6月28日 下午6:53:51 luochao
     * @history
     */
    public static String getSign(Map<String, Object> paramMap, String key) {
        return getSignByMd5(paramMap, key);
    }

    /**
         *  根据key，md5两次
     *
     * @param paramMap
     * @param key
     * @return 
     * @create  2020年7月16日 下午1:03:58 luochao
     * @history
     */
    public static String getSignByMd5(Map<String, Object> paramMap, String key) {
        String md5F = Md5Util.md5(getSignParams(paramMap) + PARAM_SPLIT + key);
        return Md5Util.md5(md5F + PARAM_SPLIT + key);
    }

    /**
         *  根据key,rsa解密
     *
     * @param paramMap
     * @param key
     * @return 
     * @create  2020年7月16日 下午1:08:29 luochao
     * @history
     */
    public static String getSignByRsa(Map<String, Object> paramMap, String key) {
        String decryptByPublicKey = "";
        try {
            decryptByPublicKey = RSAUtil3.decryptByPublicKey(paramMap.get(PARAM_SIGN).toString(), key);
        } catch (Exception e) {
            throw new RuntimeException("解密失败");
        }
        return decryptByPublicKey;
    }
}
