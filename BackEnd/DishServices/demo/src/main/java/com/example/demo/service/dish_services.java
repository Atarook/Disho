package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.repo.dish_Repo;
import com.example.demo.model.Dish;

@Service
public class dish_services {
    private final dish_Repo dishRepo;

    public dish_services(dish_Repo dishRepo) {
        this.dishRepo = dishRepo;
    }

    public void AddDish(Dish dish) {
        dishRepo.save(dish);
    }

    public void UpdateDish(Dish dish) {
        dishRepo.save(dish);
    }

    public List<Dish> listAllSaleDishs() {
        return dishRepo.getsalesdishes();
    }
}
