package com.project.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{

}
