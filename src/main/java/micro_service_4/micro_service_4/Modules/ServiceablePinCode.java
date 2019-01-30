package micro_service_4.micro_service_4.Modules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="servicablepincode")
public class ServiceablePinCode {

    @Id
    @Column(name = "pincode")
    Integer pinCode;

    @Column(name = "isservicable")
    boolean isServicable;

    public boolean isServicable() {
        return isServicable;
    }

    public void setServicable(boolean servicable) {
        isServicable = servicable;
    }

    public ServiceablePinCode(){

    }

    public ServiceablePinCode(int pinCode,boolean isServicable) {
        this.pinCode = pinCode;
        this.isServicable =isServicable;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }
}
