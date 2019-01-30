package micro_service_4.micro_service_4.Modules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="ordercartmap")
public class OrderCartMap {

    @Id
    @Column(name="orderid")
    UUID orderId;

    @Column(name="cartid")
    String cartId;

    public OrderCartMap(){

    }

    public OrderCartMap(UUID orderId, String cartId) {
        this.orderId = orderId;
        this.cartId = cartId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }


}
