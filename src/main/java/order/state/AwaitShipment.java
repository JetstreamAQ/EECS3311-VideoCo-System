package order.state;

import order.Order;

public class AwaitShipment extends OrderState {
    public AwaitShipment(Order order) {
        super(order);
    }

    @Override
    public void stateAction(boolean cancelled) {
        if (cancelled)
            order.setState("Cancelled");
        else {
            order.setState("Shipped");
        }
    }

    @Override
    public String toString() {
        return "Await Shipment";
    }
}
