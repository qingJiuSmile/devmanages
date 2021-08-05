package com.weds.devmanages.service.pay;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.weds.devmanages.util.Md5Util;
import com.weds.devmanages.util.RSAUtil3;
import com.weds.devmanages.util.RestTemplateUtils;
import com.weds.devmanages.util.SignUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PayTest {

    @Autowired
    private RestTemplateUtils restTemplateUtils;


    @Data
    class SignParam {
        @ApiModelProperty("开发者id")
        private String ymAppId;

        private String sign;

        @ApiModelProperty("商户编码（代表一个学校的一个商户）")
        @JSONField(name = "cp_code")
        private String cpCode;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    class PreOrder extends SignParam {

        @ApiModelProperty("在微信公众号内发起支付，此参数必传,此参数为微信用户在商户对应appid下的唯一标识;" +
                "APP支付不需要填")
        @JSONField(name = "openid")
        private String openId;

        @ApiModelProperty("商户订单号，确保平台唯一性字母、数字或字线数字组合,长度最大为32字节")
        @JSONField(name = "cp_tran_no")
        private String cpTranNo;

        @ApiModelProperty("单位为分，如1元需传入100")
        @JSONField(name = "tran_money")
        private Integer tranMoney;

        @ApiModelProperty("商品名称，需要URLEncode提交（签名时使用原生值）长度最大为20个中文字符")
        @JSONField(name = "prod_name")
        private String prodName;

        @ApiModelProperty("业务类型，如充水费、缴学费需要URLEncode提交（签名时使用原生值）长度最大为20个中文字符")
        @JSONField(name = "tran_type")
        private String tranType;


    }

    /*
        ymAppId:2011031649342027
        appid:2009032008460272310
        cp_code:yiyun
        签名公钥:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbicqzabDYMeXzTdHMaRqMAM6e
        2hqfzJrF1AkNyNW7G0sAlkypppqqYf68FINedcN3W4GNbjxXi83rzeqO6HOwpp5a
        JfoxXGR2FWvyLt2au+j6/HS85VJEkGxvAP003rUMuJZgD+4iZTUUqQDq939ZzIMJ
        GSr2/3OBgiQERt9rkQIDAQAB

     */
    private static CloseableHttpClient httpsclient = HttpClientBuilder.create().build();

    public void testPay() throws Exception {
        String url = "https://open.lsmart.wang/routepay/route";
        String reqUrl = "/pay/unified/preOrder.shtml";
        url = url + reqUrl;
        HttpPost httpPost = new HttpPost(url);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        PreOrder preOrder = new PreOrder();
        preOrder.setCpTranNo(uuid.toString());
        preOrder.setProdName("测试");
        preOrder.setTranMoney(2000);
        preOrder.setTranType("充水费");
        preOrder.setYmAppId("2011031649342027");
        preOrder.setCpCode("yiyun");

        // 签名不参与加密

        String signParam = Md5Util.md5(SignUtil.getSignParams(JSONObject.parseObject(JSONObject.toJSONString(preOrder))));
        System.out.println(signParam);
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbicqzabDYMeXzTdHMaRqMAM6e\n" +
                "        2hqfzJrF1AkNyNW7G0sAlkypppqqYf68FINedcN3W4GNbjxXi83rzeqO6HOwpp5a\n" +
                "        JfoxXGR2FWvyLt2au+j6/HS85VJEkGxvAP003rUMuJZgD+4iZTUUqQDq939ZzIMJ\n" +
                "        GSr2/3OBgiQERt9rkQIDAQAB";

        preOrder.setSign(RSAUtil3.encryptByPublicKey(signParam, key));
        httpPost.setEntity(assembleParam(JSONObject.parseObject(JSONObject.toJSONString(preOrder)), ContentType.APPLICATION_FORM_URLENCODED));
        CloseableHttpResponse response = httpsclient.execute(httpPost);
        System.out.println(toString(response.getEntity()));
    }

    /***
     *
     * toString:httpEntity转换为字符串.
     *
     * @author luochao
     * @date 2018年1月27日 下午1:34:33
     * @param httpEntity
     * @return
     */
    protected static String toString(HttpEntity httpEntity) {
        String result = null;
        try {
            result = EntityUtils.toString(httpEntity, Consts.UTF_8);
        } catch (ParseException | IOException e) {
            throw new RuntimeException("转字符串异常", e);
        }
        return result;
    }


    public static Map<String, String> addHeader() {
        Map<String, String> headerMap = new HashMap<>(5);
        headerMap.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        return headerMap;
    }


    /***
     *
     * assembleParam:组装参数.
     *
     * @author luochao
     * @date 2018年1月27日 下午12:59:54
     * @param map 参数
     * @param contentType 参数类型
     * @return
     */
    protected static StringEntity assembleParam(Map<String, Object> map, ContentType contentType) {
        StringEntity stringEntity = null;
        if (ContentType.APPLICATION_JSON.equals(contentType)) {
            stringEntity = new StringEntity(JSON.toJSONString(map), "UTF-8");
            stringEntity.setContentType("application/json");
        } else {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            stringEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        }

        return stringEntity;
    }
}
