package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Dish;
import com.example.demo.service.dish_services;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/dish")
public class Dish_controller {
    private final dish_services dishes;
    public Dish_controller(dish_services dish_services){
        this.dishes=dish_services;
    }

    @GetMapping("/getdish")
    public int getDishamount(@RequestParam String Name) throws Exception {
        return dishes.getDishamount(Name);
    }

@PostMapping(value = "/adddish", consumes = "application/json")
    public String AddDish(@RequestBody Dish param) {
        dishes.AddDish(param);
        return "Dish added successfully";
    }
@PostMapping(value = "/updateDish", consumes = "application/json")
    public String UpdateDish(@RequestBody Dish entity) {
        dishes.UpdateDish(entity);
        return "Dish updated successfully";
    }
    
    @GetMapping("/getallsales")
    public List<Dish> getsalesdishes() {
        return dishes.listAllSaleDishs();
    }
    @GetMapping("/test")
    public String getdish() {
        return "test";
    }
    
    @PostMapping("/test-json")
    public String testJson(@RequestBody Map<String, Object> payload) {
        return "Received JSON: " + payload;
    }
}
