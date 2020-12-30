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
import io.flowing.retail.kafka.order.flow.payload.ShipGoodsCommandPayload;
import io.flowing.retail.kafka.order.messages.Message;
import io.flowing.retail.kafka.order.messages.MessageSender;
import io.flowing.retail.kafka.order.persistence.OrderRepository;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.client.api.worker.JobHandler;
import io.zeebe.client.api.worker.JobWorker;

@Component
public class ShipGoodsAdapter implements JobHandler {
	public static int i=2;

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
      .jobType("ship-goods")
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
    String correlationId1 = UUID.randomUUID().toString();
    Integer success=0;
    System.out.println("normal mode");
    	  messageSender.send(new Message<ShipGoodsCommandPayload>( //
    	            "ShipGoodsCommand", //
    	            context.getTraceId(), //
    	            new ShipGoodsCommandPayload() //
    	              .setRefId(order.getId())
    	              .setPickId(context.getPickId()) //
    	              .setRecipientName(order.getCustomer().getName()) //
    	              .setRecipientAddress(order.getCustomer().getAddress())) //
    	        .setCorrelationid(correlationId));
    	  
    	    Map<String,Object>map=new HashMap<String,Object>();
    	    
    	  map.put("CorrelationId_ShipGoods", correlationId);
    	  if(i%2==0)
    	  {
    		  success=1;
    	  map.put("success", success);
    	  }
    	  else
    	  {
    		  success=0;
        	  map.put("success", success);
    	  }
    	  
    	  client.newCompleteCommand(job.getKey()) //
          .variables(map) //
          .send().join();
  i++;
    
  }  

}
