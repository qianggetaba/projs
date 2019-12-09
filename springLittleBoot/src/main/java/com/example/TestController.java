package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    private static Logger log = LogManager.getLogger(TestController.class);

    @GetMapping({"","/"})
    public String hi(){
        log.info("in "+ TestController.class.getName());
        return "spring mvc";
    }

    // default not mapping url
    @GetMapping("/**")
    public String defaultPage(HttpServletRequest request){
        StringBuilder stringBuilder = new StringBuilder(request.getRequestURL().toString());
        if(!StringUtils.isEmpty(request.getQueryString()))
            stringBuilder.append("?" + request.getQueryString());

        return "page not found:  "+ stringBuilder.toString();
    }
}
