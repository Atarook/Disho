package com.example.demo.service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.example.demo.Repo.dish_Repo;
import com.example.demo.model.Dish;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class dish_services {
    private final dish_Repo dishRepo;

    public dish_services(dish_Repo dishRepo) {
        this.dishRepo = dishRepo;
    }

    private final static String QUEUE_NAME = "hello";
    private static final String LOG_EXCHANGE = "log_exchange";
    private Connection connection;
    private Channel channel;
    private static final ObjectMapper mapper = new ObjectMapper();

    // Add to constructor or create a @PostConstruct method
    @jakarta.annotation.PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        // Declare the logging exchange
        channel.exchangeDeclare(LOG_EXCHANGE, BuiltinExchangeType.TOPIC, true);
    }

    public void AddDish(Dish dish) throws IOException {
try{
        dishRepo.save(dish);
    }
        catch(Exception e) {
    logEvent("Dish", "*_Error", "Dish is not Add");

        }
    }

    public int getDishamount(String Name) {
        int amount = dishRepo.getDishamount(Name);
        // ConnectionFactory factory = new ConnectionFactory();
        // factory.setHost("localhost"); // hostname
        // try (Connection connection = factory.newConnection();
        // Channel channel = connection.createChannel()) {
        // channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // String message = amount + " " + Name;
        // channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        // System.out.println(" [x] Sent '" + message + "'");
        // }
        return amount;
    }

    public boolean checkDish(String Name) {
        return dishRepo.existsByName(Name);
    }

    public void performquantatycheck() {

    }

    public List<Dish> getsales(){
        return dishRepo.getsales();
    }

    public Dish getDishById(int id) {
        return dishRepo.findById(id).orElse(null);
    }

    // @RabbitListener(queues = "rpc.check_stock")
    // @Transactional
    // public String checkStock(@Payload String body) {
    // // parse JSON (you can use Jackson)
    // ObjectMapper mapper = new ObjectMapper();
    // Dish dish = new Dish();
    // long dishId;
    // int qty=0;
    // boolean flag=false;
    // try {
    // System.out.println(" yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
    // JsonNode node = mapper.readTree(body);
    // JsonNode items=node.get("items");
    // System.out.println(" [x] Sent '" + items + "'");
    // for (JsonNode item : items) {
    // dishId = item.get("dishId").asLong();
    // qty = item.get("quantity").asInt();
    // System.out.println(" [x] Sent '" + dishId + "'");
    // System.out.println(" [x] Sent '" + qty + "'");
    // dish = dishRepo.findById((int) dishId).orElse(null);
    // if (dish.getAmount() >= qty ) {
    // dish.setAmount(dish.getAmount() - qty);
    // System.out.println(" [x] Sent '" + dish.getAmount() + "'");
    // // no need to call save(); within a @Transactional context JPA will flush
    // } else {
    // System.out.println(" [x] Sent '" + dish.getAmount() + "'");
    // flag=true;
    // // return "FAIL:OutOfStock";

    // }
    // }
    // System.out.println(" [x] Sent '" + flag + "'");
    // if (flag) {
    // return "false";
    // }
    // else {

    // System.out.println(" [x] Sent '" + dish.getAmount() + "'");
    // return "true";
    // }
    // }

    // // List<Dish> dish = mapper.convertValue(items,
    // mapper.getTypeFactory().constructCollectionType(List.class, Dish.class));
    // // System.out.println(" 11111");

    // // dish = node.get("items").traverse(mapper).readValueAs(Dish.class);
    // // System.out.println(" 22222");
    // // for (Dish d : dish) {
    // // System.out.println(" 33333");
    // // System.out.println(d.getId());
    // // System.out.println(d.getAmount());
    // // }
    // // dishId = items.get(0).getId();
    // // qty = items.get(0).getAmount();
    // catch (Exception e) {
    // System.out.println(" xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

    // return "false";
    // }

    @RabbitListener(queues = "rpc.check_stock")
    @SendTo // ← without this, your "boolean" just vanishes
    @Transactional
    public String checkStock(@Payload String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        boolean outOfStock = false;

        try {
            JsonNode items = mapper.readTree(body).get("items");
            for (JsonNode item : items) {
                System.out.println(" [x] Sent '" + items + "'");
                long dishId = item.get("dishId").asLong();
                int qty = item.get("quantity").asInt();
                Dish d = dishRepo.findById((int) dishId).orElseThrow();
                if (d.getAmount() <= qty) {
                    outOfStock = true;
                    break;
                }
                d.setAmount(d.getAmount() - qty);
            }
            System.out.println(" [x] Sent '" + outOfStock + "'");
        } catch (Exception ex) {
            logEvent("Dish", "*_Error", "LOG_EXCHANGE: Error in checkStock");
            outOfStock = true;
        }

        // Return "true" or "false" as a String (so it will go back to the replyTo
        // queue)
        return outOfStock ? "true" : "false";
    }

    // @RabbitListener(queues = "rpc.check_stock")
    // @SendTo // ← without this, your "boolean" just vanishes
    // @Transactional
    // public String reduceStock(@Payload String body) {
    // ObjectMapper mapper = new ObjectMapper();
    // boolean outOfStock = false;

    // try {
    // JsonNode items = mapper.readTree(body).get("items");
    // for (JsonNode item : items) {
    // long dishId = item.get("dishId").asLong();
    // int qty = item.get("quantity").asInt();
    // Dish d = dishRepo.findById((int)dishId).orElseThrow();
    // if (d.getAmount() <= qty) {
    // outOfStock = true;
    // break;
    // }
    // d.setAmount(d.getAmount() - qty);
    // }
    // } catch (Exception ex) {
    // outOfStock = true;
    // }

    // // Return "true" or "false" as a String (so it will go back to the replyTo
    // queue)
    // return outOfStock ? "true" : "false";
    // }

    // return dishRepo.findByIdForUpdate(dishId)
    // .map(dish -> {
    // if (dish.getAmount() >= qty && dish.isSale()) {
    // dish.setAmount(dish.getAmount() - qty);
    // System.out.println(" cccccccccccccccccccccccccccccccccccccc");

    // System.out.println(" [x] Sent '" + dish.getAmount() + "'");
    // // no need to call save(); within a @Transactional context JPA will flush
    // return "OK";
    // } else {
    // System.out.println(" cccccccccccccccccccccccccccccccccccccc");
    // System.out.println(" [x] Sent '" + dish.getAmount() + "'");

    // return "FAIL:OutOfStock";
    // }
    // })
    // .orElse("FAIL:NoSuchDish");

