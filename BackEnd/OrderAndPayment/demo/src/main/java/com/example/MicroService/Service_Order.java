package com.example.MicroService;

import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.client.Entity;

import java.util.*;
import com.example.MicroService.Models.Order;
import com.example.MicroService.Models.OrderItem;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import jakarta.*;

@Stateful
public class Service_Order {
    private final static String QUEUE_NAME = "hello";
    @PersistenceContext(unitName = "demo")
    public EntityManager ent;

    public Order order;
    private List<OrderItem> cartItems = new ArrayList<>();

    public Service_Order() {

    }

    // Define factory as a field
    ConnectionFactory factory = new ConnectionFactory();

    // Add a method to send messages
    public void sendMessage(String message) {
        factory.setHost("localhost");
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convert(String Name, Double price, Long id, int quan) {
        OrderItem orderItem = new OrderItem();
        orderItem.setDishId(id);
        orderItem.setDishName(Name);
        orderItem.setDishPrice(price);
        orderItem.setQuantity(quan);
        ent.persist(orderItem);
        cartItems.add(orderItem);
    }

    public void createOrder(Long customerid) {
        Order order = new Order();
        order.setOrderItems(cartItems);
        order.setCustomerId(customerid);
        sendMessage("Order Created");
        order.setOrderStatus("Pending");
    }

    public void SubmitOrder(Order order) {
        long cost = 0;
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            cost += order.getOrderItems().get(i).getDishPrice() * order.getOrderItems().get(i).getQuantity();
        }
        order.setCost(cost);
    }

}