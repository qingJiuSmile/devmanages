package com.weds.devmanages.service.pay;

import cn.hutool.core.lang.ObjectId;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        private String appId;

        @ApiModelProperty("商户编码（代表一个学校的一个商户）")
        @JSONField(name = "cp_code")
        private String cpCode;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class Cashier extends SignParam{
        @JSONField(name = "tran_no")
        private String tranNo;
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

        @ApiModelProperty("根据account_type确定传的内容")
        private String account;

        @ApiModelProperty("1:一卡通账号,2:学号,4:身份证号")
        @JSONField(name = "account_type")
        private String accountType;

        @ApiModelProperty("异步通知地址长度为200个字节")
        @JSONField(name = "notify_url")
        private String notifyUrl;

        @ApiModelProperty("支付成功后页面返回地址\n" +
                "长度为200个字节\n" +
                "当支付成功后聚合支付平台会在return_url后面拼接一个\n" +
                "?cp_tran_no=商户订单号的属性，\n" +
                "商户可以去后台查询一下订单的状态然后展示相应的支付结果页面")
        @JSONField(name = "return_url")
        private String returnUrl;

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
        String preUrl = url + reqUrl;
        HttpPost httpPost = new HttpPost(preUrl);
        String uuid = ObjectId.next();
        System.out.println(uuid);
        PreOrder preOrder = new PreOrder();
        preOrder.setCpTranNo(uuid);
        preOrder.setProdName("测试");
        preOrder.setTranMoney(2000);
        preOrder.setTranType("充水费");
        preOrder.setYmAppId("2011031649342027");
        preOrder.setCpCode("yiyun");
        preOrder.setAppId("2009032008460272310");
        preOrder.setNotifyUrl("https://api.baidu.com/openapi/pay/notify/wechat");
        //preOrder.setReturnUrl("https://baidu.com/pay/openapi/");

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
        ZYPayPreOrder zyPayPreOrder = JSONObject.parseObject(toString(response.getEntity()), ZYPayPreOrder.class);
        System.out.println(zyPayPreOrder);


        // ================================================================================================== //
       /* String cashier = "/pay/unified/toCashier.shtml";
        HttpGet httpPost1 = new HttpGet(url + cashier);
        Cashier cashier1 = new Cashier();
        cashier1.setYmAppId("2011031649342027");
        cashier1.setCpCode("yiyun");
        cashier1.setAppId("2009032008460272310");
        cashier1.setTranNo(zyPayPreOrder.getTranNo());
        String cashierSignParam = Md5Util.md5(SignUtil.getSignParams(JSONObject.parseObject(JSONObject.toJSONString(cashier1))));
        cashier1.setSign(RSAUtil3.encryptByPublicKey(cashierSignParam, key));
        httpPost1.setEntity(assembleParam(JSONObject.parseObject(JSONObject.toJSONString(cashier1)), ContentType.APPLICATION_FORM_URLENCODED));
        CloseableHttpResponse response1 = httpsclient.execute(httpPost1);
        System.out.println(toString(response1.getEntity()));*/


    }

    /***
     *
     * toString:httpEntity转换为字符串.
     *
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
     * @param map 参数
     * @param contentType 参数类型
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
