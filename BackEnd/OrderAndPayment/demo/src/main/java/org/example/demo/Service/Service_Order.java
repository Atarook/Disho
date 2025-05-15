package org.example.demo.Service;


import com.rabbitmq.client.*;
import jakarta.ejb.Startup;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.demo.Model.Order;
import org.example.demo.Model.OrderItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
//
//@Stateful
//public class Service_Order {
//    private final static String QUEUE_NAME = "hello";
//    @PersistenceContext(unitName = "demo")
//    public EntityManager ent;
//
//    public Order order;
//    private List<OrderItem> cartItems = new ArrayList<>();
//
//    public Service_Order() {
//
//    }
//    ConnectionFactory factory = new ConnectionFactory();
//    // Define factory as a field
////    public void recieve() throws Exception {
////        factory.setHost("localhost");
////        Connection connection = factory.newConnection();
////        Channel channel = connection.createChannel();
////
////        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
////        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
////        // callback function that handles the logic when msg received
////        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
////            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
////            System.out.println(" [x] Received '" + message + "'");
////        };
////        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
////    }
//    public String receive() {
//        factory.setHost("localhost");
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//
//            // Grab a single message (or null if the queue is empty)
//            GetResponse response = channel.basicGet(QUEUE_NAME, true);
//            if (response != null) {
//                return new String(response.getBody(), StandardCharsets.UTF_8);
//            } else {
//                return "";  // or null, or throw an exception to indicate "no message"
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//
//    // Add a method to send messages
//    public String sendMessage(String msg) {
//        factory.setHost("localhost");
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
//            System.out.println(" [x] Sent '" + msg + "'");
//            return msg;     // return what you sent (or wrap in JSON/POJO if you like)
//        } catch (IOException | TimeoutException e) {
//            e.printStackTrace();
//            return null;    // or throw a WebApplicationException(500) so JAX-RS returns a 500 error
//        }
//    }
//
//

//
//    public void createOrder(long customerid) {
//        Order order = new Order();
//        order.setOrderItems(cartItems);
//        order.setCustomerId(customerid);
//        long cost = 0;
//        for (int i = 0; i < order.getOrderItems().size(); i++) {
//            cost += order.getOrderItems().get(i).getDishPrice() * order.getOrderItems().get(i).getQuantity();
//        }
//        order.setCost(cost);
//        factory.setHost("localhost");
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//            channel.basicPublish("", QUEUE_NAME, null, String.valueOf(cost).getBytes(StandardCharsets.UTF_8));
//            channel.basicPublish("", QUEUE_NAME, null, String.valueOf(customerid).getBytes(StandardCharsets.UTF_8));
//
//               System.out.println(" [x] Sent ' "+customerid+   "co'");
//        } catch (IOException | TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        order.setOrderStatus("Pending");
//    }
//
//    public void SubmitOrder(Order order) {
//
//}
//    }
//



import com.rabbitmq.client.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.demo.Model.Order;
import org.example.demo.Model.OrderItem;

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
    @PersistenceContext(unitName = "demo")
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
//        ent.persist(orderItem);
        cartItems.add(orderItem);
    }
    public List<Order> GetOrders(){
        return ent.createQuery("SELECT o FROM Order o", Order.class)
                .getResultList();
    }

//    public boolean checkCustomerBalance(long customerId, long cost) throws IOException, InterruptedException {
//        String corrId = UUID.randomUUID().toString();
//        // Build JSON payload with timestamp
//        ObjectNode payload = mapper.createObjectNode();
//        payload.put("customerId", customerId);
//        payload.put("cost", cost);
//        payload.put("timestamp", Instant.now().toString());
//        byte[] body = mapper.writeValueAsBytes(payload);
//
//        BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
//        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
//            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
//                responseQueue.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
//            }
//        }, consumerTag -> {});
//
//        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
//                .correlationId(corrId)
//                .replyTo(replyQueueName)
//                .contentType("application/json")
//                .build();
//
//        channel.basicPublish(EXCHANGE, "customer", props, body);
//        String response = responseQueue.take();
//        System.out.println("↪ [DEBUG] raw stock‐check reply = `" + response + "`");
//
//        channel.basicCancel(ctag);
//        boolean ok = Boolean.parseBoolean(response.trim());
//        System.out.println("↪ [DEBUG] parsed OK = " + ok);
//        return ok;
//    }
}
