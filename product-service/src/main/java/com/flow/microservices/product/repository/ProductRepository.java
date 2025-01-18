package com.flow.microservices.product.repository;

import com.flow.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

//product is the entity class. the string is the id type.
public interface ProductRepository extends MongoRepository<Product, String> {
}
