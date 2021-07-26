package com.weds.devmanages.service.impl.mode;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.entity.mode.DevNet;
import com.weds.devmanages.entity.mode.LinkMode;
import com.weds.devmanages.service.DevLinkMode;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.service.impl.record.DevRecordImpl;
import com.weds.devmanages.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.weds.devmanages.service.impl.record.DevRecordImpl.SUCCESS;

/**
 * 设备链接及配置实现类
 *
 * @author tjy
 **/
@Slf4j
@Service
public class DevLinkModeImpl implements DevLinkMode {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    @Override
    public LinkMode getLinkMode(String ip) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + ip + "/network/get_link_cfg";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", LinkMode.class);
            }
        }

        return null;
    }

    @Override
    public boolean setUpLinkMode(String devIp, LinkMode param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/network/link_cfg";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }

    @Override
    public DevNet getDevNet(String devIp) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/network/get_net_cfg";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", DevNet.class);
            }
        }
        return null;
    }

    @Override
    public boolean setUpDevNet(String devIp, DevNet param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/network/net_cfg";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }
}
