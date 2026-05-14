package com.redhat.torqueshare.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.torqueshare.exceptions.ApiErrorResponse;
import com.redhat.torqueshare.services.RateLimitService;

import io.github.bucket4j.Bucket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("RateLimitFilter started");

        String ip = request.getRemoteAddr();

        Bucket bucket =
                rateLimitService.resolveBucket(ip);

        if (!bucket.tryConsume(1)) {

            log.info("Too many requests for ip: {}",ip);

            response.setStatus(429);

            response.setContentType("application/json");

            response.setCharacterEncoding("UTF-8");

            response.setHeader(
                    "Access-Control-Allow-Origin",
                    "https://torque-share.vercel.app"
            );

            ApiErrorResponse error =
                    ApiErrorResponse.builder()
                            .timestamp(Instant.now())
                            .status(429)
                            .error("RATE_LIMIT_EXCEEDED")
                            .message("Too many requests")
                            .build();

            response.getWriter().write(
                    objectMapper.writeValueAsString(error)
            );

            return;
        }

        log.info("Token consumed");

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path = request.getServletPath();

        return path.equals("/health");
    }
}