package service2.Services;

// import org.example.demo.Model.Order;
// import org.example.demo.Model.OrderItem;
import service2.Model.*;
import service2.DAL.*;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.BuiltinExchangeType;
// import org.springframework.web.client.RestTemplate;
import com.rabbitmq.client.impl.AMQImpl.Access.Request;

import jakarta.ejb.Startup;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import service2.DAL.OrderDAL;
import service2.DAL.OrderItemDAL;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;   
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.web.client.RestTemplate;

import com.rabbitmq.client.*;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
// import org.example.demo.Model.Order;
// import org.example.demo.Model.OrderItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.nio.charset.StandardCharsets.UTF_8;

@Stateful
@Startup
public class Service_Order {
    private static final String LOG_EXCHANGE = "log_exchange";
    private Connection connection;
    private Channel channel;
    private final ObjectMapper mapper = new ObjectMapper();

    private OrderDAL orderDAL;
    private OrderItemDAL orderItemDAL;
    // @PersistenceContext(unitName = "dishes")
    public EntityManager ent;
    private List<OrderItem> cartItems = new ArrayList<>();

    public Service_Order() throws IOException, TimeoutException {
        try {
            service2.DAL.DatabaseConnection dbConn = new service2.DAL.DatabaseConnection();
            this.orderItemDAL = new service2.DAL.OrderItemDAL(dbConn.getConnection());
            this.orderDAL = new service2.DAL.OrderDAL(dbConn.getConnection());

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(LOG_EXCHANGE, BuiltinExchangeType.TOPIC, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logEvent(String serviceName, String severity, String message) throws IOException {
        String routingKey =  severity;
        ObjectNode logMsg = mapper.createObjectNode();
        logMsg.put("service", serviceName);
        logMsg.put("severity", severity);
        logMsg.put("message", message);
        logMsg.put("timestamp", Instant.now().toString());
        channel.basicPublish(
                LOG_EXCHANGE,
                routingKey,
                null,
                mapper.writeValueAsBytes(logMsg));
    }

    public void convert(String name, Double price, int id, int quan) {
        OrderItem orderItem = new OrderItem();
        orderItem.setDishId(id);
        orderItem.setDishName(name);
        orderItem.setDishPrice(price);
        orderItem.setQuantity(quan);
        // ent.persist(orderItem);
        cartItems.add(orderItem);
    }

    public List<Order> GetOrders() throws SQLException {
        try {
            logEvent("Order", "Info", "Fetching all orders");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return orderDAL.getAllOrders();
        // return ent.createQuery("SELECT o FROM Order o", Order.class)
        // .getResultList();
    }

    public List<OrderItem> getSoldItems(Long Company_id) {
        try {
            logEvent("Order", "Info", "Fetching sold items for company: " + Company_id);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return orderItemDAL.getcompanyItems(Company_id);

        // return ent.createQuery("SELECT o FROM OrderItem o where company_id=?1",
        // OrderItem.class)
        // .setParameter(1, Company_id)
        // .getResultList();
    }

    public String callExternalApi(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        try {
            logEvent("Order", "*_Error", "External API call to " + url + " returned: " + response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public List<Order> getorder(long custid) throws SQLException {
        try {
            logEvent("Order", "Info", "Fetching orders for customer: " + custid);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return orderDAL.getOrdersByCustomerId(custid);
    }

    public String getRequestById(long id) {
        try {
            logEvent("Order", "Info", "Fetching request by ID: " + id);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // String Acc = "http://127.0.0.1:5000/get_Acc_info?id=" + id;
        // String com ="http://127.0.0.1:5000/get_Acc_info?id="
        return orderItemDAL.getcompanyOrders(id);

    }

    // public Order soldDish(long id){

    // }

}
