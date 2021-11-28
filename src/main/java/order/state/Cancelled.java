package order.state;

import order.Order;

public class Cancelled extends OrderState {
    public Cancelled(Order order) {
        super(order);
    }

    @Override
    public String toString() {
        return "Cancelled";
    }
}
