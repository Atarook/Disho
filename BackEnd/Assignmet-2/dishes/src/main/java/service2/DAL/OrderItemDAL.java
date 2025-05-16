package service2.DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
 import service2.Model.Order;

import service2.Model.OrderItem;


public class OrderItemDAL {
    private Connection conn;

    public OrderItemDAL(Connection conn) {
        this.conn = conn;
    }

    public void addOrderItem(OrderItem o) throws SQLException {
    String sql = "INSERT INTO orderitem (quantity, dish_id, dish_name, dish_price, order_id, company_id) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, o.getQuantity());
        stmt.setInt(2, o.getDishId());
        stmt.setString(3, o.getDishName());
        stmt.setDouble(4, o.getDishPrice());
        stmt.setObject(5, o.getOrderId() );
        stmt.setObject(6, o.company_id, java.sql.Types.BIGINT);
        stmt.executeUpdate();
    }
}

public List<OrderItem> getOrderItemsByOrderId(long orderId) throws SQLException {
    List<OrderItem> items = new ArrayList<>();
    String sql = "SELECT * FROM orderitem WHERE order_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, orderId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            OrderItem item = new OrderItem();
            item.setId(rs.getLong("id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setDishId(rs.getInt("dish_id"));
            item.setDishName(rs.getString("dish_name"));
            item.setDishPrice(rs.getDouble("dish_price"));
            item.company_id = rs.getLong("company_id");
            item.setOrderId(rs.getLong("order_id"));
            items.add(item);
        }
    }
    return items;
}

public OrderItem getOrderItemById(int id) throws SQLException {
    String sql = "SELECT * FROM orderitem WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            OrderItem item = new OrderItem();
            item.setId(rs.getLong("id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setDishId(rs.getInt("dish_id"));
            item.setDishName(rs.getString("dish_name"));
            item.setDishPrice(rs.getDouble("dish_price"));
            item.company_id = rs.getLong("company_id");
            // Set order if needed
            return item;
        }
    }
    return null;
}

public void updateOrderItem(OrderItem o) throws SQLException {
    String sql = "UPDATE orderitem SET quantity = ?, dish_id = ?, dish_name = ?, dish_price = ?, order_id = ?, company_id = ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, o.getQuantity());
        stmt.setInt(2, o.getDishId());
        stmt.setString(3, o.getDishName());
        stmt.setDouble(4, o.getDishPrice());
        stmt.setObject(5, o.getOrder() != null ? o.getOrder().getId() : null, java.sql.Types.BIGINT);
        stmt.setObject(6, o.company_id, java.sql.Types.BIGINT);
        stmt.setLong(7, o.getId());
        stmt.executeUpdate();
}
}

public List<OrderItem> getcompanyItems (long company_id){
    List<OrderItem> items = new ArrayList<>();
    String sql = "SELECT * FROM orderitem WHERE company_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, company_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            OrderItem item = new OrderItem();
            item.setId(rs.getLong("id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setDishId(rs.getInt("dish_id"));
            item.setDishName(rs.getString("dish_name"));
            item.setDishPrice(rs.getDouble("dish_price"));
            item.company_id = rs.getLong("company_id");
            // Set order if needed
            items.add(item);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return items;
}

public List<Order>getcompanyOrders(long company_id){
    List<Order> orders = new ArrayList<>();
    List<OrderItem> orderitems = new ArrayList<>();
    String sql = "SELECT * FROM `orderitem` WHERE company_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, company_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            OrderItem orderitem = new OrderItem();
            orderitem.setId(rs.getLong("id"));
            orderitem.setQuantity(rs.getInt("quantity"));   
            orderitem.setDishId(rs.getInt("dish_id"));
            orderitem.setDishName(rs.getString("dish_name"));
            orderitem.setDishPrice(rs.getDouble("dish_price"));
            orderitem.company_id = rs.getLong("company_id");
            // Set order if needed
            // orderId= orderitem.getOrderId();
            orderitems.add(orderitem);
            
            }

            Order order = new Order();
            String sql2= "SELECT * FROM `order` WHERE id = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                for (OrderItem orderItem : orderitems) {
                    orderItem.setOrderId(order.getId());
                }
                stmt2.setLong(1, orderitems.get(0).getOrderId());
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    order.setId(rs2.getLong("id"));
                    order.setCost(rs2.getLong("cost"));
                    order.setCustomerId(rs2.getLong("customer_id"));
                    order.setOrderStatus(rs2.getString("order_status"));
                    order.setOrderItems(orderitems);
                    orders.add(order);
                }
                return orders;
        }
        // while (rs.next()) {
        //     Order order = new Order();
        //     order.setId(rs.getLong("id"));
        //     order.setCost(rs.getLong("cost"));
        //     order.setCustomerId(rs.getLong("customer_id"));
        //     order.setOrderStatus(rs.getString("order_status"));
        //     orders.add(order);
        // }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return orders;
}

public List<Order> getAllOrders(){
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM `order`";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setCost(rs.getLong("cost"));
            order.setCustomerId(rs.getLong("customer_id"));
            order.setOrderStatus(rs.getString("order_status"));
            orders.add(order);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return orders;
}

}