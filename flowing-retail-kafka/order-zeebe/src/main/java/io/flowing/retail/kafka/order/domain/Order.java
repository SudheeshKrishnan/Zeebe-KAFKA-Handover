package io.flowing.retail.kafka.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name="OrderEntity")
public class Order {

  @Id
//  @GeneratedValue(generator = "uuid2")
//  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  protected String id; // = UUID.randomUUID().toString();
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER  )
  protected Customer customer = new Customer();
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER  )
  protected List<OrderItem> items = new ArrayList<OrderItem>();

  @Transient
  private String paymentMode;
  
  public String getPaymentMode() {
	return paymentMode;
}

public void setPaymentMode(String paymentMode) {
	this.paymentMode = paymentMode;
}

public void setItems(List<OrderItem> items) {
	this.items = items;
}

public void addItem(OrderItem i) {
    items.add(i);
  }
  
  public int getTotalSum() {
    int sum = 0;
    for (OrderItem orderItem : items) {
      sum += orderItem.getAmount();
    }
    return sum;
  }
  
  public String getId() {
    return id;
  }

  @JsonProperty("orderId")
  public void setId(String id) {
    this.id = id;
  }

  public List<OrderItem> getItems() {
    return items;
  }


 
  @Override
  public String toString() {
    return "Order [id=" + id + ", items=" + items + "]";
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }


  
}
