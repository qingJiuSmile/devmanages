package com.weds.devmanages;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.ObjectId;
import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.entity.N8RequestEntity;
import com.weds.devmanages.entity.SignatureEntity;
import com.weds.devmanages.entity.signature.SignatureParamEntity;
import com.weds.devmanages.service.DevBase;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.service.pay.PayTest;
import com.weds.devmanages.util.RedisUtil;
import com.weds.devmanages.util.RestTemplateUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
class DevmanagesApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DevBase n8ApiInterface;

    @Autowired
    private DevBaseImpl n8Implement;

    @Autowired
    private RestTemplateUtils restTemplateUtils;


    @Autowired
    private PayTest payTest;

    // 支付订单
    @Test
    void contes() throws Exception {
        /**
         * ymAppId:2011031649342027
         * appid:2009032008460272310
         * cp_code:yiyun
         * 签名公钥:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbicqzabDYMeXzTdHMaRqMAM6e
         * 2hqfzJrF1AkNyNW7G0sAlkypppqqYf68FINedcN3W4GNbjxXi83rzeqO6HOwpp5a
         * JfoxXGR2FWvyLt2au+j6/HS85VJEkGxvAP003rUMuJZgD+4iZTUUqQDq939ZzIMJ
         * GSr2/3OBgiQERt9rkQIDAQAB
         */
        payTest.testPay();

    }

    // 支付查询
    @Test
    void contesTo() throws Exception {
        payTest.testPayQuery("", "2108161045342334000");

    }

    // 取消支付
    @Test
    void contesTo2() throws Exception {
        payTest.testClear("2108160942432303000");

    }


    //  退款操作
    @Test
    void contesTh() throws Exception {
        String uuid = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss");
        System.out.println(uuid);
        payTest.testRollbackMoney(uuid, 1, "", "2108161607212379000", "");
    }


    // 退款查询
    @Test
    void contesThQ() throws Exception {
        payTest.testRollbackMoneyQuery("", "20210816111432");
    }


    @Test
    void contextLoads() {
     /*String name = "RABBITMQ:GATINGRECORD:maxId";
        //System.out.println(redisUtil.get(name));
        //System.out.println(n8ApiInterface.login("0000"));
        String userName = "admin";
        String password = "admin123";
        Map<String,Object> map = new HashMap<>();
        map.put("userlogin",userName);
        map.put("password",password);
        ResponseEntity<Map> post = restTemplateUtils.post("https://lock.keenzy.cn:88/info/login/1/gettoken", map, Map.class);
        System.out.println(post.getBody());*/
        redisUtil.del(DevBaseImpl.N8_USER_ACCOUNT);

        // http://10.15.0.23/  BP http://10.1.10.58/  N8 http://10.1.10.46/  N8 http://10.1.10.49/  N8 http://10.1.10.79/  N8
        //http://10.1.10.52/  N8T http://10.1.10.115/ N8T http://10.1.10.187/ N8T http://10.1.10.191/ N8T
        //http://10.1.10.76/  N8T http://10.1.10.91/  N8T http://10.15.0.225/ N8T http://10.17.1.127/ N8T
        //http://10.16.0.117/ BD http://10.1.10.110/ G5
        List<String> list = Arrays.asList("10.15.0.23", "10.1.10.58", "10.1.10.46", "10.1.10.49", "10.1.10.79", "10.1.10.52", "10.1.10.115",
                "10.1.10.187", "10.1.10.191", "10.1.10.76", "10.1.10.91", "10.15.0.225", "10.17.1.127", "10.16.0.117", "10.1.10.110", "10.17.1.126", "10.17.0.223");

        List<String> listSort = list.stream().sorted().collect(Collectors.toList());
        for (String s : listSort) {
            N8RequestEntity loginEntity = new N8RequestEntity();
            loginEntity.setIp(s);
            loginEntity.setPassword("0000");
            redisUtil.lSet(DevBaseImpl.N8_USER_ACCOUNT, JSONObject.toJSONString(loginEntity));
        }
    }


    @Autowired
    private SignatureParamEntity paramEntity;

    @Test
    void TestAbc(){
        System.out.println(Arrays.toString(paramEntity.getPathPatterns()));
    }

    @Test
    void TestSec() {
        // System.out.println(      n8Implement.getDeviceAccount());
        // n8Implement.login("0000","http://10.17.1.126/");
        // System.out.println(n8ApiInterface.getAppInfo("10.1.10.46"));
        final String filePath = "F:";
        final String fileName = "testFile.txt";
        final String url = "http://localhost:8080/file/upload";

        RestTemplate restTemplate = new RestTemplate();

        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //设置请求体，注意是LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource(filePath + "/" + fileName);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        form.add("filename", fileName);

        //用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);

        String s = restTemplate.postForObject(url, files, String.class);
        System.out.println(s);
    }


}
