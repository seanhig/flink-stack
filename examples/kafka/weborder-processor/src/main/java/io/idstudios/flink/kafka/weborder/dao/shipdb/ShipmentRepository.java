package io.idstudios.flink.kafka.weborder.dao.shipdb;

import org.springframework.data.jpa.repository.JpaRepository;

import io.idstudios.flink.kafka.weborder.model.shipdb.Shipment;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

}