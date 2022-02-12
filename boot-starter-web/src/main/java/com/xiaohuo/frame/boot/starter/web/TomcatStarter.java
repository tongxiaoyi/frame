package com.xiaohuo.frame.boot.starter.web;

import com.xiaohuo.frame.boot.annotation.BootStarter;
import com.xiaohuo.frame.boot.annotation.BootStarterMethod;
import com.xiaohuo.frame.j2ee.servlet.ActionServlet;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

@BootStarter
public class TomcatStarter {

    private static int PORT = 8080;
    private static String CONTEXT_PATH = "/com/xiaohuo";
    private static String SERVLET_NAME = "actionServlet";

    @BootStarterMethod
    public static void main(String[] args) throws LifecycleException {
        //创建tomcat服务器
        Tomcat tomcatServer = new Tomcat();
        //绑定端口号
        tomcatServer.setPort(PORT);
        //设置是否自动部署
        tomcatServer.getHost().setAutoDeploy(false);
        //创建上下文
        StandardContext standardContext = new StandardContext();
        //设置上下文
        standardContext.setPath(CONTEXT_PATH);
        //监听上下文
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
        //tomcat容器添加standardContext
        tomcatServer.getHost().addChild(standardContext);
        //创建servlet
        tomcatServer.addServlet(CONTEXT_PATH, SERVLET_NAME, new ActionServlet());
        //servlet映射
        standardContext.addServletMappingDecoded("/index", SERVLET_NAME);
        tomcatServer.start();
        System.out.println("启动tomcat成功");
        //异步进行接收请求
        tomcatServer.getServer().await();
    }

}
