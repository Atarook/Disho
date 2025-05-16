package service2.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
            stmt.setObject(5, o.getOrderId());
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

        public OrderItemDAL(){
                try {
            this.conn = DatabaseConnection.getConnection();
            // this.orderItemDAL = new OrderItemDAL(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public List<OrderItem> getcompanyItems(long company_id) {
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

    public List<Order> getcompanyOrder(long company_id) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id AS order_id, o.cost, o.customer_id, o.order_status, " +
                "oi.id AS orderitem_id, oi.quantity, oi.dish_id, oi.dish_name, oi.dish_price, oi.company_id " +
                "FROM `order` o " +
                "JOIN orderitem oi ON o.id = oi.order_id " +
                "WHERE oi.company_id = ? " +
                "ORDER BY o.id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, company_id);
            ResultSet rs = stmt.executeQuery();

            Long currentOrderId = null;
            Order currentOrder = null;
            List<OrderItem> currentOrderItems = null;

            while (rs.next()) {
                Long orderId = rs.getLong("order_id");
                if (!orderId.equals(currentOrderId)) {
                    // Save previous order
                    if (currentOrder != null) {
                        currentOrder.setOrderItems(currentOrderItems);
                        orders.add(currentOrder);
                    }
                    // Start new order
                    currentOrder = new Order();
                    currentOrder.setId(orderId);
                    currentOrder.setCost(rs.getLong("cost"));
                    currentOrder.setCustomerId(rs.getLong("customer_id"));
                    currentOrder.setOrderStatus(rs.getString("order_status"));
                    currentOrderItems = new ArrayList<>();
                    currentOrderId = orderId;
                }
                // Add order item
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("orderitem_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setDishId(rs.getInt("dish_id"));
                item.setDishName(rs.getString("dish_name"));
                item.setDishPrice(rs.getDouble("dish_price"));
                item.company_id = rs.getLong("company_id");
                item.setOrderId(orderId);
                currentOrderItems.add(item);
            }
            // Add the last order
            if (currentOrder != null) {
                currentOrder.setOrderItems(currentOrderItems);
                orders.add(currentOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getAllOrders() {
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

    public String callExternalApi(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

    public List<OrderItem> getOrderItems(long orderId) {
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
                // Set order if needed
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public String getcompanyOrders(long company_id) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT o.id AS order_id, o.cost, o.customer_id, o.order_status, " +
                "oi.id AS orderitem_id, oi.quantity, oi.dish_id, oi.dish_name, oi.dish_price, oi.company_id " +
                "FROM `order` o " +
                "JOIN orderitem oi ON o.id = oi.order_id " +
                "WHERE oi.company_id = ? " +
                "ORDER BY o.id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, company_id);
            ResultSet rs = stmt.executeQuery();

            Long currentOrderId = null;
            Map<String, Object> currentOrderMap = null;
            List<Map<String, Object>> currentOrderItems = null;
            RestTemplate restTemplate = new RestTemplate();

            while (rs.next()) {
                Long orderId = rs.getLong("order_id");
                if (!orderId.equals(currentOrderId)) {
                    // Save previous order
                    if (currentOrderMap != null) {
                        currentOrderMap.put("orderItems", currentOrderItems);
                        result.add(currentOrderMap);
                    }
                    // Start new order
                    currentOrderMap = new HashMap<>();
                    currentOrderMap.put("id", orderId);
                    currentOrderMap.put("cost", rs.getLong("cost"));
                    // currentOrderMap.put("customerId", rs.getLong("customer_id"));
                    // cuscustomer_id = rs.getLong("customer_id");
                    String apiUrl = "http://127.0.0.1:5000/get_company_info?id=" + rs.getLong("company_id");
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    String accApiResponse = restTemplate.getForObject(apiUrl, String.class);
                    try {
                        Map<String, Object> accInfo = mapper.readValue(accApiResponse, Map.class);
                        currentOrderMap.put("Companyinfo", accInfo); // or parse as JSON if needed
                    } catch (Exception e) {
                        currentOrderMap.put("Companyinfo", null);
                    }
                    currentOrderMap.put("orderStatus", rs.getString("order_status"));
                    currentOrderItems = new ArrayList<>();
                    currentOrderId = orderId;

                    // Example: Call external API for customer info
                    String apiUrl2 = "http://127.0.0.1:5000/get_Acc_info?id=" + rs.getLong("customer_id");
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    String apiResponse = restTemplate.getForObject(apiUrl2, String.class);
                    try {
                        // user = mapper.writeValueAsString(apiUrl2);
                        Map<String, Object> customerInfo = mapper.readValue(apiResponse, Map.class);
                        currentOrderMap.put("customerInfo", customerInfo); // or parse as JSON if needed
                    } catch (Exception e) {
                        currentOrderMap.put("customerInfo", null);
                    }
                }
                // Add order item
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("orderitem_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setDishId(rs.getInt("dish_id"));
                item.setDishName(rs.getString("dish_name"));
                item.setDishPrice(rs.getDouble("dish_price"));
                item.company_id = rs.getLong("company_id");
                item.setOrderId(orderId);
                // currentOrderItems.add(item);

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("dishId", item.getDishId());
                itemMap.put("dishName", item.getDishName());
                itemMap.put("dishPrice", item.getDishPrice());
                itemMap.put("company_id", item.company_id);
                itemMap.put("orderId", item.getOrderId());
                currentOrderItems.add(itemMap);
            }
            // Add the last order
            if (currentOrderMap != null) {
                currentOrderMap.put("orderItems", currentOrderItems);
                result.add(currentOrderMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String prettyJson;
        try {
            prettyJson = mapper.writeValueAsString(result);
            return prettyJson;
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        // return result;
    }

}