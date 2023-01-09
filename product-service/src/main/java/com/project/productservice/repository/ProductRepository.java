package com.project.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.project.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product,String>{
}
