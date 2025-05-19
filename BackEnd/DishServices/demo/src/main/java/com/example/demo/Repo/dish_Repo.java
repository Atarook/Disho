package com.example.demo.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Dish;

@Repository
public interface dish_Repo extends JpaRepository<Dish, Integer> , JpaSpecificationExecutor<Dish> {
    @Query("SELECT d FROM Dish d WHERE d.sale = true AND d.company_id = :companyId")
  List<Dish> findSaleDishesByCompany(@Param("companyId") Long companyId);
    @Query("select d.amount from Dish d where d.dish_name=?1")
    public int getDishamount(String Name);

    @Query("select count(d) > 0 from Dish d where d.dish_name=?1")
    public boolean existsByName(String Name);

    @Query("select d from Dish d where d.id = ?1")
    public Optional<Dish> findByIdForUpdate(int id);
   @Query("SELECT d FROM Dish d WHERE d.company_id = :companyId")
  List<Dish> findAllByCompany(@Param("companyId") Long companyId);
  @Query("SELECT d FROM Dish d WHERE d.sale = true")
  List<Dish> getsales();

}