package com.weds.devmanages.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @title java调用浏览器打开指定页面
 */
@Slf4j
public class OpenBrowser {

    public static void browse1(String url) throws Exception {
        // 获取操作系统的名字
        String osName = System.getProperty("os.name", "");
        // windows
        if (osName.startsWith("Windows")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            // Mac
        } else if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
            openURL.invoke(null, url);
        } else {// Unix or Linux
            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            // 执行代码，在brower有值后跳出
            for (int count = 0; count < browsers.length && browser == null; count++) {
                // 这里是如果进程创建成功了，==0是表示正常结束。
                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                    browser = browsers[count];
                }
            }

            if (browser == null) {
                throw new RuntimeException("未找到任何可用的浏览器");
            } else {// 这个值在上面已经成功的得到了一个进程。
                Runtime.getRuntime().exec(new String[]{browser, url});
            }
        }
    }

    /**
     * @title 使用默认浏览器打开
     * @author Xingbz
     */
    public void browse2(String url) throws Exception {
        Desktop desktop = Desktop.getDesktop();
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = new URI(url);
            desktop.browse(uri);
        }
    }

    /**
     * @title 通过获取环境变量的浏览器路径, 然后启动浏览器
     * @author Xingbz
     */
    public void browse3(String url) throws Exception {
        String firefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
        Map map = System.getenv();
        for (Object key : map.keySet()) {
            String value = (String) map.get(key);
            if (value.contains("firefox.exe")) {
                firefox = value;
                break;
            }
        }
        Runtime.getRuntime().exec(new String[]{firefox, url});
    }

    /**
     * @title 启用cmd运行IE的方式来打开网址
     * @author Xingbz
     */
    public void browse4(String url) throws Exception {
        Runtime.getRuntime().exec("cmd /c start " + url);//启用cmd运行默认浏览器
//        Runtime.getRuntime().exec("cmd /c start iexplore " + url);//启用cmd运行IE
    }

    /**
     * @title 利用java.lang.ProcessBuilder类创建系统进程的能力，通过浏览器地址启动浏览器，并将网址作为参数传送给浏览器。
     * ProcessBuilder类是J2SE1.5在java.lang中新添加的一个新类，此类用于创建操作系统进程，它提供一种启动和管理进程（也就是应用程序）的方法。
     * @author Xingbz
     */
    public void browse5(String url) throws Exception {
        ProcessBuilder proc = new ProcessBuilder("C:\\Program Files\\Mozilla Firefox\\firefox.exe",
                url);
        proc.start();
    }

    /**
     * 启动应用程序
     *
     * @return
     */
    public void startProgram(String programPath) {
        log.info("启动应用程序：" + programPath);
        if (StringUtils.isNotBlank(programPath)) {
            try {
                String programName = programPath.substring(programPath.lastIndexOf("/") + 1, programPath.lastIndexOf("."));
                List<String> list = new ArrayList<>();
                list.add("cmd.exe");
                list.add("/c");
                list.add("start");
                list.add("\"" + programName + "\"");
                list.add("\"" + programPath + "\"");
                ProcessBuilder pBuilder = new ProcessBuilder(list);
                pBuilder.start();
            } catch (Exception e) {
                log.error("应用程序：" + programPath + "不存在！");
            }
        }
    }

    //使用ip测试网络连通性
    public static boolean ping(String ipAddress) {
        try {
            //超时时间
            int timeOut = 1000;
            // 当返回值是true时，说明host是可用的，false则不可。
            return InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}