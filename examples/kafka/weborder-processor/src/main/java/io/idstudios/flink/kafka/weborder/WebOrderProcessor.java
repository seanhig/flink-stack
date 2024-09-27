package io.idstudios.flink.kafka.weborder;

import org.springframework.messaging.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import io.idstudios.flink.kafka.weborder.dao.erpdb.OrderRepository;
import io.idstudios.flink.kafka.weborder.dao.erpdb.ProductRepository;
import io.idstudios.flink.kafka.weborder.dao.shipdb.ShipmentRepository;
import io.idstudios.flink.kafka.weborder.model.erpdb.Order;
import io.idstudios.flink.kafka.weborder.model.erpdb.Product;
import io.idstudios.flink.kafka.weborder.model.shipdb.Shipment;
import io.idstudios.flink.models.WebOrder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebOrderProcessor {
  public final static String LISTENER_ID = "weborderslistener";
  int messageCount = 0;

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ShipmentRepository shipmentRepository;

  @KafkaListener(id=LISTENER_ID, 
    topics = "${kafka.weborders-topic}", 
    containerFactory = "kafkaListenerContainerFactory", 
    groupId = "${spring.kafka.consumer.group-id}",
    autoStartup = "true")
  public void listen(Message<WebOrder> webOrderEventMessage) {
    log.info("WebOrder Arrived: - {}", webOrderEventMessage.toString());

    try {
      WebOrder webOrder = webOrderEventMessage.getPayload();
      Integer orderId = createOrder(webOrder);
      log.info("ORDER CREATED: " + orderId + " for web order: " + webOrder.getWebOrderId());
      try {
        createShipment(webOrder, orderId);
      } catch(Exception ex) {
        log.error("ERROR: Creating shipment, rolling back Order: " + ex.getMessage(), ex);
        orderRepository.deleteById(orderId);
      }
      
      messageCount++;
      log.info("PROCESSED: [" + messageCount + "] web orders this session.");  
    } catch (Exception ex) {
      log.error("ERROR: Creating Order: " + ex.getMessage(), ex);
    }
  }

  private Integer createOrder(WebOrder webOrder) {
    Order newOrder = new Order();
    newOrder.setCustomer_name(webOrder.getCustomerName());
    newOrder.setProduct_id(webOrder.getProductId());
    newOrder.setOrder_qty(webOrder.getQuantity());
    newOrder.setOrder_ref(webOrder.getWebOrderId());
    newOrder.setOrder_status(0);

    LocalDateTime orderDate =
      LocalDateTime.ofInstant(Instant.ofEpochMilli(webOrder.getOrderDate()),
        TimeZone.getDefault().toZoneId()); 
    
    newOrder.setOrder_date(orderDate);      
    
    Optional<Product> product = productRepository.findById(webOrder.getProductId());
    if(product.isPresent()) {
      log.info("PRODUCT: " + product.get().getName());
      newOrder.setOrder_total(product.get().getPrice() * newOrder.getOrder_qty());
    }

    orderRepository.save(newOrder);

    return newOrder.getOrder_id();
  }

  private void createShipment(WebOrder webOrder, Integer orderId) {
    Shipment ship = new Shipment();
    Random r = new Random();

    ship.setOrder_id(orderId);
    ship.setDestination(webOrder.getDestination());
    ship.setOrigin(this.warehouseList.get(r.nextInt(this.warehouseList.size())));
    ship.setHas_arrived(false);

    shipmentRepository.save(ship);
    log.info("SHIPMENT CREATED: " + ship.getShipment_id() + " for web order: " + webOrder.getWebOrderId());
  }

  private List<String> warehouseList = Arrays.asList("W1",
            "W2",
            "W3",
            "W4");
}
