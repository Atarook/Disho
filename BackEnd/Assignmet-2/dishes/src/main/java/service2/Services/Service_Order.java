package service2.Services;

// import org.example.demo.Model.Order;
// import org.example.demo.Model.OrderItem;
import service2.Model.*;
import service2.DAL.*;
import com.rabbitmq.client.*;
import jakarta.ejb.Startup;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import service2.DAL.OrderDAL;
import service2.DAL.OrderItemDAL;
// import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

import static java.nio.charset.StandardCharsets.UTF_8;

@Stateful
@Startup
public class Service_Order {
    private OrderDAL orderDAL;
    private OrderItemDAL orderItemDAL;
    // @PersistenceContext(unitName = "dishes")
    public EntityManager ent;
    private List<OrderItem> cartItems = new ArrayList<>();

    public Service_Order() throws IOException, TimeoutException {
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

        return orderDAL.getAllOrders();
        // return ent.createQuery("SELECT o FROM Order o", Order.class)
        // .getResultList();
    }

    public List<OrderItem> getSoldItems(Long Company_id) {

        return orderItemDAL.getcompanyItems(Company_id);

        // return ent.createQuery("SELECT o FROM OrderItem o where company_id=?1",
        // OrderItem.class)
        // .setParameter(1, Company_id)
        // .getResultList();
    }
    public String callExternalApi(String url) {
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.getForObject(url, String.class);
    return response;
}
  
    // public Order soldDish(long id){
        

    // }


















}
