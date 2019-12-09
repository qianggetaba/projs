package com.ddddd.app2;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping({"","/"})
    public String hi(){
        return "app2-hi";
    }


    @RequestMapping("/app1Feign")
    public String app1Feign(){
        return "app1Feign from app2";
    }
}
