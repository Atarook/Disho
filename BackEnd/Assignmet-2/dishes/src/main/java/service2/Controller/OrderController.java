package service2.Controller;


import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.TimeoutException;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import jakarta.ws.rs.core.Response;

import jakarta.ejb.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import service2.DAL.DatabaseConnection;
import service2.Model.OrderItem;
import service2.Services.*;

@Path("hell")
@Produces(MediaType.TEXT_PLAIN)
public class OrderController {
  

    // service_msg s = new service_msg();

    public OrderController() throws IOException, TimeoutException {
    }

    @POST
    @Path("msg")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response test(Long customerId, List<OrderItem> cartitems) {
        try {
            System.out.println("hello");
            //s.processOrder(customerId, cartitems);
            return Response.ok("OK").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("GetOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOrders() {
        try {
            List<Order> orders;
            orders = (List<Order>) getOrders();
            
            return Response.ok(orders).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("GetSoldOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSoldOrders(Long customerId) {
        try {
            List<OrderItem> sold_items;
            sold_items = (List<OrderItem>) getSoldOrders(customerId);
            return Response.ok(sold_items).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

@POST
@Path("tt")
@Produces(MediaType.TEXT_PLAIN)
public Response testtt() {
    try {
        // Create dummy OrderItem
        OrderItem o = new OrderItem();
        o.setDishId(1);
        o.setDishName("Daw");
        o.setDishPrice(1111.0);
        o.setQuantity(122);
        o.company_id = 1L;

        // Add to database using ordercon
        // You need to provide a valid Connection object here
        DatabaseConnection con = new DatabaseConnection();
        Connection conn =con.getConnection() ;
        service2.DAL.OrderItemDAL orderCon = new service2.DAL.OrderItemDAL(conn);
        orderCon.addOrderItem(o);

        return Response.ok("Dummy OrderItem added!").build();
    } catch (Exception e) {
        e.printStackTrace();
        return Response.serverError().entity(e.getMessage()).build();
    }
}
}