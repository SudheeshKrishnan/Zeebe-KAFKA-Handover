package io.flowing.retail.inventory.messages;

import java.io.IOException;

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

import io.flowing.retail.inventory.application.InventoryService;
import io.flowing.retail.inventory.domain.PickOrder;

@Component
@EnableBinding(Sink.class)
public class MessageListener {    
  
  @Autowired
  private MessageSender messageSender;
  
  @Autowired
  private InventoryService inventoryService;

  @Autowired
  private ObjectMapper objectMapper;
  
  @StreamListener(target = Sink.INPUT, 
      condition="headers['type']=='FetchGoodsCommand'")
  @Transactional
  public void retrievePaymentCommandReceived(@Payload Message<FetchGoodsCommandPayload> message) throws JsonParseException, JsonMappingException, IOException {
    
    FetchGoodsCommandPayload fetchGoodsCommand = message.getData();    
    String pickId = inventoryService.pickItems( // 
        fetchGoodsCommand.getItems(), fetchGoodsCommand.getReason(), fetchGoodsCommand.getRefId());
    
    messageSender.send( //
        new Message<GoodsFetchedEventPayload>( //
            "GoodsFetchedEvent", //
            message.getTraceid(), //
            new GoodsFetchedEventPayload() //
              .setRefId(fetchGoodsCommand.getRefId())
              .setPickId(pickId))
        .setCorrelationid(message.getCorrelationid()));
  }
   
}
