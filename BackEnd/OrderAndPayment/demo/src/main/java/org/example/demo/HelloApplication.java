package org.example.demo;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.example.demo.Service.Service_Order;

@ApplicationPath("/api")
public class HelloApplication extends Application {
Service_Order service ;
}