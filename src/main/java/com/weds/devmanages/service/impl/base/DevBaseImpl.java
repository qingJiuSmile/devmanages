package com.weds.devmanages.service.impl.base;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.config.thread.DevThreadPoolConfig;
import com.weds.devmanages.config.thread.MethodThreadPoolConfig;
import com.weds.devmanages.entity.*;
import com.weds.devmanages.entity.record.RecordEntity;
import com.weds.devmanages.mapper.datasource1.DeviceBaseManage2PGMapper;
import com.weds.devmanages.mapper.datasource2.DeviceBaseMange2MSMapper;
import com.weds.devmanages.service.DevBase;
import com.weds.devmanages.service.DevRestart;
import com.weds.devmanages.service.impl.record.DevRecordImpl;
import com.weds.devmanages.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.weds.devmanages.service.impl.record.DevRecordImpl.CODE;
import static com.weds.devmanages.service.impl.record.DevRecordImpl.SUCCESS;


/**
 * N8 设备接口实现
 *
 * @author tjy
 **/

@Slf4j
@Service
public class DevBaseImpl implements DevBase, DevRestart {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TaskQueueDaemonThread taskQueueDaemonThread;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DevThreadPoolConfig n8ThreadPool;

    @Autowired
    private MethodThreadPoolConfig methodThreadPollConfig;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 人员档案
     */
    public static final String N8_USER_ACCOUNT = "N8:USER:ACCOUNT";

    /**
     * 设备硬件信息前缀
     */
    private static final String SYS_INFO = "N8:SYSTEM:DATA:";

    /**
     * 设备应用信息前缀
     */
    private static final String SYS_APP = "N8:SYSTEM:APP:";

    /**
     * 设备运行信息前缀
     */
    private static final String SYS_RUN = "N8:SYSTEM:RUN:";

    /**
     * 设备磁盘信息前缀
     */
    private static final String SYS_DISK = "N8:SYSTEM:DISK:";

    /**
     * 连通性前缀
     **/
    private static final String CONNECTION = "N8:CONNECTION:";

    /**
     * 连通成功后记录前缀
     **/
    private static final String CONNECTION_ERR_SUCCESS = CONNECTION + "SUCCESS:";

    /**
     * 连通失败后记录前缀
     **/
    private static final String CONNECTION_ERR_MESSAGE = CONNECTION + "ERR:";

    private static final int OPERATION_ID = 1073741824;

    private static long DELAYED_TIME = 0;

    /**
     * 请求前缀
     */
    public static String REQUEST_PREFIX = "http://";

    /**
     * 请求后缀
     */
    public static String REQUEST_SUFFIX = "/user/login";


    /**
     * 存储token容器 格式 ：K（设备IP） - V （token值）
     */
    public static Map<String, Object> tokenMap = new ConcurrentHashMap<>();

    /**
     * 存储硬件信息回调数据 格式: K（设备IP） - V （硬件信息）
     */
    private Map<String, SysInfoEntity.SysData> devSysInfoMap = new ConcurrentHashMap<>();


    /**
     * 存应用信息回调数据 格式: K（设备IP） - V （应用信息）
     */
    private Map<String, AppInfoEntity.AppInfoData> devAppInfoMap = new ConcurrentHashMap<>();

    /**
     * 存应用信息回调数据 格式: K（设备IP） - V （应用信息）
     */
    private Map<String, RunInfoEntity.RunInfoData> devRunInfoMap = new ConcurrentHashMap<>();


    /**
     * 存设备磁盘信息 格式: K（设备IP） - V （磁盘信息）
     */
    private Map<String, DiskInfoEntity.DiskInfoData> devDiskInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private DeviceBaseManage2PGMapper deviceBaseManage2PGMapper;

    @Autowired
    private DeviceBaseMange2MSMapper deviceBaseMange2MSMapper;


    /**
     * 修复设备(全量下发档案) 包含PG、MS库
     *
     * @param devId 设备id
     * @return {@link Integer}
     **/
    public Integer repairEquipmentAll(String devId) {
        Integer ret1 = repairEquipmentForPg(devId);
        Integer ret2 = repairEquipmentForMs(devId);
        ret1 = ret1 == null || ret1.equals(0) ? 0 : ret1;
        ret2 = ret2 == null || ret2.equals(0) ? 0 : ret2;
        return ret1 + ret2;
    }

    /**
     * 修复设备(全量下发档案) 包含PG、MS库
     *
     * @param devId 设备id
     * @return {@link Integer}
     **/
    public Integer repairEquipment2RuleAll(String devId) {
        Integer ret1 = repairEquipment2RuleForPg(devId);
        Integer ret2 = repairEquipment2RuleForMs(devId);
        ret1 = ret1 == null || ret1.equals(0) ? 0 : ret1;
        ret2 = ret2 == null || ret2.equals(0) ? 0 : ret2;
        return ret1 + ret2;
    }

