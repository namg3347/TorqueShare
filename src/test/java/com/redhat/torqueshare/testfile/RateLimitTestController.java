package com.redhat.torqueshare.testfile;

import com.redhat.torqueshare.exceptions.RateLimitException;
import com.redhat.torqueshare.services.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/")
@RequiredArgsConstructor
@Slf4j
public class RateLimitTestController {

    private final RateLimitService rateLimitService;

    @PostMapping("rate-limit")
    public String checkRateLimit(HttpServletRequest request)  {
        log.info(" in RateLimitTestController");
        String ip = request.getRemoteAddr();
        log.info(" ip : {}", ip);
        Bucket bucket = rateLimitService.resolveBucket(ip);

        if (!bucket.tryConsume(1)) {
            log.info(" bucket is empty");
            throw new RateLimitException();
        }
        log.info(" bucket is ok");
        return "Request Accepted";
    }
}
