package com.gridu.store.repository;

import com.gridu.store.model.ProductEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends CrudRepository<ProductEntity, Long> {
}