    /**
     * 修复设备PG(全量规则)
     *
     * @param devId 设备id
     * @return {@link Integer} 成功条数
     **/
    private Integer repairEquipment2RuleForPg(String devId) {
        try {
            return deviceBaseManage2PGMapper.insertFullOperationFileRule2PG(devId, OPERATION_ID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 修复设备MS(全量规则)
     *
     * @param devId 设备id
     * @return {@link Integer} 成功条数
     **/
    private Integer repairEquipment2RuleForMs(String devId) {
        try {
            return deviceBaseMange2MSMapper.insertFullOperationFileRule2MS(devId, OPERATION_ID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 修复设备PG(全量下发档案)
     *
     * @param devId 设备id
     * @return {@link Integer} 成功条数
     **/
    private Integer repairEquipmentForPg(String devId) {
        try {
            return deviceBaseManage2PGMapper.insertFullOperationFile2PG(devId, OPERATION_ID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 修复设备MS(全量下发档案)
     *
     * @param devId 设备id
     * @return {@link Integer} 成功条数
     **/
    private Integer repairEquipmentForMs(String devId) {
        try {
            return deviceBaseMange2MSMapper.insertFullOperationFile2MS(devId, OPERATION_ID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取当前所有设备状态
     *
     * @return {@link DevStateEntity}
     **/
    public List<DevStateEntity> getState() {
        // 获取设备连接状态所有key
        Set<String> scanKeys = redisUtil.getScanKeys(CONNECTION_ERR_SUCCESS + "*", -1);
        List<DevStateEntity> stateLs = new ArrayList<>();
        // 批量通过key获取value
        List<Object> list = redisTemplate.opsForValue().multiGet(scanKeys);
        if (list != null && !list.isEmpty()) {
            list.forEach(item -> {
                DevStateEntity obj = new DevStateEntity();

                // 获取设备状态记录时间
                String successTime = JSONObject.parseObject(item.toString()).getString("time");
                // 获取设备状态标识 1在线 2离线
                int state = JSONObject.parseObject(item.toString()).getIntValue("state");
                // 获取当前设备ip
                String ip = JSONObject.parseObject(item.toString()).getString("ip");
                obj.setDevIp(ip).setDevState(state).setSuccessTime(successTime);

                // 获取缓存中连接错误信息
                Object o = redisTemplate.opsForValue().get(CONNECTION_ERR_MESSAGE + ip);
                // 通过当前设备ip，再去取该ip的连接失败记录，如果有就记录
                if (o != null) {
                    obj.setErrorTime(JSONObject.parseObject(o.toString()).getString("time"))
                            .setErrorMsg(JSONObject.parseObject(o.toString()).getString("msg"));
                }
                stateLs.add(obj);
            });
        }
        return stateLs;
    }

    /**
     * 获取设备硬件信息
     *
     * @param search 查询条件
     * @return {@link SysInfoEntity}
     * @author tjy
     **/
    public SysInfoEntity getDevInfoMap(PublicParam search) {
        SysInfoEntity sysInfoEntity = new SysInfoEntity();
        List<SysInfoEntity.SysData> list = new ArrayList<>();

        // TODO 2021-9-22 这里转为从redis中获取
        // devSysInfoMap.forEach((k, v) -> list.add(v));
        Set<String> scanKeys = redisUtil.getScanKeys(SYS_INFO + "*", -1);
        List<Object> devLs = redisTemplate.opsForValue().multiGet(scanKeys);

        // 数据不为空则添加
        if (devLs != null && !devLs.isEmpty()) {
            devLs.forEach(item -> {
                SysInfoEntity.SysData sysData = JSONObject.parseObject(item.toString(), SysInfoEntity.SysData.class);
                if (sysData != null && StringUtils.isNotBlank(sysData.getDevIp())) {
                    list.add(sysData);
                }
            });
        }

        //////////////////////   筛选、排序等   //////////////////////
        // 排序
        if (StringUtils.isNotBlank(search.getOrder()) && StringUtils.isNotBlank(search.getSort())) {
            if ("devModel".equals(search.getSort())) {
                if ("desc".equals(search.getOrder())) {
                    list.sort(Comparator.comparing(SysInfoEntity.SysData::getDevModel).reversed());
                } else {
                    list.sort(Comparator.comparing(SysInfoEntity.SysData::getDevModel));
                }
            } else {
                if ("desc".equals(search.getOrder())) {
                    list.sort(Comparator.comparing(SysInfoEntity.SysData::getDevIp).reversed());
                } else {
                    list.sort(Comparator.comparing(SysInfoEntity.SysData::getDevIp));
                }
            }
        }
        // 分页
        @SuppressWarnings("unchecked")
        List<SysInfoEntity.SysData> listTo = (List<SysInfoEntity.SysData>) PageUtil.startPage(list, search.getPage(), search.getRows());
        sysInfoEntity.setRows(listTo);
        sysInfoEntity.setTotal(list.size());
        return sysInfoEntity;
    }

    /**
     * 获取应用信息
     *
     * @param search 查询条件
     * @return {@link SysInfoEntity}
     * @author tjy
     **/
    public AppInfoEntity getDevAppInfoMap(PublicParam search) {
        AppInfoEntity appInfoEntity = new AppInfoEntity();
        List<AppInfoEntity.AppInfoData> list = new ArrayList<>();
        // devAppInfoMap.forEach((k, v) -> list.add(v));

        Set<String> scanKeys = redisUtil.getScanKeys(SYS_APP + "*", -1);
        List<Object> devLs = redisTemplate.opsForValue().multiGet(scanKeys);

        // 数据不为空则添加
        if (devLs != null && !devLs.isEmpty()) {
            devLs.forEach(item -> {
                AppInfoEntity.AppInfoData sysData = JSONObject.parseObject(item.toString(), AppInfoEntity.AppInfoData.class);
                if (sysData != null && StringUtils.isNotBlank(sysData.getDevIp())) {
                    list.add(sysData);
                }
            });
        }

        //////////////////////   筛选、排序等   //////////////////////
        // 排序
        if (StringUtils.isNotBlank(search.getOrder()) && StringUtils.isNotBlank(search.getSort())) {
            if ("desc".equals(search.getOrder())) {
                list.sort(Comparator.comparing(AppInfoEntity.AppInfoData::getDevIp).reversed());
            } else {
                list.sort(Comparator.comparing(AppInfoEntity.AppInfoData::getDevIp));
            }
        }
        @SuppressWarnings("unchecked")
        List<AppInfoEntity.AppInfoData> listTo = (List<AppInfoEntity.AppInfoData>) PageUtil.startPage(list, search.getPage(), search.getRows());
        appInfoEntity.setRows(listTo);
        appInfoEntity.setTotal(list.size());
        return appInfoEntity;
    }

    /**
     * 获取应用信息
     *
     * @param search 查询条件
     * @return {@link SysInfoEntity}
     * @author tjy
     **/
    public RunInfoEntity getDevRunInfoMap(PublicParam search) {

        RunInfoEntity appInfoEntity = new RunInfoEntity();
        List<RunInfoEntity.RunInfoData> list = new ArrayList<>();
        //  devRunInfoMap.forEach((k, v) -> list.add(v));
        Set<String> scanKeys = redisUtil.getScanKeys(SYS_RUN + "*", -1);
        List<Object> devLs = redisTemplate.opsForValue().multiGet(scanKeys);

        // 数据不为空则添加
        if (devLs != null && !devLs.isEmpty()) {
            devLs.forEach(item -> {
                RunInfoEntity.RunInfoData sysData = JSONObject.parseObject(item.toString(), RunInfoEntity.RunInfoData.class);
                if (sysData != null && StringUtils.isNotBlank(sysData.getDevIp())) {
                    list.add(sysData);
                }
            });
        }
        //////////////////////   筛选、排序等   //////////////////////
        // 排序
        if (StringUtils.isNotBlank(search.getOrder()) && StringUtils.isNotBlank(search.getSort())) {
            if ("desc".equals(search.getOrder())) {
                list.sort(Comparator.comparing(RunInfoEntity.RunInfoData::getDevIp).reversed());
            } else {
                list.sort(Comparator.comparing(RunInfoEntity.RunInfoData::getDevIp));
            }
        }
        @SuppressWarnings("unchecked")
        List<RunInfoEntity.RunInfoData> listTo = (List<RunInfoEntity.RunInfoData>) PageUtil.startPage(list, search.getPage(), search.getRows());
        appInfoEntity.setRows(listTo);
        appInfoEntity.setTotal(list.size());
        return appInfoEntity;
    }

    /**
     * 获取应用信息
     *
     * @param search 查询条件
     * @return {@link SysInfoEntity}
     * @author tjy
     **/
    public DiskInfoEntity getDevDiskInfoMap(PublicParam search) {
        DiskInfoEntity diskInfoEntity = new DiskInfoEntity();
        List<DiskInfoEntity.DiskInfoData> list = new ArrayList<>();
        // devDiskInfoMap.forEach((k, v) -> list.add(v));

        Set<String> scanKeys = redisUtil.getScanKeys(SYS_DISK + "*", -1);
        List<Object> devLs = redisTemplate.opsForValue().multiGet(scanKeys);

        // 数据不为空则添加
        if (devLs != null && !devLs.isEmpty()) {
            devLs.forEach(item -> {
                DiskInfoEntity.DiskInfoData sysData = JSONObject.parseObject(item.toString(), DiskInfoEntity.DiskInfoData.class);
                if (sysData != null && StringUtils.isNotBlank(sysData.getDevIp())) {
                    list.add(sysData);
                }
            });
        }

        //////////////////////   筛选、排序等   //////////////////////
        // 排序
        if (StringUtils.isNotBlank(search.getOrder()) && StringUtils.isNotBlank(search.getSort())) {
            if ("desc".equals(search.getOrder())) {
                list.sort(Comparator.comparing(DiskInfoEntity.DiskInfoData::getDevIp).reversed());
            } else {
                list.sort(Comparator.comparing(DiskInfoEntity.DiskInfoData::getDevIp));
            }
        }
        @SuppressWarnings("unchecked")
        List<DiskInfoEntity.DiskInfoData> listTo = (List<DiskInfoEntity.DiskInfoData>) PageUtil.startPage(list, search.getPage(), search.getRows());
        diskInfoEntity.setRows(listTo);
        diskInfoEntity.setTotal(list.size());
        return diskInfoEntity;
    }

    /***********************************************************************************************
     ***                                 设 备 实 现 方 法                                        ***
     ***********************************************************************************************
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    @Override
    public boolean reStart(String ip) {
        // 拼接接口地址
        String url = REQUEST_PREFIX + ip + "/config/reboot";
        /*// 登录设备
        boolean login = refreshToken(ip);
        log.info("登录 ==>[{}]", login);*/
        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        log.info("设备 [{}] 重启 [{}]", ip, body);
        if (body == null) {
            return false;
        }
        return body.getIntValue("code") == 0 && "OK".equals(body.getString("msg"));
    }

    /**
     * 登录设备
     *
     * @param password 密码
     * @param ip       设备ip
     * @return {@link N8LoginEntity}
     * @author tjy
     **/
    @Override
    public N8LoginEntity login(String password, String ip) {
        // 拼接接口地址
        String url = REQUEST_PREFIX + ip + REQUEST_SUFFIX;
        Map<String, Object> param = new HashMap<>(2);
        param.put("password", password);
        return restTemplateUtils.post(url, param, N8LoginEntity.class).getBody();
    }

    /**
     * ==========================================   单个设备请求开始 ==========================================
     */

    @Override
    public SysInfoEntity.SysData getSysInfo(String ip) {

        String url = REQUEST_PREFIX + ip + "/data/get_sys_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            // 判断响应情况
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                SysInfoEntity.SysData data = body.getObject("data", SysInfoEntity.SysData.class);
                data.setDevIp(ip);
                return data;
            }
        }
        return null;
    }

    @Override
    public AppInfoEntity.AppInfoData getAppInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_app_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            // 判断响应情况
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                AppInfoEntity.AppInfoData data = body.getObject("data", AppInfoEntity.AppInfoData.class);
                data.setDevIp(ip);
                return data;
            }
        }
        return null;
    }

    @Override
    public RunInfoEntity.RunInfoData getRunInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_run_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            // 判断响应情况
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                RunInfoEntity.RunInfoData data = body.getObject("data", RunInfoEntity.RunInfoData.class);
                data.setDevIp(ip);
                return data;
            }
        }
        return null;
    }

    @Override
    public DiskInfoEntity.DiskInfoData getDiskInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_disk_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            // 判断响应情况
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                DiskInfoEntity.DiskInfoData data = body.getObject("data", DiskInfoEntity.DiskInfoData.class);
                data.setDevIp(ip);
                return data;
            }
        }
        return null;
    }

