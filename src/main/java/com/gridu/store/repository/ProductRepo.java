package com.gridu.store.repository;

import com.gridu.store.model.ProductEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends CrudRepository<ProductEntity, Long> {

    Page<ProductEntity> findAll(Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProductEntity> findById(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductEntity findByTitleAndPrice(String title, double price);
}
