package service2.Controller;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service2.Model.OrderItem;
import service2.Services.service_msg;
import java.util.List;

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

    // Endpoint to process an order
    @POST
    @Path("/process")
    public Response processOrder(@QueryParam("customerId") long customerId, List<OrderItem> cartItems) {
        try {
            if (serviceMsg.processOrder(customerId, cartItems))
            return Response.ok("Order processed").build();
            else
            return Response.status(Response.Status.BAD_REQUEST).entity("Order processing failed").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}