package order.state;

import order.Order;

public class AwaitPayment extends OrderState {
    public AwaitPayment(Order order) {
        super(order);
    }

    @Override
    public void stateAction(boolean cancelled) {
        if (cancelled)
            order.setState("Cancelled");
        else
            order.setState("Await Shipment");
    }

    @Override
    public String toString() {
        return "Await Payment";
    }
}
