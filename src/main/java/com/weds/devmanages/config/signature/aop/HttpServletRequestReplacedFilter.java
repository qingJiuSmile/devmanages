package com.weds.devmanages.config.signature.aop;



import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class HttpServletRequestReplacedFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if(request instanceof HttpServletRequest) {
            // 创建一个新的封装好的ServletRequest
            requestWrapper = new RequestReaderHttpServletRequestWrapper((HttpServletRequest) request);
        }
        // 获取请求中的流如何将取出来的字符串，再次转换成流，然后把它放入到新request对象中。
        // 在chain.doFiler方法中传递新的request对象
        if(requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }
    @Override
    public void destroy() {

    }
    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

}
