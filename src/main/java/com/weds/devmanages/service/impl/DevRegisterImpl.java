package com.weds.devmanages.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.entity.N8LoginEntity;
import com.weds.devmanages.entity.N8RequestEntity;
import com.weds.devmanages.service.DevBase;
import com.weds.devmanages.service.DevRegister;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.service.impl.record.DevRecordImpl;
import com.weds.devmanages.util.IpConfig;
import com.weds.devmanages.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 设备注册实现类
 *
 * @author tjy
 **/
@Slf4j
@Service
public class DevRegisterImpl extends BaseClass implements DevRegister {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DevBaseImpl devBase;


    @Override
    public JsonResult<String> register(String devIp, String pwd) {

        if (StringUtils.isBlank(devIp) || StringUtils.isBlank(pwd)) {
            return failMsg("参数有误");
        }

        // 多台设备进行分批处理
        String[] devList = devIp.split(",");
        StringBuilder msg = new StringBuilder();
        for (String devIpLs : devList) {
            if (!IpConfig.ipCheck(devIpLs)) {
                msg.append("设备[").append(devIpLs).append("]").append("设备ip有误;");
                continue;
            }

            N8RequestEntity loginEntity = new N8RequestEntity();
            loginEntity.setIp(devIpLs);
            loginEntity.setPassword(pwd);

            boolean ret = redisUtil.set(DevBaseImpl.N8_USER_ACCOUNT + ":" + devIpLs, JSONObject.toJSONString(loginEntity));
            if (!ret) {
                msg.append("设备[").append(devIpLs).append("]").append("注册失败;");
            }
        }
        // 同步token
        devBase.initToken();
        return succMsgData(msg.toString());
    }
}
