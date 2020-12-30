package io.flowing.retail.kafka.order.flow.payload;

public class RetrievePaymentCommandPayload {
  
  private String refId;
  private String reason;
  private int amount;
  private String paymenttype;
  
  public String getRefId() {
    return refId;
  }
  public RetrievePaymentCommandPayload setRefId(String refId) {
    this.refId = refId;
    return this;
  }
  public String getReason() {
    return reason;
  }
  public RetrievePaymentCommandPayload setReason(String reason) {
    this.reason = reason;
    return this;
  }
  public int getAmount() {
    return amount;
  }
  public RetrievePaymentCommandPayload setAmount(int amount) {
    this.amount = amount;
    return this;
  }
  public RetrievePaymentCommandPayload setPaymentType(String paymenttype) {
	    this.paymenttype = paymenttype;
	    return this;
	  }
}