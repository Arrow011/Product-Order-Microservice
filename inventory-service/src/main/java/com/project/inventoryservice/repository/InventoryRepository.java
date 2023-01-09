package com.project.inventoryservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.inventoryservice.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory,Long>{

	//Optional<Inventory> findBySkuCode(String skuCode);
	List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
