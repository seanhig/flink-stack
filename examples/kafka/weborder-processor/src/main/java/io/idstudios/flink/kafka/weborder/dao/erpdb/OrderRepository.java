package io.idstudios.flink.kafka.weborder.dao.erpdb;

import org.springframework.data.jpa.repository.JpaRepository;

import io.idstudios.flink.kafka.weborder.model.erpdb.Order;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface OrderRepository extends JpaRepository<Order, Integer> {

}