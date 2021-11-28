package order;

import order.state.*;

import java.util.ArrayList;

public class Order {
    private ArrayList<Integer> movies;

    private String email,
                   orderDate;

    private long orderID;

    private OrderState state;

    public ArrayList<Integer> getMovies() {return movies;}
    public void setMovies(ArrayList<Integer> movies) {this.movies = movies;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getOrderDate() {return orderDate;}
    public void setOrderDate(String orderDate) {this.orderDate = orderDate;}

    public String getState() {return state.toString();}
    public OrderState getOrderState() {return state;}
    public void setState(String state) {
        switch (state) {
            case "Cancelled": this.state = new Cancelled(this); break;
            case "Fulfilled": this.state = new Fulfilled(this); break;
            case "Shipped": this.state = new Shipped(this); break;
            case "Await Shipment": this.state = new AwaitShipment(this); break;

            case "Await Payment":
            default:
                this.state = new AwaitPayment(this);
                break;
        }
    }

    public long getOrderID() {return orderID;}
    public void setOrderID(long orderID) {this.orderID = orderID;}

    public String toString() {
        String moviesString = (movies.size() > 0) ? movies.toString().substring(1, movies.toString().length() - 1) : "None";
        return  "Order ID: " + orderID + "\n" +
                "Customer Email: " + email + "\n" +
                "Order Date: " + orderDate + "\n" +
                "Movies: " + moviesString + "\n" +
                "Status: " + state.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Order))
            return false;

        Order obj = (Order) o;
        boolean primitive = email.equals(obj.getEmail()) &&
                            orderDate.equals(obj.getOrderDate()) &&
                            state.toString().equals(obj.getState()) &&
                            orderID == obj.getOrderID();
        boolean matchingMovies = movies.size() == obj.getMovies().size();
        if (matchingMovies) {
            for (int id : movies)
                matchingMovies = matchingMovies && obj.getMovies().contains(id);
        }

        return primitive && matchingMovies;
    }
}
