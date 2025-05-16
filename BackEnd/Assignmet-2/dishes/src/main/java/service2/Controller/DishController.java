// package service2.Controller;


// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.util.List;

// import jakarta.ejb.EJB;
// import jakarta.ws.rs.Consumes;
// import jakarta.ws.rs.DELETE;
// import jakarta.ws.rs.GET;
// import jakarta.ws.rs.POST;
// import jakarta.ws.rs.PUT;
// import jakarta.ws.rs.Path;
// import jakarta.ws.rs.PathParam;
// import jakarta.ws.rs.Produces;
// import jakarta.ws.rs.core.MediaType;
// import service2.Model.Dish;
// import service2.Services.DishService;

// @Path("/dishes")
// @Produces(MediaType.APPLICATION_JSON)
// @Consumes(MediaType.APPLICATION_JSON)
// public class DishController {

//     @EJB
//     private DishService dishService;

//     @POST
//     @Path("/add")
//     public void addDish(Dish dish) throws Exception {
//         Dish d=new Dish();
//         d.setDescription("dawdasd");
//         d.setDishId(1111);
//         d.setDishName("dad");
//         d.setPrice(112);
        
//         d.setStockQuantity(1222);
//         dishService.addDish(d);
//     }
//     @POST
//     @Path("/addDish")
//     public void c()
//     {

//     }

//     @PUT
//     public void updateDish(Dish dish) throws Exception {
//         dishService.updateDish(dish);
//     }

//     @DELETE
//     @Path("/{id}")
//     public void deleteDish(@PathParam("id") int id) throws Exception {
//         dishService.deleteDish(id);
//     }

//     @GET
//     @Path("/seller/{sellerId}")
//     public List<Dish> getDishesBySeller(@PathParam("sellerId") int sellerId) throws Exception {
//         return dishService.getAllDishesBySeller(sellerId);
//     }

//     @GET
//     @Path("/{id}")
//     public Dish getDishById(@PathParam("id") int id) throws Exception {
//         return dishService.getDishById(id);
//     }
//     @GET
//     @Path("/all")
//     public List<Dish> getAllDishes() throws Exception {
//         return dishService.getAllDishes();
//     }
// }