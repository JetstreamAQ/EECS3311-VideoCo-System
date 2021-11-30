package system;

import order.Order;
import order.OrderDB;
import order.state.Fulfilled;

import java.util.ArrayList;

public class System {
    public System() {}

    private void performChecks() {
        ArrayList<Order> orders = OrderDB.getINSTANCE().getOrders();

        //TODO: Finish
        for (Order o : orders) {
            if (o.getState().equals("Fulfilled"))
                daysSinceBorrowed(((Fulfilled) o.getOrderState()).getDateArrived());
        }
    }

    private int daysSinceBorrowed(String date) {
        return 0;
    }
}
