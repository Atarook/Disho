package service2.Controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service2.Controller.OrderController.ConvertRequest;
import service2.Model.OrderItem;
import service2.Services.service_msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

// filepath: C:/Users/ahmed/OneDrive/Desktop/Assignmet-2/dishes/src/main/java/service2/Controller/Order_msg_controller.java

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Order_msg_controller {

    @Inject
    private service_msg serviceMsg;

    // Endpoint to check customer balance
    @POST
    @Path("/check-customer-balance")
    public Response checkCustomerBalance(@QueryParam("customerId") long customerId,
            @QueryParam("cost") float cost) {
        try {
            boolean ok = serviceMsg.checkCustomerBalance(customerId, cost);
            return Response.ok(ok).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("GetcompanyOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getcompanyOrders(long id) {
        try {
            List<Map<String, Object>> orders;
            orders = (List<Map<String, Object>>) getcompanyOrders(id);
            return Response.ok(orders).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }

    }

    // Endpoint to check dish stock
    @POST
    @Path("/check-dish-stock")
    public Response checkDishStock(List<OrderItem> cartItems) {
        try {
            boolean ok = serviceMsg.checkDishStock(cartItems);
            return Response.ok(ok).build();
        } catch (Exception e) {
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
    }

    @POST
    @Path("convert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response convert(List<ConvertRequest> cartItems) {
        try {
            List<OrderItem> orderItem2 = new ArrayList<>();

            // Convert from ConvertRequest to OrderItem format
            for (ConvertRequest item : cartItems) {
                OrderItem orderItem = new OrderItem();

                orderItem.setDishId(item.getDish_id());
                orderItem.setDishName(item.getDish_name());
                orderItem.setDishPrice(item.getDish_price());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setCompany_id(item.getCompany_id());

                // Log the transformation for debugging
                System.out.println("Converted from: " + item.getDish_name() + " to: " + orderItem.getDishName());

                // Optional: Save to database
                // OrderItemDAL d = new OrderItemDAL();
                // d.addOrderItem(orderItem);
            }
            // OrderItem orderItem = new OrderItem();
            // orderItem.setId(cartItems.get(0).getId());
            // orderItem.setCompany_id(cartItems.company_id);
            // orderItem.setDishId(cartItems.dish_id);
            // orderItem.setDishName(cartItems.dish_name);
            // orderItem.setDishPrice(cartItems.dish_price);
            // orderItem.setQuantity(cartItems.quantity);

            // // Log the transformation for debugging
            // System.out.println("Converted from: " + cartItems.dish_name + " to: " + orderItem.getDishName());

            // Optional: Save to database
            // OrderItemDAL d = new OrderItemDAL();
            // d.addOrderItem(orderItem);

            // Return the OrderItem object which will be serialized to JSON
            return Response.ok(orderItem2).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    // Endpoint to process an order
    @POST
    @Path("/process")
    public Response processOrder(
            @QueryParam("customerId") long customerId,
            List<OrderItem> cartItems,
            @QueryParam("companyId") long companyId) {
        try {
            // convert(cartItems);
            System.out.println("Processing order for customer ID: " + customerId);
            Map<String, Object> result = serviceMsg.processOrder(customerId, cartItems, companyId);
            String status = (String) result.get("status");
            if ("success".equals(status)) {
                return Response.ok(result).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(
                    Map.of("status", "error", "message", e.getMessage())).build();
        }
    }
}
