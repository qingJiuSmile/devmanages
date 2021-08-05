package com.weds.devmanages.config.signature.aop;

import com.weds.devmanages.config.log.JsonResult;
import com.weds.devmanages.service.DevRestart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Component
@Order(2)
public class SignatureAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureAspect.class);

    @Autowired
    private DevRestart devRestart;


    @Pointcut("(@annotation(com.weds.devmanages.service.sign.Signature)) || (@within(com.weds.devmanages.service.sign.Signature))")
    private void signAspect() {
    }


    @Around("signAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        Object proceed = null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // 获取path variable（对应@PathVariable）
        String[] paths = null;
        ServletWebRequest webRequest = new ServletWebRequest(request, null);
        Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            paths = uriTemplateVars.values().toArray(new String[]{});
        }
        try {
            proceed = pjp.proceed();
            // 如果程序异常 进行重置token操作
        } catch (Throwable throwable) {
            if ("500 Internal Server Error: [no permission]".equals(throwable.getMessage()) && paths != null) {
                LOGGER.info("token过期刷新token [{}]", devRestart.refreshToken(paths[0]));
                return new JsonResult(601, "token已过期，已为您刷新token，请再试一次");
            }
        }
        return proceed;
    }


}