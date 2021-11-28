package order.state;

import order.Order;

public class Shipped extends OrderState{
    public Shipped(Order order) {
        super(order);
    }

    @Override
    public void stateAction(boolean cancelled) {
        if (cancelled)
            order.setState("Cancelled");
        else
            order.setState("Fulfilled");
    }

    @Override
    public String toString() {
        return "Shipped";
    }
}
