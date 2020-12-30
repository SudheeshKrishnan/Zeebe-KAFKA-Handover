package io.flowing.retail.kafka.order.flow;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
public class RetrievePaymentAdapter implements JobHandler {
  
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
      .jobType("retrieve-payment")
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

		/*
		 * messageSender.send( // new Message<RetrievePaymentCommandPayload>( //
		 * "RetrievePaymentCommand", // context.getTraceId(), // new
		 * RetrievePaymentCommandPayload() // .setRefId(order.getId()) //
		 * .setReason("order") // .setAmount(order.getTotalSum())) //
		 * .setCorrelationid(correlationId));
		 * Object>(); 
		 * map.put("paymenttype", "creditcard");
		 */
    Map<String,Object>map=new HashMap<String,Object>();
    Integer paymenttype=0;
    System.out.println("payent"+context.getPaymentMode());
    if(null!=context.getPaymentMode() && context.getPaymentMode().equalsIgnoreCase("netbanking"))
    {
    	paymenttype=1;
    }
    else
    	if(null!=context.getPaymentMode() && context.getPaymentMode().equalsIgnoreCase("creditcard"))
        {
    		paymenttype=2;
        }
    
    map.put("CorrelationId_RetrievePayment", correlationId);
    map.put("paymenttype", paymenttype);
    
    client.newCompleteCommand(job.getKey()) //
        .variables(map) //
        .send().join();
  }

}
