package micro_service_4.micro_service_4.Modules;


//NOT USED IN THE ENTIRE CODEBASE, KEPT FOR POTENTIAL USE
public class IndividualProductQuantity {
    private String productIDs;
    private Integer quantity;

    public IndividualProductQuantity() {
    }

    public IndividualProductQuantity(String productIDs, Integer quantity) {
        this.productIDs = productIDs;
        this.quantity = quantity;
    }

    public String getProductIDs() {
        return productIDs;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductIDs(String productIDs) {
        this.productIDs = productIDs;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
