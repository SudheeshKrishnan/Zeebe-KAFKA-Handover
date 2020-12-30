package io.flowing.retail.checkout.rest;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.flowing.retail.checkout.domain.Customer;
import io.flowing.retail.checkout.domain.Order;
import io.flowing.retail.checkout.domain.RequstParamPojo;
import io.flowing.retail.checkout.messages.Message;
import io.flowing.retail.checkout.messages.MessageSender;

@RestController
public class ShopRestController {
  
  @Autowired
  private MessageSender messageSender;
  
  @RequestMapping(path = "/api/cart/order", method = RequestMethod.POST)
  public String placeOrder(@RequestBody RequstParamPojo requstParamPojo) {
    
	  System.out.println("requstParamPojo"+requstParamPojo.getPayment());
    Order order = new Order();
    order.addItem("article1", 5);
    order.addItem("article2", 10);
    order.setPaymentMode(requstParamPojo.getPayment());
    
    order.setCustomer(new Customer("Camunda", "Zossener Strasse"));
    
    Message<Order> message = new Message<Order>("OrderPlacedEvent", order);
    messageSender.send(message);
        
    // note that we cannot easily return an order id here - as everything is asynchronous
    // and blocking the client is not what we want.
    // but we return an own correlationId which can be used in the UI to show status maybe later
    return "{\"traceId\": \"" + message.getTraceid() + "\"}";
  }

}