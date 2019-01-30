package micro_service_4.micro_service_4.Modules;

import com.fasterxml.jackson.annotation.JsonProperty;
import micro_service_4.micro_service_4.Exceptions.InvalidParameterException;

import java.util.Date;
import java.util.UUID;

public class PaymentRequest {

    @JsonProperty("transactionId")
    private String paymentId;
    private UUID orderId;
    private Boolean isSuccess;

    @JsonProperty("modOfPayment")
    private String modeOfPayment;
    @JsonProperty("date")
    private Date dateOfPurchase;

    public PaymentRequest(){

    }

    public PaymentRequest(String paymentId, UUID orderId, Boolean isSuccess, String modeOfPayment, Date dateOfPurchase) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.isSuccess = isSuccess;
        this.modeOfPayment = modeOfPayment;
        this.dateOfPurchase = dateOfPurchase;
    }

    @JsonProperty("transactionId")
    public String getPaymentId() {
        return paymentId;
    }

    @JsonProperty("transactionId")
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId)throws Exception{
        this.orderId = orderId;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    @JsonProperty("modOfPayment")
    public String getModeOfPayment() {
        return modeOfPayment;
    }

    @JsonProperty("modOfPayment")
    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    @JsonProperty("date")
    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    @JsonProperty("date")
    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }



}
