package service2.Controller;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonProperty;

import service2.Model.Order;
import jakarta.ws.rs.core.Response;

import jakarta.ejb.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import service2.DAL.DatabaseConnection;
import service2.DAL.OrderItemDAL;
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
            // s.processOrder(customerId, cartitems);
            return Response.ok("OK").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("GetOrders")
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
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
            Connection conn = con.getConnection();
            service2.DAL.OrderItemDAL orderCon = new service2.DAL.OrderItemDAL(conn);
            orderCon.addOrderItem(o);

            return Response.ok("Dummy OrderItem added!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    public class ConvertRequest {
    @JsonProperty("quantity")
    public int quantity;
    
    @JsonProperty("dish_id")
    public int dish_id;
    
    @JsonProperty("dish_name")
    public String dish_name;
    
    @JsonProperty("dish_price")
    public double dish_price;
    
    @JsonProperty("company_id")
    public long company_id;
    

        public ConvertRequest() {
        }

        // Getters and setters
        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getDish_id() {
            return dish_id;
        }

        public void setDish_id(int dish_id) {
            this.dish_id = dish_id;
        }

        public String getDish_name() {
            return dish_name;
        }

        public void setDish_name(String dish_name) {
            this.dish_name = dish_name;
        }

        public double getDish_price() {
            return dish_price;
        }

        public void setDish_price(double dish_price) {
            this.dish_price = dish_price;
        }

        public long getCompany_id() {
            return company_id;
        }

        public void setCompany_id(long company_id) {
            this.company_id = company_id;
        }

        // (plus getters/setters if your JSON provider needs them)
    }

    @POST
    @Path("convert")
    public Response convert(@RequestBody OrderItem orderitem) {
        try {
            // Create a new OrderItem from the request data
            // OrderItem orderitem = new OrderItem();
            // orderitem.company_id = request.company_id;
            // orderitem.setDishId(request.dish_id);
            // orderitem.setDishName(request.dish_name);
            // orderitem.setDishPrice(request.dish_price);
            // orderitem.setQuantity(request.quantity);

            // Save to database
            OrderItemDAL d = new OrderItemDAL();
            d.addOrderItem(orderitem);

            return Response.ok(orderitem).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("GetcompanyOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getcompanyOrders(@QueryParam("id") long id) {
        try {
            Service_Order orderService = new Service_Order();

            // orders = (List<Map<String, Object>>) getcompanyOrders(id);
            System.out.println("id: " + id);
            System.out.println(orderService.getRequestById(id));
            return Response.ok(orderService.getRequestById(id)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }

    }

    @GET
    @Path("GetOrdersByCustomerId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByCustomerId(@QueryParam("id") long id) {
        try {
            Service_Order orderService = new Service_Order();
            List<Order> orders = orderService.getorder(id);
            return Response.ok(orders).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}