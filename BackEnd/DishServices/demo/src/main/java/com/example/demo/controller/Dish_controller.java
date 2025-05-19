package com.example.demo.controller;

import com.example.demo.model.Dish;
import com.example.demo.service.dish_services;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:8000", allowCredentials = "true")
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
    public String AddDish(@RequestBody Dish param) throws IOException {
        dishes.AddDish(param);
        return "Dish added successfully";
    }
@PostMapping(value = "/updateDish", consumes = "application/json")
    public String UpdateDish(@RequestBody Dish entity) throws IOException {
        try {
            dishes.UpdateDish(entity);
        } catch (AccessDeniedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Dish updated successfully";
    }
    
    @GetMapping("/get_sales")
  public List<Dish> getSaleDishes(@RequestParam("companyId") Long companyId) {
    return dishes.listSaleDishes(companyId);
  }


  

  @GetMapping("get_dishes")
  public List<Dish> getAllDishes(@RequestParam("companyId") Long companyId) {
    return dishes.listAllDishes(companyId);
  }
   @GetMapping("get_alldishes")
  public List<Dish> getAllDishes() {
    return dishes.listAll();
  }
    @GetMapping("get_allSolddishes")
  public List<Dish> getAllSoldDishes() {
    return dishes.getsales();
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
