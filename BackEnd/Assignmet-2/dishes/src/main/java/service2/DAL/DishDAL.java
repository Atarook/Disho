// package service2.DAL;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;

// import service2.Model.Dish;

// public class DishDAL {
//     private Connection conn;

//     public DishDAL(Connection conn) {
//         this.conn = conn;
//     }

//     public void addDish(Dish dish) throws SQLException {
//         String sql = "INSERT INTO dish ( dish_name, description, price, stock_quantity) VALUES ( ?, ?, ?, ?)";
//         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//             stmt.setString(2, dish.getDishName());
//             stmt.setString(3, dish.getDescription());
//             stmt.setDouble(4, dish.getPrice());
//             stmt.setInt(5, dish.getStockQuantity());
//             stmt.executeUpdate();
//         }
//     }

//     public void updateDish(Dish dish) throws SQLException {
//         String sql = "UPDATE dish SET dish_name = ?, description = ?, price = ?, stock_quantity = ? WHERE dish_id = ?";
//         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//             stmt.setString(1, dish.getDishName());
//             stmt.setString(2, dish.getDescription());
//             stmt.setDouble(3, dish.getPrice());
//             stmt.setInt(4, dish.getStockQuantity());
//             stmt.setInt(5, dish.getDishId());
//             stmt.executeUpdate();
//         }
//     }

//     public void deleteDish(int dishId) throws SQLException {
//         String sql = "DELETE FROM dish WHERE dish_id = ?";
//         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//             stmt.setInt(1, dishId);
//             stmt.executeUpdate();
//         }
//     }

//     public List<Dish> getAllDishesBySeller(int sellerId) throws SQLException {
//         List<Dish> dishes = new ArrayList<>();
//         String sql = "SELECT * FROM dish WHERE seller_id = ?";
//         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//             stmt.setInt(1, sellerId);
//             ResultSet rs = stmt.executeQuery();
//             while (rs.next()) {
//                 Dish dish = new Dish();
//                 dish.setDishId(rs.getInt("dish_id"));
//                 dish.setSellerId(rs.getInt("seller_id"));
//                 dish.setDishName(rs.getString("dish_name"));
//                 dish.setDescription(rs.getString("description"));
//                 dish.setPrice(rs.getDouble("price"));
//                 dish.setStockQuantity(rs.getInt("stock_quantity"));
//                 dishes.add(dish);
//             }
//         }
//         return dishes;
//     }
//     public Dish GetDishById(int Id) throws SQLException{
//         String sql = "SELECT * FROM dish WHERE dish_id = ?";
//         Dish dish = new Dish();
//         try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//             stmt.setInt(1, Id);
//             ResultSet rs = stmt.executeQuery();   
//             if (rs.next()) {
//                 dish.setDishId(rs.getInt("dish_id"));
//                 dish.setSellerId(rs.getInt("seller_id"));
//                 dish.setDishName(rs.getString("dish_name"));
//                 dish.setDescription(rs.getString("description"));
//                 dish.setPrice(rs.getDouble("price"));
//                 dish.setStockQuantity(rs.getInt("stock_quantity"));
//             }
//         }
//         return dish;
//     }

//     public List<Dish> getAllDishes() throws SQLException {
//         List<Dish> dishes = new ArrayList<>();
//         String sql = "SELECT * FROM dish";
//         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//             ResultSet rs = stmt.executeQuery();
//             while (rs.next()) {
//                 Dish dish = new Dish();
//                 dish.setDishId(rs.getInt("dish_id"));
//                 dish.setSellerId(rs.getInt("seller_id"));
//                 dish.setDishName(rs.getString("dish_name"));
//                 dish.setDescription(rs.getString("description"));
//                 dish.setPrice(rs.getDouble("price"));
//                 dish.setStockQuantity(rs.getInt("stock_quantity"));
//                 dishes.add(dish);
//             }
//         }
//         return dishes;
//     }
// }
