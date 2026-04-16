package com.redhat.torqueshare.testfile;

import com.redhat.torqueshare.exceptions.RateLimitException;
import com.redhat.torqueshare.services.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/")
@RequiredArgsConstructor
public class RateLimitTestController {

    private final RateLimitService rateLimitService;

    @PostMapping("rate-limit")
    public String checkRateLimit(HttpServletRequest request)  {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimitService.resolveBucket(ip);
        String reply = "Request Accepted";
        if (!bucket.tryConsume(1)) {
            throw new RateLimitException();
        }
        else return reply;
    }
}
