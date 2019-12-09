package com.example;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class App {

    private static Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        Tomcat tomcat = new Tomcat();
        tomcat.setSilent(true);

        // tomcat config

        //CATALINA_BASE home dir
        String baseDir = "./tomcat_tmp/";
        tomcat.setBaseDir(baseDir);
        tomcat.setPort(8080);
        tomcat.setHostname("127.0.0.1");  //default localhost

        // tomcat component config, server->service->connector->engine->host->context
        Server server    = tomcat.getServer();   //one server for one tomcat
        Service service   = tomcat.getService();
        Connector connector = tomcat.getConnector();  // default HTTP/1.1 connector
        Engine engine    = tomcat.getEngine();
        Host host      = tomcat.getHost();
        Context context    = tomcat.addContext(host,"","../"); // dir is "", means create webapps dir under baseDir

        String servletName1 = "myTestServlet";
        Tomcat.addServlet(context,servletName1,new TestServlet());
        context.addServletMappingDecoded("/TestServlet",servletName1);

        // add spring mvc servlet
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.scan("com.example");
        // read yaml config to application context
        StandardEnvironment environment = new StandardEnvironment();

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        environment.getPropertySources().addAfter(
                StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new PropertiesPropertySource("test",yaml.getObject()));

        log.debug("read config test: "+ environment.getProperty("my.testconfig"));
        appContext.setEnvironment(environment);
        // application context refresh is to init
        appContext.refresh();

        String servletName2 = "myspringmvc";
        Tomcat.addServlet(context,servletName2,new DispatcherServlet(appContext));
        context.addServletMappingDecoded("/*",servletName2);

        // start embedded tomcat server
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            log.error("Embedded tomcat server start failed!");
            try {
                tomcat.stop();
            } catch (LifecycleException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return;
        }
        log.info("Embedded tomcat server started  ...............");
        tomcat.getServer().await();
    }

    static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("text/plain;charset=UTF-8");
            ServletOutputStream out = resp.getOutputStream();
            out.print("This is TestServlet");
        }
    }
}
