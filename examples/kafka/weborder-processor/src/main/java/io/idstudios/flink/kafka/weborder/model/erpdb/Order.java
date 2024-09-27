package io.idstudios.flink.kafka.weborder.model.erpdb;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity 
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer order_id;
  private String order_ref;
  private String customer_name;
  private LocalDateTime order_date;
  private Double order_total;
  private Integer order_qty;
  private Integer product_id;
  private Integer order_status;


  public Integer getOrder_id() {
    return order_id;
  }
  public void setOrder_id(Integer order_id) {
    this.order_id = order_id;
  }
  public String getOrder_ref() {
    return order_ref;
  }
  public void setOrder_ref(String order_ref) {
    this.order_ref = order_ref;
  }
  public String getCustomer_name() {
    return customer_name;
  }
  public void setCustomer_name(String customer_name) {
    this.customer_name = customer_name;
  }
  public LocalDateTime getOrder_date() {
    return order_date;
  }
  public void setOrder_date(LocalDateTime order_date) {
    this.order_date = order_date;
  }
  public Double getOrder_total() {
    return order_total;
  }
  public void setOrder_total(Double order_total) {
    this.order_total = order_total;
  }
  public Integer getOrder_qty() {
    return order_qty;
  }
  public void setOrder_qty(Integer order_qty) {
    this.order_qty = order_qty;
  }
  public Integer getProduct_id() {
    return product_id;
  }
  public void setProduct_id(Integer product_id) {
    this.product_id = product_id;
  }
  public Integer getOrder_status() {
    return order_status;
  }
  public void setOrder_status(Integer order_status) {
    this.order_status = order_status;
  }


}