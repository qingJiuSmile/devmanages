package com.weds.devmanages.config.signature;

import com.alibaba.fastjson.JSONObject;
import com.weds.devmanages.config.log.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * @author 签名校验过滤器
 */
@Slf4j
public class SignStreamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String contentType = req.getContentType();
        String method = "multipart/form-data";
        try {
            if (contentType != null && contentType.contains(method)) {
                // 将转化后的 request 放入过滤链中
                request = new StandardServletMultipartResolver().resolveMultipart(request);
            }

            checkSign(request, response);
            // 扩展request，使其能够能够重复读取requestBody
            ServletRequest requestWrapper = new RequestWrapper(request);
            // 这里需要放行，但是要注意放行的 request是requestWrapper
            chain.doFilter(requestWrapper, resp);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setContentType("application/json; charset=utf-8");
            // 校验失败返回
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.append(JSONObject.toJSONString(new JsonResult(601, e.getMessage(), "")));
        }


    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        log.info("================== 开启接口签名校验 ================== ");
    }

    private void checkSign(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String oldSign = request.getHeader("WEDS-SIGN");
        String appId = request.getHeader("appId");
        String appSecret = request.getHeader("appSecret");
        if (StringUtils.isBlank(oldSign) || StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            throw new Exception("Header缺少必要验证参数");
        }

        // 检查appId是否存在 TODO
        if (!"321".equals(appId)) {
            throw new Exception("appId不存在");
        }
        // 检查appSecret是否合法 TODO
        if (!"123".equals(appSecret)) {
            throw new Exception("appSecret不合法");
        }

        // 获取body（对应@RequestBody）
        String body = null;
        body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

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

        String newSign = SignUtil.sign(body, params, paths, appSecret);
        log.info("请求签名为 ==>" + newSign);
        if (!newSign.equals(oldSign)) {
            throw new Exception("签名不一致...");
        }
    }

}
