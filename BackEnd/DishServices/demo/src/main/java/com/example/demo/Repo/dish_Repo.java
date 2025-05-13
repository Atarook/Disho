package com.example.demo.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Dish;

@Repository
public interface dish_Repo extends JpaRepository<Dish, Integer> , JpaSpecificationExecutor<Dish> {
    @Query("select d from Dish d where d.sale=true")
    public List<Dish> getsalesdishes();
}