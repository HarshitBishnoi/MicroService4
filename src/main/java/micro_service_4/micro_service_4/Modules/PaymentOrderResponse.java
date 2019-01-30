package micro_service_4.micro_service_4.Modules;

import java.util.List;
import java.util.UUID;

public class PaymentOrderResponse {

    UUID orderId;

    public PaymentOrderResponse(){

    }

    public PaymentOrderResponse(UUID orderId, List<String> productIds, Integer amount) {
        this.orderId = orderId;
        this.productIds = productIds;
        this.amount = amount;
    }

    List<String> productIds;
    Integer amount;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


}
