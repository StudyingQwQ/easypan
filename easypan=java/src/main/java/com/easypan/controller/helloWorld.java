package com.easypan.controller;

import com.easypan.service.impl.EmailCodeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "testControllerAPI",description = "测试用")
public class helloWorld {
    private static final Logger logger = LoggerFactory.getLogger(helloWorld.class);
    @PostMapping("/sayHello")
    @Operation(summary = "测试用接口")
    public String hello()
    {
        logger.info("成功了");
        return "hello wolrd!";
    }
}
