package com.ddddd.app1;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("sapp2")
public interface FeignApp2 {

    @RequestMapping("/app1Feign")
    public String app1Feign();
}
