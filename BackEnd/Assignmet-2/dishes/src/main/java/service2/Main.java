// package service2;

// import java.sql.Connection;

// import service2.DAL.DatabaseConnection;
// import service2.DAL.DishDAL;
// import service2.Model.Dish;

// public class Main {
//    public static void main(String[] args) {
//         try (Connection conn = DatabaseConnection.getConnection()) {
//             DishDAL dishDAL = new DishDAL(conn);

//             Dish newDish = new Dish();
//             newDish.setSellerId(1);
//             newDish.setDishName("Stuffed Grape Leaves");
//             newDish.setDescription("Middle Eastern appetizer.");
//             newDish.setPrice(8.50);
//             newDish.setStockQuantity(20);

//             dishDAL.addDish(newDish);
//             System.out.println("Dish inserted successfully.");

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }