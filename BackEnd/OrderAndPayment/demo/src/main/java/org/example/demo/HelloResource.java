package org.example.demo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.example.demo.Service.Service_Order;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("hell")
@Produces(MediaType.TEXT_PLAIN)
public class HelloResource {
//    @GET
//    @Produces("text/plain")
//    public String hello() {
//        return "Hello, World!";
//    }

    Service_Order s = new Service_Order();

    public HelloResource() throws IOException, TimeoutException {
    }

    @GET
    @Path("msg")
    @Produces(MediaType.TEXT_PLAIN)
    public void  test() throws IOException, TimeoutException {

        System.out.println("hello");
        s.processOrder(1);

    }
//    @GET
//    @Path("re")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String test2() {
//        return s.receive();
//    }
//    @POST
//    @Path("order")
//    public createorder(long ){
//
//    }
}