    /**
     * ==========================================  单个设备请求结束 ==========================================
     */


    /**
     * ==========================================   Async Method Begin  ==========================================
     */

    @Override
    public void asyncGetAppInfo() {
        tokenMap.forEach((k, v) -> n8ThreadPool.taskExecutor().execute(() -> {
            // log.info("当前ip的token为 ==> [{}]", tokenMap.get(k).toString());
            String url = REQUEST_PREFIX + k + "/data/get_app_info";

            // 请求对应ip的接口
            try {
                // 新增前，先删除原有数据
                devAppInfoMap.remove(k);
                JSONObject body = restTemplateUtils.post(url, addHeader(k), null, JSONObject.class, new HashMap<>()).getBody();
                // 如果请求失败再次请求 加入队列 TODO
                if (body == null) {
                    addQueue(restartAppInfo(k));
                } else {
                    // 判断响应情况
                    if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                        AppInfoEntity.AppInfoData data = body.getObject("data", AppInfoEntity.AppInfoData.class);
                        data.setDevIp(k);
                        devAppInfoMap.put(k, data);
                    } else {
                        // 请求错误加入队列重新请求 TODO
                        addQueue(restartAppInfo(k));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                // 失败时，也加入队列，再次请求 TODO
                addQueue(restartAppInfo(k));
            }
        }));
    }

    @Override
    public void asyncGetRunInfo() {
        // 轮询设备ip，将每一个设备放入不同线程中
        tokenMap.forEach((k, v) -> n8ThreadPool.taskExecutor().execute(() -> {
            // log.info("当前ip的token为 ==> [{}]", tokenMap.get(k).toString());
            String url = REQUEST_PREFIX + k + "/data/get_run_info";

            // 请求对应ip的接口
            try {
                // 新增前，先删除原有数据
                devRunInfoMap.remove(k);
                JSONObject body = restTemplateUtils.post(url, addHeader(k), null, JSONObject.class, new HashMap<>()).getBody();
                // 如果请求失败再次请求 加入队列 TODO
                if (body == null) {
                    addQueue(restartRunInfo(k));
                } else {

                    // 判断响应情况
                    if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                        RunInfoEntity.RunInfoData data = body.getObject("data", RunInfoEntity.RunInfoData.class);
                        data.setDevIp(k);
                        devRunInfoMap.put(k, data);
                    } else {
                        // 请求错误加入队列重新请求 TODO
                        addQueue(restartRunInfo(k));
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                // 失败时，也加入队列，再次请求 TODO
                addQueue(restartRunInfo(k));
            }
        }));
    }

    @Override
    public void asyncGetSysInfo() {
        // 轮询设备ip，将每一个设备放入不同线程中
        tokenMap.forEach((k, v) -> n8ThreadPool.taskExecutor().execute(() -> {
            // log.info("当前ip的token为 ==> [{}]", tokenMap.get(k).toString());
            String url = REQUEST_PREFIX + k + "/data/get_sys_info";

            // 请求对应ip的接口
            try {
                // 新增前，先删除原有数据
                devSysInfoMap.remove(k);
                JSONObject body = restTemplateUtils.post(url, addHeader(k), null, JSONObject.class, new HashMap<>()).getBody();
                // 如果请求失败再次请求 加入队列 TODO
                if (body == null) {
                    addQueue(restartSysInfo(k));
                } else {
                    // 判断响应情况
                    if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                        SysInfoEntity.SysData data = body.getObject("data", SysInfoEntity.SysData.class);
                        data.setDevIp(k);
                        devSysInfoMap.put(k, data);
                    } else {
                        // 请求错误加入队列重新请求 TODO
                        addQueue(restartSysInfo(k));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                // 失败时，也加入队列，再次请求 TODO
                addQueue(restartSysInfo(k));
            }
        }));
    }

    @Override
    public void asyncGetDiskInfo() {
        // 轮询设备ip，将每一个设备放入不同线程中
        tokenMap.forEach((k, v) -> n8ThreadPool.taskExecutor().execute(() -> {
            // log.info("当前ip的token为 ==> [{}]", tokenMap.get(k).toString());
            String url = REQUEST_PREFIX + k + "/data/get_disk_info";

            // 请求对应ip的接口
            try {
                // 新增前，先删除原有数据
                devDiskInfoMap.remove(k);
                JSONObject body = restTemplateUtils.post(url, addHeader(k), null, JSONObject.class, new HashMap<>()).getBody();
                if (body == null) {
                    addQueue(restartDiskInfo(k));
                } else {
                    // 判断响应情况
                    if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                        DiskInfoEntity.DiskInfoData data = body.getObject("data", DiskInfoEntity.DiskInfoData.class);
                        data.setDevIp(k);
                        devDiskInfoMap.put(k, data);
                    } else {
                        addQueue(restartDiskInfo(k));
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                addQueue(restartDiskInfo(k));
            }
        }));
    }

    /**
     * ==========================================   Async Method End  ==========================================
     */


    @Override
    public boolean deviceUpdate(String ip, MultipartFile file) {

        String url = REQUEST_PREFIX + ip + "/config/update_app";

        // 通过ip获取设备密码
        List deviceAccount = getDeviceAccount();
        if (deviceAccount == null) {
            return false;
        }

        String pwd = "";
        for (Object o : deviceAccount) {
            N8RequestEntity entity = JSONObject.parseObject(o.toString(), N8RequestEntity.class);
            if (entity.getIp().equals(ip)) {
                pwd = entity.getPassword();
                break;
            }
        }
        if (StringUtils.isBlank(pwd)) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Cookie", "jtoken=" + tokenMap.get(ip).toString());
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        try {
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.add("password", pwd);
            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            String result = restTemplate.postForObject(url, requestEntity, String.class);
            if (StringUtils.isNotBlank(result)) {
                JSONObject body = JSONObject.parseObject(result);
                if (body != null) {
                    if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public RecordEntity getDevRecord(String ip, int pageNum, int pageSize) {
        String url = REQUEST_PREFIX + ip + "/data/get_doc_data";

        Map<String, Object> param = new HashMap<>(10);
        param.put("fieldName", "pid");
        param.put("order", "rec_time desc");
        param.put("pageNum", pageNum);
        param.put("pageSize", pageSize);
        param.put("queryKey", "");
        param.put("rowCount", 0);
        param.put("tableName", "ut_record");

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), param, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            RecordEntity recordEntity = JSONObject.parseObject(body.toJSONString(), RecordEntity.class);
            if (recordEntity.getCode().equals(0) && "OK".equals(recordEntity.getMsg())) {

                return recordEntity;
            }
        }
        return null;
    }

    @Override
    public StartUpImg getStartUpImg(String devIp) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/get_startup";
        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                return body.getObject("data", StartUpImg.class);
            }
        }
        return null;
    }

    @Override
    public boolean setUpStartUpImg(String devIp, String base64Img) {

        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_startup";
        StartUpImg param = new StartUpImg();
        param.setStartUpPic(base64Img);

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(param), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0;
        }
        return false;
    }

    @Override
    public boolean timeCalibration(String devIp) {
        // 拼接接口地址
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/config/set_time";
        Map<String, Object> map = new HashMap<>();
        map.put("timeStr", LocalDateTime.now());

        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(devIp), JSONObject.toJSONString(map), JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0;
        }
        return false;
    }

    @Override
    public String downloadLog(String devIp) {
        // 下载文件因为是以流的形式，无法直接使用http请求去访问
        // 所以需要将内容在浏览器中直接打开
        String url = DevBaseImpl.REQUEST_PREFIX + devIp + "/data/down_log";
        try {
            return url + "?access_token=" + tokenMap.get(devIp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 查询设备记录
     *
     * @param search 搜索分页条件
     * @return {@link RecordEntity}
     * @author tjy
     * @date 2021/7/12
     **/
    public RecordEntity.RecordData searchRecord(String ip, PublicParam search) {
        return getDevRecord(ip, search.getPage(), search.getRows()).getData();
    }


    class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }


    /**
     * ==========================================   Restart Method Begin  ==========================================
     */

    /**
     * sysInfo重试机制
     *
     * @param ip 设备ip
     * @return {@link Runnable}
     * @author tjy
     **/
    private Runnable restartSysInfo(String ip) {
        return () -> {
            // 重新刷新该设备token
            boolean restart = refreshToken(ip);
            log.info("重新设备IP[{}] 结果 ==> [{}]", ip, restart);

            SysInfoEntity.SysData sysInfo = getSysInfo(ip);
            if (sysInfo == null) {
                return;
            }
            devSysInfoMap.put(ip, sysInfo);
        };
    }

    /**
     * appInfo重试机制
     *
     * @param ip 设备ip
     * @return {@link Runnable}
     * @author tjy
     **/
    private Runnable restartAppInfo(String ip) {
        return () -> {
            // 重新刷新该设备token
            boolean restart = refreshToken(ip);
            log.info("重新设备IP[{}] 结果 ==> [{}]", ip, restart);

            AppInfoEntity.AppInfoData appInfo = getAppInfo(ip);
            if (appInfo == null) {
                return;
            }
            devAppInfoMap.put(ip, appInfo);
        };
    }

    /**
     * runInfo重试机制
     *
     * @param ip 设备ip
     * @return {@link Runnable}
     * @author tjy
     **/
    private Runnable restartRunInfo(String ip) {
        return () -> {
            // 重新刷新该设备token
            boolean restart = refreshToken(ip);
            log.info("重新设备IP[{}] 结果 ==> [{}]", ip, restart);

            RunInfoEntity.RunInfoData runInfo = getRunInfo(ip);
            if (runInfo == null) {
                return;
            }
            devRunInfoMap.put(ip, runInfo);
        };
    }

    /**
     * runInfo重试机制
     *
     * @param ip 设备ip
     * @return {@link Runnable}
     * @author tjy
     **/
    private Runnable restartDiskInfo(String ip) {
        return () -> {
            // 重新刷新该设备token
            boolean restart = refreshToken(ip);
            log.info("重新设备IP[{}] 结果 ==> [{}]", ip, restart);

            DiskInfoEntity.DiskInfoData diskInfo = getDiskInfo(ip);
            if (diskInfo == null) {
                return;
            }
            devDiskInfoMap.put(ip, diskInfo);
        };
    }


    /**
     * ==========================================   Restart Method Begin  ==========================================
     */


    // ==================================================  工具方法   ==================================================

    /**
     * 添加请求头
     *
     * @param ip 设备ip
     * @return {@link Map< String, String>}
     * @author tjy
     **/
    public static Map<String, String> addHeader(String ip) {
        Map<String, String> headerMap = new HashMap<>(5);
        if (tokenMap.get(ip) != null) {
            headerMap.put("Cookie", "jtoken=" + tokenMap.get(ip).toString());
        }
        headerMap.put("content-type", "application/json;charset=UTF-8");
        return headerMap;
    }


    /**
     * 获取设备账户列表
     *
     * @return {@link List}
     * @author tjy
     **/
    public List getDeviceAccount() {
        Set<String> scanKeys = redisUtil.getScanKeys(N8_USER_ACCOUNT + ":*", -1);
        List<Object> list = new ArrayList<>();
        for (String scanKey : scanKeys) {
            list.add(redisUtil.get(scanKey));
        }
        return list;
    }

    /**
     * 刷新某台设备的token，适用与请求失败后，进行刷新
     *
     * @param ip 设备ip
     * @return {@link boolean}
     * @author tjy
     **/
    @Override
    public boolean refreshToken(String ip) {
        List deviceAccount = getDeviceAccount();
        // 获取列表失败
        if (deviceAccount.isEmpty()) {
            return false;
        }

        AtomicReference<String> pwd = new AtomicReference<>();
        // 循环遍历根据ip获取设备的密码
        for (Object o : deviceAccount) {
            N8RequestEntity entity = JSONObject.parseObject(o.toString(), N8RequestEntity.class);
            if (entity != null && StringUtils.isNotBlank(entity.getIp())) {
                if (entity.getIp().equals(ip)) {
                    pwd.set(entity.getPassword());
                }
            }
        }

        // 没有匹配IP的设备
        if (StringUtils.isBlank(pwd.get())) {
            return false;
        }

        // 请求接口获取token
        N8LoginEntity login = login(pwd.get(), ip);

        // 获取token接口失败
        if (login == null || StringUtils.isBlank(login.getData().getToken())) {
            return false;
        }

        // 将token缓存入容器中
        tokenMap.put(ip, login.getData().getToken());
        log.info("" + tokenMap);
        return true;
    }


    /**
     * 添加延时队列
     *
     * @param runnable 处理线程
     * @author tjy
     **/
    public void addQueue(Runnable runnable) {
        // 将失败的请求放如阻塞队列，刷新token再次请求，如果成功则加入map中。
        taskQueueDaemonThread.put(DELAYED_TIME, runnable);
    }


    /**
     * 初始化请求设备硬件方法
     **/
    public void initSendDevRequest() {
        tokenMap.forEach(
                (k, v) -> methodThreadPollConfig.instance().execute(() -> {
                    String url = DevBaseImpl.REQUEST_PREFIX + k + "/data/get_sys_info";
                    try {
                        // 请求对应ip的接口
                        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(k),
                                null,
                                JSONObject.class,
                                new HashMap<>(16)
                        ).getBody();
                        // 判断响应情况
                        if (body != null && SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                            SysInfoEntity.SysData data = body.getObject("data", SysInfoEntity.SysData.class);
                            data.setDevIp(k);
                            // 成功请求的设备进行项目启动时存入缓存
                            //log.info("" + data);
                            redisUtil.set(SYS_INFO + k, JSONObject.toJSONString(data));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }));
    }

    /**
     * 初始化设备应用信息
     **/
    public void initDeviceAppInfo() {
        tokenMap.forEach(
                (k, v) -> methodThreadPollConfig.instance().execute(() -> {
                    String url = DevBaseImpl.REQUEST_PREFIX + k + "/data/get_app_info";
                    try {
                        // 请求对应ip的接口
                        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(k),
                                null,
                                JSONObject.class,
                                new HashMap<>(16)
                        ).getBody();

                        if (body != null) {
                            // 判断响应情况
                            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                                AppInfoEntity.AppInfoData data = body.getObject("data", AppInfoEntity.AppInfoData.class);
                                data.setDevIp(k);
                                redisTemplate.opsForValue().set(SYS_APP + k, JSONObject.toJSONString(data));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }));
    }

    /**
     * 初始化设备运行信息
     */
    public void initDeviceRunInfo() {
        tokenMap.forEach(
                (k, v) -> methodThreadPollConfig.instance().execute(() -> {
                    String url = DevBaseImpl.REQUEST_PREFIX + k + "/data/get_run_info";
                    try {
                        // 请求对应ip的接口
                        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(k),
                                null,
                                JSONObject.class,
                                new HashMap<>(16)
                        ).getBody();

                        if (body != null) {
                            // 判断响应情况
                            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                                RunInfoEntity.RunInfoData data = body.getObject("data", RunInfoEntity.RunInfoData.class);
                                data.setDevIp(k);
                                redisTemplate.opsForValue().set(SYS_RUN + k, JSONObject.toJSONString(data));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }));
    }

    /**
     * 初始化设备磁盘信息
     */
    public void initDeviceDiskInfo() {
        tokenMap.forEach(
                (k, v) -> methodThreadPollConfig.instance().execute(() -> {
                    String url = DevBaseImpl.REQUEST_PREFIX + k + "/data/get_disk_info";
                    try {
                        // 请求对应ip的接口
                        JSONObject body = restTemplateUtils.post(url, DevBaseImpl.addHeader(k),
                                null,
                                JSONObject.class,
                                new HashMap<>(16)
                        ).getBody();

                        if (body != null) {
                            // 判断响应情况
                            if (SUCCESS.equals(body.getString(DevRecordImpl.MSG)) && body.getIntValue(CODE) == 0) {
                                DiskInfoEntity.DiskInfoData data = body.getObject("data", DiskInfoEntity.DiskInfoData.class);
                                data.setDevIp(k);
                                redisTemplate.opsForValue().set(SYS_DISK + k, JSONObject.toJSONString(data));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }));
    }

    /**
     * 初始化对设备连通性进行验证
     **/
    public void initDeviceState() {
        tokenMap.forEach((k, v) ->
                methodThreadPollConfig.instance().execute(
                        () -> {
                            RLock lock = redissonClient.getLock(k);
                            // ping当前对应ip的设备
                            try {
                                lock.lock(9, TimeUnit.SECONDS);
                                boolean ping = OpenBrowser.ping(k);
                                Map<String, Object> map = new HashMap<>(5);
                                map.put("time", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
                                map.put("ip", k);
                                if (ping) {
                                    // 标记状态1 ping通
                                    map.put("state", 1);

                                    redisUtil.set(CONNECTION_ERR_SUCCESS + k, JSONObject.toJSONString(map));
                                } else {
                                    // 标记状态为2 ping不通
                                    map.put("state", 2);
                                    redisUtil.set(CONNECTION_ERR_SUCCESS + k, JSONObject.toJSONString(map));
                                    Map<String, Object> errMap = new HashMap<>(5);
                                    errMap.put("msg", "设备ping不通");
                                    errMap.put("time", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
                                    redisUtil.set(CONNECTION_ERR_MESSAGE + k, JSONObject.toJSONString(errMap));
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            } finally {
                                if (lock != null && lock.isHeldByCurrentThread()) {
                                    lock.unlock();
                                }
                            }

                        }
                )
        );
    }

    /**
     * 初始化加载token
     *
     * @author tjy
     **/
    public void initToken() {
        List deviceAccount = getDeviceAccount();
        // 获取列表失败
        if (deviceAccount.isEmpty()) {
            return;
        }

        log.info(" ================ 初始化拉取设备token信息 ================ ");
        // 循环加载设备
        for (Object o : deviceAccount) {
            n8ThreadPool.taskExecutor().execute(() -> {
                // log.info("设备信息 ==> [{}]", o);
                N8RequestEntity entity = JSONObject.parseObject(o.toString(), N8RequestEntity.class);
                // 遍历所有设备，初始化加载token
                try {
                    // 请求token接口
                    N8LoginEntity login = login(entity.getPassword(), entity.getIp());
                    if (login != null && StringUtils.isNotBlank(login.getData().getToken())) {
                        tokenMap.put(entity.getIp(), login.getData().getToken());
                        // 初始化将设备信息存入缓存
                        SysInfoEntity.SysData sysInfo = getSysInfo(entity.getIp());
                        if (sysInfo != null) {
                            redisUtil.set(SYS_INFO + entity.getIp(), JSONObject.toJSONString(sysInfo));
                        }
                    }
                    log.info("" + tokenMap);
                } catch (Exception e) {
                    // log.error("设备 ==> [{}] {}{}", o, e.getMessage(), e);
                }
            });
        }

    }

    /**
     * token定时巡检
     **/
    public void tokenRegularInspection() {
        // 取已存在的token值的key与持久化数据中注册了的设备key进行对比，将差值进行二次请求，如果请求成功，则将token存入
        List deviceAccount = getDeviceAccount();
        // 获取列表失败
        if (deviceAccount.isEmpty()) {
            return;
        }
        // 获取注册列表信息
        List<N8RequestEntity> tokenList = new ArrayList<>();
        for (Object obj : deviceAccount) {
            tokenList.add(JSONObject.parseObject(obj.toString(), N8RequestEntity.class));
        }
        // 获取token列表中的信息
        List<N8RequestEntity> map2List = tokenMap.
                entrySet().stream().map(
                en -> new N8RequestEntity(en.getValue().toString(), en.getKey())).collect(Collectors.toList());

        // 二者取差集
        List<N8RequestEntity> newList = tokenList.stream()
                .filter(
                        item -> !map2List.stream().map(N8RequestEntity::getIp).collect(Collectors.toList())
                                .contains(item.getIp())
                ).collect(Collectors.toList());

        // 循环每个注册过但没有加入token的设备
        newList.forEach(req -> n8ThreadPool.taskExecutor().execute(() -> {
            try {
                // 请求token接口
                N8LoginEntity login = login(req.getPassword(), req.getIp());
                if (login != null && StringUtils.isNotBlank(login.getData().getToken())) {
                    tokenMap.put(req.getIp(), login.getData().getToken());
                }
                // 调用成功后，刷新一遍该设备的硬件信息
                initSendDevRequest();
                initDeviceRunInfo();
                initDeviceAppInfo();
                initDeviceDiskInfo();
            } catch (Exception e) {
                //  log.error(e.getMessage(), e);
            }
        }));

    }

}
