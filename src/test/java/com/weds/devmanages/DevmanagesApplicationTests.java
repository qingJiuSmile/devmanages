package com.weds.devmanages;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.weds.devmanages.entity.LoginReturn;
import com.weds.devmanages.entity.N8RequestEntity;
import com.weds.devmanages.entity.signature.SignatureParamEntity;
import com.weds.devmanages.service.DevBase;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.util.RedisUtil;
import com.weds.devmanages.util.RestTemplateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    void TestAbc() {
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


    @Test
    public void test() {
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        redisUtil.lSet("test", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), 3000);
        System.out.println(redisUtil.lGet("test", 0, -1));
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public  void test1(){
        System.out.println();
        for (String scanKey : redisUtil.getScanKeys("k*", -1)) {
            redisUtil.del(scanKey);
        }
        //System.out.println(redisTemplate.opsForValue().getOperations().getExpire("redisKey"));

       /* Map<String,Object >map = new HashMap<>();
        map.put("name","iot");
        map.put("passwd","123456");
        LoginReturn post = restTemplateUtils.post("http://zyhr.cn:50059/v1.1/login", map, LoginReturn.class).getBody();
        System.out.println(post);*/

    }



}
