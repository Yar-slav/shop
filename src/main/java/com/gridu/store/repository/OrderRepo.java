package com.gridu.store.repository;

import com.gridu.store.model.OrderEntity;
import com.gridu.store.model.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends CrudRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByUser(UserEntity entity);
    Optional<OrderEntity> findByIdAndUser(Long id, UserEntity user);

}
