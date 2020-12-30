package io.flowing.retail.kafka.order.flow;

import java.util.HashMap;
import java.util.Map;

public class OrderFlowContext {

  private String traceId;
  private String orderId;
  private String pickId;
  private String shipmentId;
  private String paymentMode;

  public static OrderFlowContext fromMap(Map<String, Object> values) {
    OrderFlowContext context = new OrderFlowContext();
    context.traceId = (String) values.get("traceId");
    context.orderId = (String) values.get("orderId");
    context.pickId = (String) values.get("pickId");
    context.shipmentId = (String) values.get("shipmentId");
    context.paymentMode = (String) values.get("paymentMode");
    return context;
  }
  
  public Map<String, String> asMap() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("traceId", traceId);
    map.put("orderId", orderId);
    map.put("pickId", pickId);
    map.put("shipmentId", shipmentId);
    map.put("paymentMode", paymentMode);
    return map;
  }
  
  public String getPickId() {
    return pickId;
  }
  public OrderFlowContext setPickId(String pickId) {
    this.pickId = pickId;
    return this;
  }
  public String getTraceId() {
    return traceId;
  }
  public OrderFlowContext setTraceId(String traceId) {
    this.traceId = traceId;
    return this;
  }
  public String getOrderId() {
    return orderId;
  }
  public OrderFlowContext setOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }
  public String getShipmentId() {
    return shipmentId;
  }
  public OrderFlowContext setShipmentId(String shipmentId) {
    this.shipmentId = shipmentId;
    return this;
  }

  
  
  public String getPaymentMode() {
	return paymentMode;
}

public void setPaymentMode(String paymentMode) {
	this.paymentMode = paymentMode;
}

@Override
  public String toString() {
    return "OrderFlowContext [traceId=" + traceId + ", orderId=" + orderId + ", pickId=" + pickId + ", shipmentId=" + shipmentId + "]";
  }
  
}
