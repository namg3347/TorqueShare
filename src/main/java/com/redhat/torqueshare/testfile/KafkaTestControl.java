package com.redhat.torqueshare.testfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class KafkaTestControl {

    private final KafkaTestProducer kafkaTestProducer;

    @PostMapping("/kafka")
    public String send() {
        kafkaTestProducer.sendTestEvent();
        return "success";
    }


}
