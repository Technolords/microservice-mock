package net.technolords.micro.filter;

import static net.technolords.micro.filter.InfoFilter.FILTER_ID;
import static net.technolords.micro.filter.InfoFilter.URL_PATTERNS;

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

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter (filterName = FILTER_ID, urlPatterns = { URL_PATTERNS })
public class InfoFilter implements Filter {
    public static final String FILTER_ID = "infoFilter";
    public static final String URL_PATTERNS = "\"/*\"";
    private static final String LOG_CONTEXT_HTTP_URI = "httpUri";
    private static final String LOG_CONTEXT_HTTP_STATUS = "httpStatus";
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
        // Update logging thread with meta data
        this.updateThreadContextWithHttpUri((HttpServletRequest) servletRequest);
        this.updateThreadContextWithHttpStatus((HttpServletResponse) servletResponse);
        // Log: epoch, uri, response code and elapsed time, using: pattern="%d{UNIX_MILLIS} %X{httpUri} %X{httpStatus} %m%n"
        LOGGER.info("{}", endTime);
    }

    private void updateThreadContextWithHttpUri(HttpServletRequest httpServletRequest) {
        ThreadContext.put(LOG_CONTEXT_HTTP_URI, String.valueOf(httpServletRequest.getRequestURI()));
    }

    private void updateThreadContextWithHttpStatus(HttpServletResponse httpServletResponse) {
        ThreadContext.put(LOG_CONTEXT_HTTP_STATUS, String.valueOf(httpServletResponse.getStatus()));
    }

    @Override
    public void destroy() {
        LOGGER.debug("destroy called...");
    }
}
