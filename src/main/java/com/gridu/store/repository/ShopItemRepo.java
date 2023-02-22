package com.gridu.store.repository;

import com.gridu.store.model.ShopItemEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopItemRepo extends CrudRepository<ShopItemEntity, Long> {

    Page<ShopItemEntity> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ShopItemEntity findByProductTitleAndProductPrice(String title, double price);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ShopItemEntity> findById(Long id);
}
