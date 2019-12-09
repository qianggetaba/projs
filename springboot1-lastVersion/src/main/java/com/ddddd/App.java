package com.ddddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


@SpringBootApplication
public class App {


    public static void main(String[] args) {



//        LinkedHashSet names = new LinkedHashSet(SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, ClassLoader.getSystemClassLoader()));
//        System.out.println(names);

        SpringApplication.run(App.class);


        System.out.println("done");

    }
}
