package user.data;

import java.util.ArrayList;

public class Customer extends User {
    private String street,
                   postalCode,
                   province;
    private double amtOwed;
    private int loyaltyPoints;

    private ArrayList<Long> custOrders;

    public String getStreet() {return street;}
    public void setStreet(String street) {this.street = street;}

    public String getPostalCode() {return postalCode;}
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}

    public String getProvince() {return province;}
    public void setProvince(String province) {this.province = province;}

    public double getAmtOwed() {return amtOwed;}
    public void setAmtOwed(double amtOwed) {this.amtOwed = amtOwed;}

    public int getLoyaltyPoints() {return loyaltyPoints;}
    public void setLoyaltyPoints(int loyaltyPoints) {this.loyaltyPoints = loyaltyPoints;}

    public ArrayList<Long> getCustOrders() {return custOrders;}
    public void setCustOrders(ArrayList<Long> custOrders) {this.custOrders = custOrders;}
}
