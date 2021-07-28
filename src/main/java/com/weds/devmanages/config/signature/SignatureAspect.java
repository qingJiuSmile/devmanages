package com.weds.devmanages.config.signature;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Aspect
@Component
@Order(1)
public class SignatureAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureAspect.class);


    /*  @Pointcut("execution(* com.weds.devmanages.controller..*.*(..))")
      private void signAspect() {
      }
    */

    @Pointcut("(@annotation(com.weds.devmanages.service.sign.Signature))")
    private void signAspect() {
    }


    @Around("signAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        try {
            this.checkSign();
            return pjp.proceed();
        } catch (Throwable e) {
            LOGGER.error("SignatureAspect>>>>>>>>", e);
            throw e;
        }
    }

    private void checkSign() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String oldSign = request.getHeader("WEDS-SIGN");
        String appId = request.getHeader("appId");
        String appSecret = request.getHeader("appSecret");
        if (StringUtils.isBlank(oldSign) || StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            throw new RuntimeException("Header缺少必要验证参数");
        }
        //获取body（对应@RequestBody）
        String body = null;
        if (request instanceof RequestWrapper) {
            body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        }

        // 获取parameters（对应@RequestParam）
        Map<String, String[]> params = null;
        if (!CollectionUtils.isEmpty(request.getParameterMap())) {
            params = request.getParameterMap();
        }

        // 获取path variable（对应@PathVariable）
        String[] paths = null;
        ServletWebRequest webRequest = new ServletWebRequest(request, null);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            paths = uriTemplateVars.values().toArray(new String[]{});
        }
        try {
            String newSign = SignUtil.sign(body, params, paths,appSecret);
            LOGGER.info("签名为 ==>" + newSign);
            if (!newSign.equals(oldSign)) {
                throw new RuntimeException("签名不一致...");
            }
        } catch (Exception e) {
            throw new RuntimeException("验签出错...", e);
        }
    }
}