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

@Singleton
@Startup
public class Service_Order {
    @PersistenceContext(unitName = "demo")
    public EntityManager ent;

    private List<OrderItem> cartItems = new ArrayList<>();
    private static final String CUSTOMER_QUEUE = "customer_request_queue";
    private static final String DISH_QUEUE = "rpc.check_stock";
    private static final String EXCHANGE = "order_exchange";

    private Connection connection;
    private Channel channel;
    private String replyQueueName;
    private static final ObjectMapper mapper = new ObjectMapper();


    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        // Declare a direct exchange for routing
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, true);

        // Declare service request queues
        channel.queueDeclare(CUSTOMER_QUEUE, true, false, false, null);
        channel.queueBind(CUSTOMER_QUEUE, EXCHANGE, "customer");
        channel.queueDeclare(DISH_QUEUE, true, false, false, null);
        channel.queueBind(DISH_QUEUE, EXCHANGE, "dish");

        // Create a private reply-to queue for this service
//        replyQueueName = channel.queueDeclare().getQueue();
        replyQueueName = channel.queueDeclare(
                "",     // ask the server for a name
                false,  // not durable
                true,   // exclusive to this connection
                false,  // <--- autoDelete = false
                null
        ).getQueue();
    }


    public Service_Order() throws IOException, TimeoutException {
        init();
    }

    @PreDestroy
    public void teardown() throws IOException, TimeoutException {
        if (channel != null && channel.isOpen()) channel.close();
        if (connection != null && connection.isOpen()) connection.close();
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
public boolean checkCustomerBalance(long customerId, float cost)
        throws IOException, InterruptedException {

    String corrId = UUID.randomUUID().toString();

    // 1) Build JSON request
    ObjectNode payload = mapper.createObjectNode()
            .put("customerId", customerId)
            .put("cost", cost)
            .put("deductCost", "true")

            .put("timestamp", Instant.now().toString());
    byte[] body = mapper.writeValueAsBytes(payload);

    // 2) Prepare a temporary reply queue
    //    (created once in @PostConstruct as 'replyQueueName')
    BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);

    String consumerTag = channel.basicConsume(
            replyQueueName,         // the auto‑delete, exclusive reply queue
            true,                   // autoAck
            (ct, delivery) -> {
                AMQP.BasicProperties props = delivery.getProperties();
                if (corrId.equals(props.getCorrelationId())) {
                    responseQueue.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                }
            },
            ct -> {}
    );

    // 3) Publish the request
    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .contentType("application/json")
            .build();

    channel.basicPublish(
            EXCHANGE,      // "order_exchange"
            "customer",    // routing key
            props,
            body
    );

    // 4) Block until we get a reply
    String reply = responseQueue.poll(20,TimeUnit.SECONDS);
    // “true” or “false”
    channel.basicCancel(consumerTag);

    boolean ok = Boolean.parseBoolean(reply);
    System.out.println("↪ CUSTOMEEER[CustomerBalance] reply=" + reply + ", ok=" + ok);
    return ok;
}


    public boolean checkDishStock() throws IOException, InterruptedException {
        String corrId = UUID.randomUUID().toString();
        // Build JSON payload with order items and timestamp
        ObjectNode payload = mapper.createObjectNode();
        payload.putPOJO("items", cartItems);

        payload.put("timestamp", Instant.now().toString());
        byte[] body = mapper.writeValueAsBytes(payload);

        BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                responseQueue.offer(new String(delivery.getBody(), UTF_8));
            }
        }, consumerTag -> {});

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .contentType("application/json")
                .build();

        channel.basicPublish(EXCHANGE, "dish", props, body);
        String response = responseQueue.poll(20, TimeUnit.SECONDS);
        System.out.println("↪ [DEBUG] raw stock‐check reply = `" + response + "`");
        if (response == null) {
            System.out.println("↪ [DEBUG] Stock-check timed out after 1 minute");
            return true;
        }
//        channel.basicCancel(ctag);
        boolean ok = Boolean.parseBoolean(response.trim());
        System.out.println(" DISHSHSH↪ [DEBUG] parsed OK = " + ok);
        return ok;
//        return "YES".equalsIgnoreCase(response);
//        return Boolean.parseBoolean(response);
    }

    public void processOrder(long customerId) {
        try {
            init();
            convert("dish1",12.0,2,2);
            // Persist basic order info
            Order order = new Order();
            order.setOrderItems(cartItems);
            order.setCustomerId(customerId);
            long cost = 0;
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            cost += order.getOrderItems().get(i).getDishPrice() * order.getOrderItems().get(i).getQuantity();
        }
        order.setCost(cost);
            order.setOrderStatus("Pending");
//            ent.persist(order);

            // Step 1: Verify customer balance
            if (!checkCustomerBalance(customerId, cost)) {
                System.out.println("Insufficient funds for customer: " + customerId);
                return;
            }
                System.out.println("hees ya3m: " + customerId);


            // Step 2: Verify dish stock
            if (checkDishStock()) {//

//                System.out.println(checkDishStock());
                ObjectNode commitCust = mapper.createObjectNode();
                commitCust.put("customerId", customerId);
                commitCust.put("cost", cost);
                commitCust.put("deductCost", "false");
                commitCust.put("timestamp", Instant.now().toString());
                channel.basicPublish(EXCHANGE, "customer", null, mapper.writeValueAsBytes(commitCust));
                System.out.println("InsuffX icient stock for order items");
                return;
            }
                else{
                System.out.println("mafrood hena ");

            }


            // Step 3: Deduct cost and stock (commit)
//            ObjectNode commitCust = mapper.createObjectNode();
//            commitCust.put("customerId", customerId);
//            commitCust.put("deductCost", "false");
//            commitCust.put("timestamp", Instant.now().toString());
//            channel.basicPublish(EXCHANGE, "customer", null, mapper.writeValueAsBytes(commitCust));

//            ObjectNode commitDish = mapper.createObjectNode();
//            commitDish.putPOJO("items", cartItems);
//            commitDish.put("deductStock", true);
//            commitDish.put("timestamp", Instant.now().toString());
//            channel.basicPublish(EXCHANGE, "dish", null, mapper.writeValueAsBytes(commitDish));

            System.out.println("Order processed and committed for customer " + customerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
