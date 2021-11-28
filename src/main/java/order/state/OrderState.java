package order.state;

import order.Order;

public abstract class OrderState {
    Order order;

    public OrderState(Order order) {
        this.order = order;
    }

    public void stateAction(boolean cancel){}
    public String toString(){return null;}
}
