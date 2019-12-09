package com.ddddd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping({"","/"})
    public String dd(){
        return "d";
    }

    @GetMapping("/dd")
    public String dd2(){
        return "d2";
    }
}
