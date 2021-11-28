package order.state;

import order.Order;

public class Fulfilled extends OrderState {
    public Fulfilled(Order order) {
        super(order);
    }

    @Override
    public String toString() {
        return "Fulfilled";
    }
}
