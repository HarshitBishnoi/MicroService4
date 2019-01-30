package micro_service_4.micro_service_4.Modules;

import java.util.List;

public class UpdateQuantity {

 private List<ProductDetails> productIds;
 private Boolean toReduce;


    public UpdateQuantity() {

    }

    public UpdateQuantity(List<ProductDetails> productIds,Boolean toReduce) {
        this.toReduce = toReduce;
        this.productIds = productIds;
    }

    public Boolean getToReduce() {
        return toReduce;
    }

    public void setToReduce(Boolean toReduce) {
        this.toReduce = toReduce;
    }

    public List<ProductDetails> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<ProductDetails> productIds) {
        this.productIds = productIds;
    }
}





