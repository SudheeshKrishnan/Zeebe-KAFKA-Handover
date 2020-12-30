package io.flowing.retail.kafka.order.flow;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.flowing.retail.kafka.order.domain.Order;
import io.flowing.retail.kafka.order.flow.payload.RetrievePaymentCommandPayload;
import io.flowing.retail.kafka.order.messages.Message;
import io.flowing.retail.kafka.order.messages.MessageSender;
import io.flowing.retail.kafka.order.persistence.OrderRepository;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.client.api.worker.JobHandler;
import io.zeebe.client.api.worker.JobWorker;

@Component
public class NetBankingPaymentAdapter implements JobHandler {
  
  @Autowired
  private MessageSender messageSender;
  
  @Autowired
  private OrderRepository orderRepository;  

  @Autowired
  private ZeebeClient zeebe;

  private JobWorker subscription;
  
  @PostConstruct
  public void subscribe() {
    subscription = zeebe.newWorker()
      .jobType("netbanking-payment")
      .handler(this)
      .timeout(Duration.ofMinutes(1))
      .open();      
  }

  @PreDestroy
  public void closeSubscription() {
    subscription.close();      
  }

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    OrderFlowContext context = OrderFlowContext.fromMap(job.getVariablesAsMap());
    
    Order order = orderRepository.findById(context.getOrderId()).get();   
            
    // generate an UUID for this communication
    String correlationId = UUID.randomUUID().toString();

    messageSender.send( //
        new Message<RetrievePaymentCommandPayload>( //
            "RetrieveNetBankPaymentCommand", //
            context.getTraceId(), //
            new RetrievePaymentCommandPayload() //
              .setRefId(order.getId()) //
              .setReason("order") //
              .setAmount(order.getTotalSum())) //
        .setCorrelationid(correlationId));
    
    client.newCompleteCommand(job.getKey()) //
        .variables(Collections.singletonMap("CorrelationId_RetrievePayment", correlationId)) //
        .send().join();
  }

}


