package com.gridu.store.repository;

import com.gridu.store.model.ProductEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends CrudRepository<ProductEntity, Long> {

    List<ProductEntity> findAll();

    ProductEntity findByTitleAndPrice(String title, Double price);
}
