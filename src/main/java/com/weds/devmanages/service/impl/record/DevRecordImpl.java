package com.weds.devmanages.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.entity.record.*;
import com.weds.devmanages.service.DevRecord;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 档案管理实现类
 *
 * @author tjy
 **/

@Service
@Slf4j
public class DevRecordImpl extends BaseClass implements DevRecord {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    public static final String MSG = "msg";

    public static final String SUCCESS = "OK";

    public static final String CODE = "code";

    private long archivesCount = 0;

    private long ruleCount = 0;

    private long timeIntervalCount = 0;

    private long faceErrCount = 0;

    private final static String RECORD_URL = "/data/get_doc_data";


    @Override
    public DevArchives getArchives(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        param.setFieldName("xh");
        param.setTableName("wdda");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevArchives data = body.getObject("data", DevArchives.class);
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    archivesCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(archivesCount);
                return data;
            }
        } else {
            // TODO 请求失败时，这里考虑不处理token失效的情况，因为这里是针对于某个设备，在进入此接口前，一定是会请求具体的设备列表，那个时候会验证token实效性;不过还是有可能出现单页面很久没操作，刷新后token到期的情况；考虑性能问题，先不处理。
           /* // 失败后重新刷新token
            boolean newToken = restart.refreshToken(param.getDevIp());
            // 正常情况，请求token没问题说明接口是没问题的，但还是可能会出现死循的情况
            if (newToken) {
                return getArchives(param);
            }*/
        }
        return null;
    }

    @Override
    public DevRule getRule(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        param.setFieldName("gz");
        param.setTableName("mjgz");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevRule data = body.getObject("data", DevRule.class);
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    ruleCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(ruleCount);
                return data;
            }
        } // TODO 失败处理同上
        return null;
    }


    @Override
    public DevTimeInterval getTimeInterval(DevRecordParam param) {
        // 添加对对应数据库字段的查询
        param.setFieldName("sd");
        param.setTableName("mjsd");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevTimeInterval data = body.getObject("data", DevTimeInterval.class);
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    timeIntervalCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(timeIntervalCount);
                return data;
            }
        } // TODO 失败处理同上

        return null;
    }

    @Override
    public DevFaceErr getFaceErr(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        param.setFieldName("xh");
        param.setTableName("face_err");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevFaceErr data = body.getObject("data", DevFaceErr.class);
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    faceErrCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(faceErrCount);
                return data;
            }
        } // TODO 失败处理同上

        return null;
    }


}
