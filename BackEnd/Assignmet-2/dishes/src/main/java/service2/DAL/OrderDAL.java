package service2.DAL;

import java.sql.*;
import java.util.List;
import service2.Model.Order;
import service2.Model.OrderItem;

public class OrderDAL {

    private Connection conn;
    private OrderItemDAL orderItemDAL;

    public OrderDAL(Connection conn) {
        this.conn = conn;
    }

    public OrderDAL() {
        try {
            this.conn = DatabaseConnection.getConnection();
            // this.orderItemDAL = new OrderItemDAL(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO `Order` (cost, customer_id, order_status,companyId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setFloat(1, order.getCost());
            orderStmt.setLong(2, order.getCustomerId());
            orderStmt.setString(3, order.getOrderStatus());
            orderStmt.setLong(4, order.getCompany_id());
            orderStmt.executeUpdate();

            // Get generated order ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            Long orderId = null;
            if (rs.next()) {
                orderId = rs.getLong(1);
                order.setId(orderId); // <-- FIX: Set the generated ID on the order object

            }

            // Insert order items
            if (orderId != null && order.getOrderItems() != null) {
                OrderItemDAL orderCon = new OrderItemDAL(conn);
                for (OrderItem item : order.getOrderItems()) {
                    item.setOrder(order);
                    item.setOrderId(orderId);
                    orderCon.addOrderItem(item);
                }
            }
        }

    }

    public Order getOrderById(Long custid) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, custid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getLong("id"));
                    order.setCost(rs.getLong("cost"));
                    order.setCustomerId(rs.getLong("customer_id"));
                    order.setOrderStatus(rs.getString("order_status"));
                    // Optionally load order items here if needed
                    return order;
                }
            }
        }
        return null;
    }

    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT * FROM `Order`";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            List<Order> orders = new java.util.ArrayList<>();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setCost(rs.getLong("cost"));
                order.setCustomerId(rs.getLong("customer_id"));
                order.setOrderStatus(rs.getString("order_status"));
                orders.add(order);
            }
            return orders;
        }
    }

    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE `Order` SET cost = ?, customer_id = ?, order_status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, order.getCost());
            stmt.setLong(2, order.getCustomerId());
            stmt.setString(3, order.getOrderStatus());
            stmt.setLong(4, order.getId());
            stmt.executeUpdate();
        }
    }

   public void deleteOrder(Long orderId) throws SQLException {
    // First delete order items
    String deleteItemsSql = "DELETE FROM orderitem WHERE order_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
        stmt.setLong(1, orderId);
        stmt.executeUpdate();
    }
    // Then delete the order
    String deleteOrderSql = "DELETE FROM `Order` WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(deleteOrderSql)) {
        stmt.setLong(1, orderId);
        stmt.executeUpdate();
    }
}

    public List<Order> getOrdersByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE customer_id = ?";
        OrderItemDAL orderItemDAL = new OrderItemDAL();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Order> orders = new java.util.ArrayList<>();
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getLong("id"));
                    order.setCost(rs.getLong("cost"));
                    order.setCustomerId(rs.getLong("customer_id"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setOrderItems(orderItemDAL.getOrderItemsByOrderId(order.getId()));
                    orders.add(order);
                }
                return orders;
            }
        }
    }
}