@Transactional
public void     UpdateDish(Dish incoming) throws IOException {
    // 1) Load (and lock) the existing dish
    Dish existing = dishRepo.findByIdForUpdate(incoming.getId())
        .orElseThrow(() -> new EntityNotFoundException(
            "Dish not found with id " + incoming.getId())
        );

    // 2) Security check: verify the dish belongs to the company attempting to update it
    if (!existing.getCompany_id().equals(incoming.getCompany_id())) {
        logEvent("Dish", "*_Error", "LOG_EXCHANGE: You can only update dishes that belong to your company");
        throw new AccessDeniedException("You can only update dishes that belong to your company");
    }

    // 3) Mutate only the fields that should change
    // Note: We're not updating dish_name from the incoming request since it's not in the form
    // existing.setDish_name(incoming.getDish_name());
    existing.setPrice(incoming.getPrice());
    existing.setAmount(incoming.getAmount());
    existing.setSale(incoming.isSale());

    // 4) No explicit save() needed — JPA will flush the dirty fields at commit.
}


     public List<Dish> listSaleDishes(Long companyId) {
    return dishRepo.findSaleDishesByCompany(companyId);
  }

  public List<Dish> listAllDishes(Long companyId) {
    return dishRepo.findAllByCompany(companyId);
  }
public List<Dish> listAll() {
    return dishRepo.findAll();
  }

// Add the logEvent method
private void logEvent(String serviceName, String severity, String message) throws IOException {
    String routingKey = severity;
    ObjectNode logMsg = mapper.createObjectNode();
    logMsg.put("service", serviceName);
    logMsg.put("severity", severity);
    logMsg.put("message", message);
    logMsg.put("timestamp", Instant.now().toString());
    channel.basicPublish(
        LOG_EXCHANGE,
        routingKey,
        null,
        mapper.writeValueAsBytes(logMsg)
    );
}

// Add cleanup method
@jakarta.annotation.PreDestroy
public void teardown() throws IOException, TimeoutException {
    if (channel != null && channel.isOpen()) {
        channel.close();
    }
    if (connection != null && connection.isOpen()) {
        connection.close();
    }
}
}
