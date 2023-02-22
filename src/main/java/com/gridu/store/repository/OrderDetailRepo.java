package com.gridu.store.repository;

import com.gridu.store.model.OrderDetailEntity;
import com.gridu.store.model.OrderEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepo extends CrudRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findAllByOrder(OrderEntity order);
    void deleteAllByOrder(OrderEntity order);
}
