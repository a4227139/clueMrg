package com.wa.cluemrg.filter;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.mock.web.DelegatingServletInputStream;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;

@Log4j2
@WebFilter(urlPatterns = "/*", filterName = "channelFilter")
public class ChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 强制转换为HttpServlet请求和响应对象
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (path.contains("/static")){
            log.info("Request URI: {}", path);
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }
        boolean isPrintRequest=true,isPrintResponse=true;
        if (path.contains("upload")){
            isPrintRequest=false;
        }
        if (path.contains("export")||path.contains("download")){
            isPrintResponse=false;
        }

        // 备份请求和响应
        RequestWrapper requestWrapper = new RequestWrapper(httpRequest);
        ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

        if (!requestWrapper.getRequestURI().endsWith(".html")){
            // 打印请求信息
            String requestContent = new String(requestWrapper.getRequestBody().getBytes(), requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() : StandardCharsets.UTF_8.name());
            log.info("Request URI: {}", requestWrapper.getRequestURI());
            log.info("Request Method: {}", requestWrapper.getMethod());
            log.info("Request Headers: {}", requestWrapper.getHeaderNames());
            if (isPrintRequest){
                log.info("Request Body: {}", requestContent);
            }else {
                log.info("Request Body: {}", "具体内容请看导入文件");
            }
        }
        // 调用过滤器链的下一个过滤器或处理器
        filterChain.doFilter(requestWrapper, responseWrapper);
        byte[] responseContent = responseWrapper.toByteArray();
        String responseContentStr = new String(responseContent, StandardCharsets.UTF_8.name());

        if (!requestWrapper.getRequestURI().endsWith(".html")) {
            // 打印响应信息
            log.info("Response Status: {}", responseWrapper.getStatus());
            log.info("Response Headers: {}", responseWrapper.getHeaderNames());
            if (isPrintResponse){
                log.info("Response Body: {}", responseContentStr);
            }else {
                log.info("Response Body: {}", "具体内容请看导出文件");
            }
        }
        // 将响应内容写回客户端
        httpResponse.getOutputStream().write(responseContent);

    }

    @Override
    public void destroy() {
    }

    /**
     * 获取请求或响应的头部信息
     */
    private String getHeadersInfo(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append(",");
        }
        return headers.toString();
    }

    private String getHeadersInfo(HttpServletResponse response) {
        StringBuilder headers = new StringBuilder();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            String headerValue = response.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append(",");
        }
        return headers.toString();
    }
}