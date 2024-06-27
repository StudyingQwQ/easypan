package com.easypan.controller;

import com.easypan.service.impl.EmailCodeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class helloWorld {
    private static final Logger logger = LoggerFactory.getLogger(helloWorld.class);
    @RequestMapping("/sayHello")
    public String hello()
    {
        logger.info("成功了");
        return "hello wolrd!";
    }
}
