package com.infra.gummadibuilt.common.correlation;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/**
 * Generates a unique correlation id for incoming requests and associates it with the logger context.
 * <p>
 * Please note that the correlation id itself is not cryptographically secure. It is only meant to
 * make correlating a bug report with the correct log entries easier.
 */
public class CorrelationIdFilter implements Filter {

    private static final String MDC_CORRELATION_ID = "CID";

    private static final String CID_HEADER = "X-Correlation-Id";

    private static final Random RANDOM_GEN = new Random();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    	/*
    	 //Called by the web container to indicate to a filter that it is being placed into service. 
    	 */
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // If there's already a correlation in the MDC, do not do anything
        if (MDC.get(MDC_CORRELATION_ID) != null) {
            chain.doFilter(request, response);
            return;
        }

        String correlationId = generateCorrelationId();

        // Also replicate the correlation id to the client
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(CID_HEADER, correlationId);
        }

        try (MDC.MDCCloseable mdc = MDC.putCloseable(MDC_CORRELATION_ID, correlationId)) {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
    	/*
    	//Called by the web container to indicate to a filter that it is being taken out of service
    	*/

    }

    private String generateCorrelationId() {
        byte[] key = new byte[4];
        RANDOM_GEN.nextBytes(key);
        return Hex.encodeHexString(key);
    }

}
