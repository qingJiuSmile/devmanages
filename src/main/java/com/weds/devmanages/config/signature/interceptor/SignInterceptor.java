package com.weds.devmanages.config.signature.interceptor;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.config.signature.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * 签名拦截器配置
 *
 * @author tjy
 **/
@Slf4j
public class SignInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            // 检查请求参数签名
            SignUtil.checkSign(request, response);
            return true;
        } catch (Exception e) {
            response.setContentType("application/json; charset=utf-8");
            // 校验失败返回
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            log.error(e.getMessage(), e);
            out.append(JSONObject.toJSONString(new JsonResult(601, e.getMessage(), "")));
        }
        return false;

    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}