package com.weds.devmanages.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.base.BaseClass;
import com.weds.devmanages.entity.record.*;
import com.weds.devmanages.service.DevRecord;
import com.weds.devmanages.service.impl.base.DevBaseImpl;
import com.weds.devmanages.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    private long faceIdentCount = 0;

    private final static String RECORD_URL = "/data/get_doc_data";

    private static final Map<String, Object> FACE_ERR_MAP = new HashMap<>();

    static {
        FACE_ERR_MAP.put("-2001", "图片解析失败");
        FACE_ERR_MAP.put("-2009", "档案丢失");
        FACE_ERR_MAP.put("-2010", "特征版本错误");
        FACE_ERR_MAP.put("-2011", "未指定人员");
        FACE_ERR_MAP.put("-4000", "绑定失败");
        FACE_ERR_MAP.put("-4001", "图片解析特征失败");
        FACE_ERR_MAP.put("-4002", "未检测到人脸");
        FACE_ERR_MAP.put("-4003", "多人脸(人脸数>1)");
        FACE_ERR_MAP.put("-4004", "人脸面积过小");
        FACE_ERR_MAP.put("-4005", "侧脸/人脸角度过大");
        FACE_ERR_MAP.put("-4006", "照片模糊");
        FACE_ERR_MAP.put("-4007", "戴口罩");
        FACE_ERR_MAP.put("-4008", "图片过小");
        FACE_ERR_MAP.put("-4009", "人脸过亮");
        FACE_ERR_MAP.put("-4010", "人脸过暗");
        FACE_ERR_MAP.put("-4011", "面部光线差异大");
    }

    @Override
    public DevArchives getArchives(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("xh");
        }
        param.setTableName("wdda");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevArchives data = (DevArchives) formatData(body.getObject("data", DevArchives.class));
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    archivesCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(archivesCount);
                data.setTotal(archivesCount);
                return data;
            }
        }
        return null;
    }

    @Override
    public DevRule getRule(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("gz");
        }
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
                data.setTotal(ruleCount);
                return data;
            }
        } // TODO 失败处理同上
        return null;
    }

    @Override
    public DevTimeInterval getTimeInterval(DevRecordParam param) {
        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("sd");
        }
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
                data.setTotal(timeIntervalCount);
                return data;
            }
        } // TODO 失败处理同上

        return null;
    }

    @Override
    public DevFaceErr getFaceErr(DevRecordParam param) {

        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("xh");
        }
        param.setTableName("face_err");
        param.setOrder("jtime desc");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevFaceErr data = (DevFaceErr) formatData(body.getObject("data", DevFaceErr.class));
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    faceErrCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(faceErrCount);
                data.setTotal(faceErrCount);
                return data;
            }
        }

        return null;
    }

    @Override
    public DevIdentifyEntity getFaceIdentify(DevRecordParam param) {
        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("user_id");
        }
        param.setTimeField("iden_time");
        param.setTableName("wdjl");
        param.setOrder("iden_time desc");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevIdentifyEntity data = (DevIdentifyEntity) formatData(body.getObject("data", DevIdentifyEntity.class));
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    faceIdentCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(faceIdentCount);
                data.setTotal(faceIdentCount);
                return data;
            }
        }
        return null;
    }

    @Override
    public DevOutsidersEntiy getOutsiders(DevRecordParam param) {
        // 添加对对应数据库字段的查询
        if (StringUtils.isBlank(param.getFieldName())) {
            param.setFieldName("rec_state");
        }
        param.setTimeField("iden_time");
        param.setTableName("exjl");
        param.setOrder("iden_time desc");

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + RECORD_URL;

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();

        if (body != null) {
            if (SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0) {
                DevOutsidersEntiy data = (DevOutsidersEntiy) formatData(body.getObject("data", DevOutsidersEntiy.class));
                // 只有查询第一页时，才会知道总数量，所以这里需要记录总数量
                if (param.getPageNum().equals(1)) {
                    faceIdentCount = data.getPagination().getTotal();
                }
                data.getPagination().setTotal(faceIdentCount);
                data.setTotal(faceIdentCount);
                return data;
            }
        }
        return null;
    }

    @Override
    public boolean taskClearAll(DevRecordParam param) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + "/task/clear_all";
        param.setType(0);

        JSONObject body = restTemplateUtils.post(
                url,
                DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param),
                JSONObject.class
        ).getBody();

        if (body != null) {
            return SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0;
        }
        return false;
    }


    @Override
    public boolean clearAll(DevRecordParam param) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + param.getDevIp() + "/data/clear_dev_data";

        JSONObject body = restTemplateUtils.post(
                url,
                DevBaseImpl.addHeader(param.getDevIp()),
                JSONObject.toJSONString(param),
                JSONObject.class
        ).getBody();

        if (body != null) {
            return SUCCESS.equals(body.getString(MSG)) && body.getIntValue(CODE) == 0;
        }
        return false;
    }


    /**
     * 字典字段转换
     *
     * @param data 入参对象
     * @author tjy
     **/
    private Object formatData(Object data) {

        // 识别记录字段转换
        if (data instanceof DevIdentifyEntity) {
            DevIdentifyEntity target = (DevIdentifyEntity) data;
            if (!target.getList().isEmpty()) {
                target.getList().forEach(e -> {
                    // 方向
                    if (e.getAttDir() != null) {
                        switch (e.getAttDir()) {
                            case 0:
                                e.setAttDirStr("自动");
                                break;
                            case 1:
                                e.setAttDirStr("进门");
                                break;
                            case 2:
                                e.setAttDirStr("出门");
                                break;
                            default:
                                break;
                        }
                    }
                    // 人员类型
                    if (StringUtils.isNotBlank(e.getUserType())) {
                        switch (e.getUserType()) {
                            case "0":
                                e.setUserType("授权人员");
                                break;
                            case "1":
                                e.setUserType("临时访客");
                                break;
                            case "2":
                                e.setUserType("请假人员");
                                break;
                            default:
                                break;
                        }
                    }
                    // 门状态
                    if (e.getDoorOpen() != null) {
                        switch (e.getDoorOpen()) {
                            case 0:
                                e.setDoorOpenStr("未开门");
                                break;
                            case 1:
                                e.setDoorOpenStr("已开门");
                                break;
                            default:
                                break;
                        }
                    }
                    // 门状态
                    if (StringUtils.isNotBlank(e.getAuthType())) {
                        switch (e.getAuthType()) {
                            case "c":
                                e.setAuthType("刷卡");
                                break;
                            case "d":
                                e.setAuthType("身份证");
                                break;
                            case "f":
                                e.setAuthType("指纹");
                                break;
                            case "l":
                                e.setAuthType("人脸");
                                break;
                            case "p":
                                e.setAuthType("超级密码");
                                break;
                            case "q":
                                e.setAuthType("二维码");
                                break;
                            default:
                                break;
                        }
                    }

                });
            }
            return target;
        }

        // 上传人脸错误信息
        if (data instanceof DevFaceErr) {
            DevFaceErr target = (DevFaceErr) data;
            if (!target.getList().isEmpty()) {
                target.getList().forEach(e -> {
                    if (StringUtils.isNotBlank(e.getErrCode())) {
                        Object o = FACE_ERR_MAP.get(e.getErrCode());
                        e.setErrCode(o == null ? null : o.toString());
                    }
                });
            }
            return target;
        }

        // 设备档案信息
        if (data instanceof RecordEntity) {
            RecordEntity target = (RecordEntity) data;
            if (!target.getRows().isEmpty()) {
                target.getRows().forEach(e -> {

                });
            }
        }
        // 设备档案信息
        if (data instanceof DevArchives) {
            DevArchives target = (DevArchives) data;
            if (!target.getList().isEmpty()) {
                target.getList().forEach(e -> {
                    // 是否存在照片
                    if (e.getPhotoNum() != null) {
                        switch (e.getPhotoNum()) {
                            case 0:
                                e.setPhotoStr("无");
                                break;
                            case 1:
                                e.setPhotoStr("有");
                                break;
                            default:
                                break;
                        }
                    }
                    // 是否存在人脸
                    if (e.getFaceNum() != null) {
                        switch (e.getFaceNum()) {
                            case 0:
                                e.setFaceStr("无");
                                break;
                            case 2:
                                e.setFaceStr("有");
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            return target;
        }

        // 外来人员记录
        if (data instanceof DevOutsidersEntiy) {
            DevOutsidersEntiy target = (DevOutsidersEntiy) data;
            if (!target.getList().isEmpty()) {
                target.getList().forEach(e -> {
                    // 是否存在照片
                    if (e.getAttDir() != null) {
                        // 方向
                        if (e.getAttDir() != null) {
                            switch (e.getAttDir()) {
                                case 0:
                                    e.setAttDirStr("自动");
                                    break;
                                case 1:
                                    e.setAttDirStr("进门");
                                    break;
                                case 2:
                                    e.setAttDirStr("出门");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
            return target;
        }

        return null;
    }
}
