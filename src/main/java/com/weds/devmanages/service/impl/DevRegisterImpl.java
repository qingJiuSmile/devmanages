package com.weds.devmanages.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.N8LoginEntity;
import com.weds.devmanages.entity.N8RequestEntity;
import com.weds.devmanages.entity.SignatureEntity;
import com.weds.devmanages.service.DevRegister;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.util.IpConfig;
import com.weds.devmanages.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备注册实现类
 *
 * @author tjy
 **/
@Slf4j
@Service
public class DevRegisterImpl extends BaseClass implements DevRegister {


    private static final String SECRET = "WEDS";

    public static final String DEV_APP = "N8:APP:";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DevBaseImpl devBase;

    @Override
    public JsonResult<List<Map<String, Object>>> register(String devIp, String pwd) {

        if (StringUtils.isBlank(devIp) || StringUtils.isBlank(pwd)) {
            return failMsg("参数有误");
        }

        // 多台设备进行分批处理
        String[] devList = devIp.split(",");
        List<Map<String, Object>> signLs = new ArrayList<>();
        for (String devIpLs : devList) {
            if (!IpConfig.ipCheck(devIpLs)) {
                continue;
            }

            // 登录校验；如果成功，则返回对应的密钥
            try {
                Map<String, Object> map = loginAndRegister(devIpLs, pwd);
                if (map != null && !map.isEmpty()) {
                    signLs.add(map);
                    N8RequestEntity loginEntity = new N8RequestEntity();
                    loginEntity.setIp(devIpLs);
                    loginEntity.setPassword(pwd);
                    redisUtil.set(DevBaseImpl.N8_USER_ACCOUNT + ":" + devIpLs, JSONObject.toJSONString(loginEntity));
                    redisUtil.set(DEV_APP + devIpLs + ":appId", map.get("appId").toString());
                    redisUtil.set(DEV_APP + devIpLs + ":appSecret", map.get("appSecret").toString());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        // 同步token
        devBase.initToken();
        return succMsgData(signLs);
    }

    /**
     * 登录并注册
     *
     * @param depIp 设备ip
     * @param pwd   设备密码
     * @return {@link SignatureEntity} appId、appSecret
     * @author tjy
     **/
    private Map<String, Object> loginAndRegister(String depIp, String pwd) {
        // 请求token接口
        N8LoginEntity login = devBase.login(pwd, depIp);
        if (login != null && StringUtils.isNotBlank(login.getData().getToken())) {
            // 如果登录成功，分配给对应设备appId,appSecret
            return distribution(depIp);
        }
        return null;
        //
    }


    /**
     * 根据设备ip分配appId和appSecret
     *
     * @param devIp 设备ip
     * @return {@link Map< String, Object>} key1: appSecret k2: appId k3 : devIp
     * @author tjy
     **/
    public Map<String, Object> distribution(String devIp) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("appSecret", Base64Encoder.encode(System.currentTimeMillis() + SECRET + devIp));
        map.put("appId", MD5.create().digestHex(System.currentTimeMillis() + SECRET + devIp));
        map.put("devIp", devIp);
        return map;
    }
}
