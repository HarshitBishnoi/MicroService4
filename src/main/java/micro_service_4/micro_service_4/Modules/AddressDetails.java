package micro_service_4.micro_service_4.Modules;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="addressdetails")
public class AddressDetails {
    @Id
    @Column(name="addressid")
    private UUID addressId;
    @Column(name="street")
    private String street;
    @Column(name="colony")
    private String colony;
    @Column(name="city")
    private String city;
    @Column(name="state")
    private String state;
    @Column(name="pinCode")
    private Integer pinCode;

    public AddressDetails() {
    }

    public AddressDetails(UUID addressId,String street, String colony, String city, String state, Integer pinCode) {
        this.street = street;
        this.colony = colony;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.addressId= addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public String getStreet() {
        return street;
    }

    public String getColony() {
        return colony;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public Integer getPinCode() {
        return pinCode;
    }

    @Override
    public String toString() {
        return this.getStreet() + "," + this.getPinCode() + "," + this.getCity();
    }
}
