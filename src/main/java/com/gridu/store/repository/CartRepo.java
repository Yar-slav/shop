package com.gridu.store.repository;

import com.gridu.store.model.UserEntity;
import com.gridu.store.model.CartEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends CrudRepository<CartEntity, Long> {

    Optional<CartEntity> findByUserAndProductId(UserEntity user, Long id);

    List<CartEntity> findAllByUser(UserEntity user);
}
