package com.ddddd.app1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestController {

    @RequestMapping({"","/"})
    public String hi(){
        return "app1-hi";
    }

    @Autowired
    private FeignApp2 feignApp2;

    @RequestMapping("/feignTest")
    public String feignTest(){
        return feignApp2.app1Feign();
    }

}
