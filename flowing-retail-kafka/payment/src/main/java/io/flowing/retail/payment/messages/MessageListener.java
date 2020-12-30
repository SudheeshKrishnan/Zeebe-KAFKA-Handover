package io.flowing.retail.payment.messages;

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

@Component
@EnableBinding(Sink.class)
public class MessageListener {    
  
  @Autowired
  private MessageSender messageSender;

  @Autowired
  private ObjectMapper objectMapper;

  @StreamListener(target = Sink.INPUT, 
      condition="headers['type']=='RetrievePaymentCommand'")
  @Transactional
  public void retrievePaymentCommandReceived(@Payload Message<RetrievePaymentCommandPayload> message) throws JsonParseException, JsonMappingException, IOException {
    RetrievePaymentCommandPayload retrievePaymentCommand = message.getData();    
    
    System.out.println("Retrieve payment: " + retrievePaymentCommand.getAmount() + " for " + retrievePaymentCommand.getRefId());
    
    messageSender.send( //
        new Message<PaymentReceivedEventPayload>( //
            "PaymentReceivedEvent", //
            message.getTraceid(), //
            new PaymentReceivedEventPayload() //
              .setRefId(retrievePaymentCommand.getRefId()))
        .setCorrelationid(message.getCorrelationid()));

  }
  
  @StreamListener(target = Sink.INPUT, 
	      condition="headers['type']=='RetrieveCreditCardPaymentCommand'")
	  @Transactional
	  public void retrieveCreditCardPaymentCommand(@Payload Message<RetrievePaymentCommandPayload> message) throws JsonParseException, JsonMappingException, IOException {
	    RetrievePaymentCommandPayload retrievePaymentCommand = message.getData();    
	    
	    System.out.println("RetrieveCreditCardPaymentCommand payment: " + retrievePaymentCommand.getAmount() + " for " + retrievePaymentCommand.getRefId());
	    
	    messageSender.send( //
	        new Message<PaymentReceivedEventPayload>( //
	            "PaymentReceivedEvent", //
	            message.getTraceid(), //
	            new PaymentReceivedEventPayload() //
	              .setRefId(retrievePaymentCommand.getRefId()))
	        .setCorrelationid(message.getCorrelationid()));

	  }
    
  @StreamListener(target = Sink.INPUT, 
	      condition="headers['type']=='RetrieveNetBankPaymentCommand'")
	  @Transactional
	  public void retrieveNetBankPaymentCommand(@Payload Message<RetrievePaymentCommandPayload> message) throws JsonParseException, JsonMappingException, IOException {
	    RetrievePaymentCommandPayload retrievePaymentCommand = message.getData();    
	    
	    System.out.println("RetrieveNetBankPaymentCommand payment: " + retrievePaymentCommand.getAmount() + " for " + retrievePaymentCommand.getRefId());
	    
	    messageSender.send( //
	        new Message<PaymentReceivedEventPayload>( //
	            "PaymentReceivedEvent", //
	            message.getTraceid(), //
	            new PaymentReceivedEventPayload() //
	              .setRefId(retrievePaymentCommand.getRefId()))
	        .setCorrelationid(message.getCorrelationid()));

	  }
}
