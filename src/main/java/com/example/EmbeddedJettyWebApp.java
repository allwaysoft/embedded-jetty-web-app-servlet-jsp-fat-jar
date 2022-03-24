package com.example;

import java.io.InputStream;
import java.net.URL;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class EmbeddedJettyWebApp {

    public static void main(String[] args) throws Exception {
        Server server = new Server(createThreadPool());

        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(9090);
        connector.setHost("localhost");

        server.addConnector(connector);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        String webDir = EmbeddedJettyWebApp.class.getClassLoader().getResource("src/main/webapp").toExternalForm();

        webAppContext.setResourceBase(webDir);
        System.out.println("ResourceBase:" + webAppContext.getResourceBase());

        webAppContext.setConfigurations(new Configuration[]{
            new AnnotationConfiguration(), new WebXmlConfiguration(),
            new WebInfConfiguration(), new PlusConfiguration(),
            new MetaInfConfiguration(), new FragmentConfiguration(),
            new EnvConfiguration()
        });

        webAppContext.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/target/classes/|.*\\.jar");

        webAppContext.setParentLoaderPriority(true);

        server.setHandler(webAppContext);

        server.setStopAtShutdown(true);

        server.start();
        server.join();
    }

    private static ThreadPool createThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool(5);
        return threadPool;
    }
}
