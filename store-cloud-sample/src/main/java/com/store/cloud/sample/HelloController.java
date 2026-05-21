package com.store.cloud.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    private final String demoMsg;

    public HelloController(@Value("${demo.msg:}") String demoMsg) {
        this.demoMsg = demoMsg;
    }

    @GetMapping("/hello")
    public String hello() {
        String msg = demoMsg.isBlank() ? "demo.msg 未就绪（Apollo 未连上或未发布键）" : demoMsg;
        return "store-cloud-sample ok, demo.msg=" + msg;
    }
}
