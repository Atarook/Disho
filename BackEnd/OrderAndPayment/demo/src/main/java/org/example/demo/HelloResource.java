package org.example.demo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.demo.Model.Order;
import org.example.demo.Model.OrderItem;
import org.example.demo.Service.Service_Order;
import org.example.demo.Service.service_msg;
import java.util.*;
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

    service_msg s = new service_msg();

    public HelloResource() throws IOException, TimeoutException {
    }
    @POST
    @Path("msg")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response  test(Long customerId,List<OrderItem> cartitems) {
        try{
        System.out.println("hello");
        s.processOrder(customerId,cartitems);
        return Response.ok("OK").build();}
        catch(Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("GetOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOrders() {
        try{
            List<Order> orders;
            orders= (List<Order>) getOrders();
            return Response.ok(orders).build();
        }
        catch(Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    //sm3ny ??
    // 2ool hena

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