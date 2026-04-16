package com.redhat.torqueshare.testfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class KafkaTestController {

    private final KafkaTestProducer kafkaTestProducer;

    @PostMapping("/kafka")
    public String send() {
        kafkaTestProducer.sendTestEvent();
        return "success";
    }


}
