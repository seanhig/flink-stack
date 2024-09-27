package io.idstudios.flink.kafka.weborder.model.shipdb;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "shipments")
public class Shipment {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer shipment_id;
  private Integer order_id;
  private String origin;
  private String destination;
  private Boolean has_arrived;

  public Integer getShipment_id() {
    return shipment_id;
  }
  public void setShipment_id(Integer shipment_id) {
    this.shipment_id = shipment_id;
  }

  public Integer getOrder_id() {
    return order_id;
  }
  public void setOrder_id(Integer order_id) {
    this.order_id = order_id;
  }

  public String getOrigin() {
    return origin;
  }
  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }
  public void setDestination(String destination) {
    this.destination = destination;
  }

  public Boolean getHas_arrived() {
    return has_arrived;
  }
  public void setHas_arrived(Boolean has_arrived) {
    this.has_arrived = has_arrived;
  }


}
