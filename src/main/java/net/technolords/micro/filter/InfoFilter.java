package net.technolords.micro.filter;

import static net.technolords.micro.filter.InfoFilter.FILTER_ID;
import static net.technolords.micro.filter.InfoFilter.URL_PATTERNS;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@WebFilter (filterName = FILTER_ID, urlPatterns = { URL_PATTERNS })
public class InfoFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoFilter.class);
    public static final String FILTER_ID = "infoFilter";
    public static final String URL_PATTERNS = "/*";
    private static final String LOG_CONTEXT_HTTP_URI = "httpUri";
    private static final String LOG_CONTEXT_HTTP_STATUS = "httpStatus";

    /**
     * Auxiliary method to add the filter directly to the ServlerContextHandler associated with the Jetty Server.
     *
     * Note that when this filter is annotated with:
     *  dispatcherTypes = { DispatcherType.ASYNC })
     * it does NOT work (nor any other type for that matter)!
     *
     * @param server
     *  The Server associated with the Filter.
     */
    public static void registerFilterDirectlyWithServer(Server server) {
        ServletContextHandler servletContextHandler = server.getChildHandlerByClass(ServletContextHandler.class);
        servletContextHandler.addFilter(InfoFilter.class, URL_PATTERNS, EnumSet.of(DispatcherType.ASYNC));
    }

    /**
     * Called when the filter is instantiated, but in this case nothing special needs to be done.
     *
     * @param filterConfig
     *  The filter configuration associated with the initialization.
     *
     * @throws ServletException
     *  When the initialization fails.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Execute the filter logic (as part of a chain). In this case, the time before and after the other chain invocation
     * is measured. The difference is the elapsed time. Note that both the uri associated with the request as well as
     * the http status code associated with the response is 'preserved' in the log context. These values will be
     * substituted in the log patterns %X{httpUri} and %X{httpStatus} respectively.
     *
     * @param servletRequest
     *  The request associated with the filter (chain).
     * @param servletResponse
     *  The response associated with the filter (chain).
     * @param filterChain
     *  The filter chain.
     *
     * @throws IOException
     *  When executing the filter (chain) fails.
     * @throws ServletException
     *  When executing the filter (chain) fails.
     */
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

    /**
     * Update the thread context associated with the logging with specific data. In this case, it is the request URI.
     * The value will eventually end up in the log, where it will substitute for %X{httpUri}
     *
     * Note that updating the thread context can be done directly (using the org.apache.logging.log4j.ThreadContext
     * class, but instead the MDC is used from slf4j)
     *
     * @param httpServletRequest
     *  The request associated with the thread context update.
     */
    private void updateThreadContextWithHttpUri(HttpServletRequest httpServletRequest) {
        MDC.put(LOG_CONTEXT_HTTP_URI, String.valueOf(httpServletRequest.getRequestURI()));
    }

    /**
     * Update the thread context associated with the logging with specific data. In this case, it is the response status.
     * The value will eventually end up in the log, where it will be substituted for %X{httpStatus}
     *
     * Note that updating the thread context can be done directly (using the org.apache.logging.log4j.ThreadContext
     * class, but instead the MDC is used from slf4j)
     *
     * @param httpServletResponse
     *  The response associated with the thread context update.
     */
    private void updateThreadContextWithHttpStatus(HttpServletResponse httpServletResponse) {
        MDC.put(LOG_CONTEXT_HTTP_STATUS, String.valueOf(httpServletResponse.getStatus()));
    }

    /**
     * Called when the server is stopped, in this case, there is nothing to clean up.
     */
    @Override
    public void destroy() {
    }
}
