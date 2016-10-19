package net.technolords.micro.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter (filterName = "infoFilter", urlPatterns = { "/*" })
public class InfoFilter implements Filter {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("init called...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        long endTime = System.currentTimeMillis() - startTime;
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        // epoch, uri, response code and elapsed time
        LOGGER.info("elapsed time: {}, return code: {}, uri: {}", endTime, httpServletResponse.getStatus(), httpServletRequest.getRequestURI());
    }

    @Override
    public void destroy() {
        LOGGER.debug("destroy called...");
    }
}
