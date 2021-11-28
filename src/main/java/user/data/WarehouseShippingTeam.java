package user.data;

public class WarehouseShippingTeam extends Employee {
    private String warehouseLocation;
    private boolean isShipping;

    public String getWarehouseLocation() {return warehouseLocation;}
    public void setWarehouseLocation(String warehouseLocation) {this.warehouseLocation = warehouseLocation;}

    public boolean getIsShipping() {return isShipping;}
    public void setIsShipping(boolean isShipping) {this.isShipping = isShipping;}
}
