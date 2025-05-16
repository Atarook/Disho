// package service2.Services;

// import java.sql.Connection;
// import java.sql.SQLException;
// import java.util.List;

// import jakarta.ejb.Stateless;
// import service2.DAL.DatabaseConnection;
// import service2.DAL.DishDAL;
// import service2.Model.Dish;

// @Stateless
// public class DishService {

//     private DishDAL dishDAL;

//     public DishService() throws SQLException {
//         // You can replace with JNDI lookup or DataSource later
//         Connection conn = DatabaseConnection.getConnection();
//         this.dishDAL = new DishDAL(conn);
//     }

//     public void addDish(Dish dish) throws SQLException {
//         dishDAL.addDish(dish);
//     }

//     public void updateDish(Dish dish) throws SQLException {
//         dishDAL.updateDish(dish);
//     }

//     public void deleteDish(int dishId) throws SQLException {
//         dishDAL.deleteDish(dishId);
//     }

//     public List<Dish> getAllDishesBySeller(int sellerId) throws SQLException {
//         return dishDAL.getAllDishesBySeller(sellerId);
//     }

//     public Dish getDishById(int id) throws SQLException {
//         return dishDAL.GetDishById(id);
//     }

//     public List<Dish> getAllDishes() throws SQLException {
//         return dishDAL.getAllDishes();
//     }
// }