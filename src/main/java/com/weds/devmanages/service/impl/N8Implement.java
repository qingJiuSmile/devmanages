package com.weds.devmanages.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.weds.devmanages.entity.*;
import com.weds.devmanages.service.N8ApiInterface;
import com.weds.devmanages.util.PageUtil;
import com.weds.devmanages.util.RedisUtil;
import com.weds.devmanages.util.RestTemplateUtils;
import com.weds.devmanages.util.TaskQueueDaemonThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * N8 设备接口实现
 *
 * @author tjy
 **/

@Slf4j
@Service
public class N8Implement implements N8ApiInterface {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TaskQueueDaemonThread taskQueueDaemonThread;

    @Autowired
    private RedisUtil redisUtil;

    public static String N8_USER_ACCOUNT = "N8:USER:ACCOUNT";

    private static long DELAYED_TIME = 0;

    private static ThreadPoolExecutor n8ThreadPoll = new ThreadPoolExecutor(10, 10 * Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat("N8-pool-%d").setDaemon(true).build(), new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * 存储token容器 格式 ：K（设备IP） - V （token值）
     */
    private static Map<String, Object> tokenMap = new ConcurrentHashMap<>();

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
     * 存设备磁盘信息 格式: K（设备IP） - V （应用信息）
     */
    private Map<String, DiskInfoEntity.DiskInfoData> devDiskInfoMap = new ConcurrentHashMap<>();

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
        devSysInfoMap.forEach((k, v) -> list.add(v));

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
        devAppInfoMap.forEach((k, v) -> list.add(v));

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
    public DiskInfoEntity getDevDiskInfoMap(PublicParam search) {
        DiskInfoEntity diskInfoEntity = new DiskInfoEntity();
        List<DiskInfoEntity.DiskInfoData> list = new ArrayList<>();
        devDiskInfoMap.forEach((k, v) -> list.add(v));

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
        devRunInfoMap.forEach((k, v) -> list.add(v));

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
     * 请求前缀
     */
    private static String REQUEST_PREFIX = "http://";

    /**
     * 请求后缀
     */
    private static String REQUEST_SUFFIX = "/user/login";


    /***********************************************************************************************
     ***                                 设 备 实 现 方 法                                        ***
     ***********************************************************************************************
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    @Override
    public boolean reStart(String ip) {
        // 拼接接口地址
        String url = REQUEST_PREFIX + ip + "/config/reboot";
        // 登录设备
        boolean login = refreshToken(ip);
        log.info("登录 ==>[{}]", login);
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

    @Override
    public SysInfoEntity getSysInfo(String ip) {

        String url = REQUEST_PREFIX + ip + "/data/get_sys_info";

        // log.info("当前ip的token为 ==> [{}]", tokenMap.get(ip).toString());
        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return JSONObject.parseObject(body.toJSONString(), SysInfoEntity.class);
        }
        return null;
    }

    @Override
    public AppInfoEntity getAppInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_app_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return JSONObject.parseObject(body.toJSONString(), AppInfoEntity.class);
        }
        return null;
    }

    @Override
    public RunInfoEntity getRunInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_run_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return JSONObject.parseObject(body.toJSONString(), RunInfoEntity.class);
        }
        return null;
    }

    @Override
    public DiskInfoEntity getDiskInfo(String ip) {
        String url = REQUEST_PREFIX + ip + "/data/get_disk_info";

        JSONObject body = restTemplateUtils.post(url, addHeader(ip), null, JSONObject.class, new HashMap<>()).getBody();
        if (body != null) {
            return JSONObject.parseObject(body.toJSONString(), DiskInfoEntity.class);
        }
        return null;
    }


    @Override
    public void asyncGetAppInfo() {
        tokenMap.forEach((k, v) -> n8ThreadPoll.execute(() -> {
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
                    AppInfoEntity appInfoEntity = JSONObject.parseObject(body.toJSONString(), AppInfoEntity.class);
                    // 判断响应情况
                    if (appInfoEntity.getCode().equals(0) && "OK".equals(appInfoEntity.getMsg())) {
                        appInfoEntity.getData().setDevIp(k);
                        devAppInfoMap.put(k, appInfoEntity.getData());
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
        tokenMap.forEach((k, v) -> n8ThreadPoll.execute(() -> {
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
                    RunInfoEntity runInfoEntity = JSONObject.parseObject(body.toJSONString(), RunInfoEntity.class);
                    // 判断响应情况
                    if (runInfoEntity.getCode().equals(0) && "OK".equals(runInfoEntity.getMsg())) {
                        runInfoEntity.getData().setDevIp(k);
                        devRunInfoMap.put(k, runInfoEntity.getData());
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
        tokenMap.forEach((k, v) -> n8ThreadPoll.execute(() -> {
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
                    SysInfoEntity sysInfoEntity = JSONObject.parseObject(body.toJSONString(), SysInfoEntity.class);
                    // 判断响应情况
                    if (sysInfoEntity.getCode().equals(0) && "OK".equals(sysInfoEntity.getMsg())) {
                        sysInfoEntity.getData().setDevIp(k);
                        devSysInfoMap.put(k, sysInfoEntity.getData());
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
        tokenMap.forEach((k, v) -> n8ThreadPoll.execute(() -> {
            // log.info("当前ip的token为 ==> [{}]", tokenMap.get(k).toString());
            String url = REQUEST_PREFIX + k + "/data/get_disk_info";

            // 请求对应ip的接口
            try {
                // 新增前，先删除原有数据
                devDiskInfoMap.remove(k);
                JSONObject body = restTemplateUtils.post(url, addHeader(k), null, JSONObject.class, new HashMap<>()).getBody();
                // 如果请求失败再次请求 加入队列 TODO
                if (body == null) {
                    addQueue(restartDiskInfo(k));
                } else {
                    DiskInfoEntity diskInfoEntity = JSONObject.parseObject(body.toJSONString(), DiskInfoEntity.class);
                    // 判断响应情况
                    if (diskInfoEntity.getCode().equals(0) && "OK".equals(diskInfoEntity.getMsg())) {
                        diskInfoEntity.getData().setDevIp(k);
                        devDiskInfoMap.put(k, diskInfoEntity.getData());
                    } else {
                        // 请求错误加入队列重新请求 TODO
                        addQueue(restartDiskInfo(k));
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                // 失败时，也加入队列，再次请求 TODO
                addQueue(restartDiskInfo(k));
            }
        }));
    }

    @Override
    public boolean deviceUpdate(String ip, String pwd, MultipartFile file) {
        String url = REQUEST_PREFIX + "localhost:8081" + "/excel/import/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      //  headers.add("Cookie", "jtoken=" + tokenMap.get(ip).toString());
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        try {
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            log.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
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

            SysInfoEntity sysInfo = getSysInfo(ip);
            if (sysInfo == null) {
                return;
            }
            // 判断响应情况
            if (sysInfo.getCode().equals(0) && "OK".equals(sysInfo.getMsg())) {
                sysInfo.getData().setDevIp(ip);
                devSysInfoMap.put(ip, sysInfo.getData());
            }
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

            AppInfoEntity appInfo = getAppInfo(ip);
            if (appInfo == null) {
                return;
            }
            // 判断响应情况
            if (appInfo.getCode().equals(0) && "OK".equals(appInfo.getMsg())) {
                appInfo.getData().setDevIp(ip);
                devAppInfoMap.put(ip, appInfo.getData());
            }
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

            RunInfoEntity runInfo = getRunInfo(ip);
            if (runInfo == null) {
                return;
            }
            // 判断响应情况
            if (runInfo.getCode().equals(0) && "OK".equals(runInfo.getMsg())) {
                runInfo.getData().setDevIp(ip);
                devRunInfoMap.put(ip, runInfo.getData());
            }
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

            DiskInfoEntity diskInfo = getDiskInfo(ip);
            if (diskInfo == null) {
                return;
            }
            // 判断响应情况
            if (diskInfo.getCode().equals(0) && "OK".equals(diskInfo.getMsg())) {
                diskInfo.getData().setDevIp(ip);
                devDiskInfoMap.put(ip, diskInfo.getData());
            }
        };
    }

    // ==================================================  工具方法   ==================================================

    /**
     * 添加请求头
     *
     * @param ip 设备ip
     * @return {@link Map< String, String>}
     * @author tjy
     **/
    private Map<String, String> addHeader(String ip) {
        Map<String, String> headerMap = new HashMap<>(5);
        headerMap.put("Cookie", "jtoken=" + tokenMap.get(ip).toString());
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
        return redisUtil.lGet(N8_USER_ACCOUNT, 0, -1);
    }

    /**
     * 刷新某台设备的token，适用与请求失败后，进行刷新
     *
     * @param ip 设备ip
     * @return {@link boolean}
     * @author tjy
     * @date 2021/7/3
     **/
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
    private void addQueue(Runnable runnable) {
        // 将失败的请求放如阻塞队列，刷新token再次请求，如果成功则加入map中。
        taskQueueDaemonThread.put(DELAYED_TIME, runnable);
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
            n8ThreadPoll.execute(() -> {
                // log.info("设备信息 ==> [{}]", o);
                N8RequestEntity entity = JSONObject.parseObject(o.toString(), N8RequestEntity.class);
                // 遍历所有设备，初始化加载token
                try {
                    // 请求token接口
                    N8LoginEntity login = login(entity.getPassword(), entity.getIp());
                    if (login != null && StringUtils.isNotBlank(login.getData().getToken())) {
                        tokenMap.put(entity.getIp(), login.getData().getToken());
                    }
                    log.info("" + tokenMap);
                } catch (Exception e) {
                    log.error("设备 ==> [{}] {}{}", o, e.getMessage(), e);
                }
            });
        }

    }


}
