package order.state;

import order.Order;

import java.util.Calendar;

public class Fulfilled extends OrderState {
    private String dateArrived;
    private boolean returned;

    public Fulfilled(Order order) {
        super(order);
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
                month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
                day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateArrived = year + "-" + month + "-" + day;
        returned = false;
    }

    @Override
    public void stateAction(boolean cancel) {
        if (!returned)
            returned = true;
    }

    @Override
    public String toString() {
        return "Fulfilled";
    }

    public String getDateArrived() {return dateArrived;}

    public void orderReturned() {returned = true;}
    public boolean getReturned() {return returned;}
}
