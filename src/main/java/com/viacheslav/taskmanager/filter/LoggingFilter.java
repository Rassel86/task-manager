package com.viacheslav.taskmanager.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        if (!path.startsWith("/api")) {
            filterChain.doFilter(request, response);
        }

        long start = System.currentTimeMillis();
        log.info("Request: {} {}", method, path);


        try {
            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();

            if(status >= 500) {
                log.error("Response: {} {} - {} ({} ms)", method, path, status, duration);
            } else if (status >= 400) {
                log.warn("Response: {} {} - {} ({} ms)", method, path, status, duration);
            } else {
                log.info("Response: {} {} - {} ({} ms)", method, path, status, duration);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("Response: {} {} - ERROR after {} ms: {})", method, path, duration, e.getMessage());
            throw e;
        }

    }
}
