package com.ecom.craftbid.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("uri", request.getRequestURI());
        requestData.put("method", request.getMethod());
        requestData.put("parameters", request.getParameterMap());

        String requestJson = gson.toJson(requestData);
        LOG.info("Incoming request: {}", requestJson);

        filterChain.doFilter(request, response);
    }
}
