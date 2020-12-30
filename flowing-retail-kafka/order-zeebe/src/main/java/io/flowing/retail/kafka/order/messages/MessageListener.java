package io.flowing.retail.kafka.order.messages;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.flowing.retail.kafka.order.domain.Order;
import io.flowing.retail.kafka.order.flow.OrderFlowContext;
import io.flowing.retail.kafka.order.flow.payload.GoodsFetchedEventPayload;
import io.flowing.retail.kafka.order.flow.payload.GoodsShippedEventPayload;
import io.flowing.retail.kafka.order.flow.payload.PaymentReceivedEventPayload;
import io.flowing.retail.kafka.order.persistence.OrderRepository;
import io.zeebe.client.ZeebeClient;

@Component
@EnableBinding(Sink.class)
public class MessageListener {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private ZeebeClient zeebe;
	
  private ObjectMapper objectMapper;	

  @StreamListener(target = Sink.INPUT, condition = "headers['type']=='OrderPlacedEvent'")
  public void orderPlacedReceived(@Payload Message<Order> messageJson) throws JsonParseException, JsonMappingException, IOException {
    // read dat
	  System.out.println("data binded"+messageJson);
    Message<Order> message = messageJson;
    Order order = message.getData();
    
    // persist domain entity
    // (if we want to do this "transactional" this could be a step in the workflow)
    repository.save(order);

    System.out.println("order.getPaymentMode()"+order.getPaymentMode());
    // prepare data for workflow
    OrderFlowContext context = new OrderFlowContext();
    context.setOrderId(order.getId());
    context.setTraceId(message.getTraceid());
    context.setPaymentMode(order.getPaymentMode());
    // and kick of a new flow instance
    System.out.println("New order placed, start flow with " + context);
    zeebe.newCreateInstanceCommand() //
        .bpmnProcessId("zeebe_kafka") //
        .latestVersion() // 
        .variables(context.asMap()) //
        .send().join();
  }

  @StreamListener(target = Sink.INPUT, condition = "headers['type']=='PaymentReceivedEvent'")
  @Transactional
  public void paymentReceived(@Payload Message<PaymentReceivedEventPayload> message) throws Exception {

    PaymentReceivedEventPayload event = message.getData(); // TODO: Read something from it? 
  

    zeebe.newPublishMessageCommand() //
      .messageName(message.getType())
      .correlationKey(message.getCorrelationid())
      .variables(Collections.singletonMap("paymentInfo", "YeahWeCouldAddSomething"))
      .send().join();
    
    System.out.println("Correlated " + message );
  }

  @StreamListener(target = Sink.INPUT, condition = "headers['type']=='GoodsFetchedEvent'")
  @Transactional
  public void goodsFetchedReceived(@Payload Message<GoodsFetchedEventPayload> message) throws Exception {

    String pickId = message.getData().getPickId();     

    zeebe.newPublishMessageCommand() //
        .messageName(message.getType()) //
        .correlationKey(message.getCorrelationid()) // 
        .variables(Collections.singletonMap("pickId", pickId)) //
        .send().join();

    System.out.println("Correlated " + message );
  }
  
   @StreamListener(target = Sink.INPUT, condition = "headers['type']=='GoodsShippedEvent'")
  @Transactional
  public void goodsShippedReceived(@Payload Message<GoodsShippedEventPayload> message) throws Exception {

    String shipmentId = message.getData().getShipmentId();     

    zeebe.newPublishMessageCommand() //
        .messageName(message.getType()) //
        .correlationKey(message.getCorrelationid()) //
        .variables(Collections.singletonMap("shipmentId", shipmentId)) //
        .send().join();

    System.out.println("Correlated " + message );
  }
  @StreamListener(target = Sink.INPUT, condition = "headers['type']=='GoodsVerifyEvent'")
  @Transactional
  public void goodsShipErrorReceived(@Payload Message<GoodsShippedEventPayload> message) throws Exception {

	    String shipmentId = message.getData().getShipmentId();     
	    
    zeebe.newPublishMessageCommand() //
        .messageName(message.getType()) //
        .correlationKey(message.getCorrelationid()) //
        .variables(Collections.singletonMap("shipmentId", shipmentId)) //
        .send().join();

    System.out.println("Correlated GoodsShipErrorEvent" + message );
  }
}
