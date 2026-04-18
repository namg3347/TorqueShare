package com.redhat.torqueshare.filters;

import com.redhat.torqueshare.services.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip =  request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);
        if(!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("too many requests");
            return;
        }
        doFilter(request,response,filterChain);
    }
}
