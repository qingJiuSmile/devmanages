package com.weds.devmanages.service.impl.config;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.entity.config.DevFaceConfig;
import com.weds.devmanages.entity.config.DevPwdConfig;
import com.weds.devmanages.entity.config.DevStdConfig;
import com.weds.devmanages.entity.config.DevViewConfig;
import com.weds.devmanages.service.DevConfiguration;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.service.impl.record.DevRecordImpl;
import com.weds.devmanages.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.weds.devmanages.service.impl.record.DevRecordImpl.SUCCESS;


/**
 * 配置设备实现类
 *
 * @author tjy
 **/
@Service
@Slf4j
public class DevConfigurationImpl implements DevConfiguration {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    @Override
    public DevStdConfig getDevStdConfig(String devIp) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/get_std";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", DevStdConfig.class);
            }
        }
        return null;
    }

    @Override
    public boolean configureTheDevice(String devIp, DevStdConfig param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_std";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }

    @Override
    public DevFaceConfig getDevFaceConfig(String devIp) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/get_face";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", DevFaceConfig.class);
            }
        }
        return null;
    }

    @Override
    public boolean configureDeviceFace(String devIp, DevFaceConfig param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_face";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }

    @Override
    public DevViewConfig getDevViewConfig(String devIp) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/get_view";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", DevViewConfig.class);
            }
        }
        return null;
    }

    @Override
    public boolean configureDeviceView(String devIp, DevViewConfig param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_view";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }

    @Override
    public DevPwdConfig getDevPwdConfig(String devIp) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/get_spwd";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0) {
                return body.getObject("data", DevPwdConfig.class);
            }
        }
        return null;
    }

    @Override
    public boolean configureDevicePwd(String devIp, DevPwdConfig param) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_spwd";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(DevRecordImpl.CODE) == 0;
        }
        return false;
    }
}
