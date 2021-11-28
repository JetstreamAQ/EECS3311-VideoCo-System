package storehook;

import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.OrderDB;
import user.data.User;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class StoreHook {
    private User currentUser;

    public StoreHook(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Given a user's cart---represented as an ArrayList of movies---create a new order containing their
     * added movies, and email.  Also assigns a unique order ID for them and an order state.
     *
     * @param movies ArrayList of movies the user has added
     * @return the order ID if it was successfully placed; otherwise returns -1
     */
    public long makeOrder(ArrayList<Movie> movies) {
        Order newOrder = new Order();
        newOrder.setOrderID(0);
        newOrder.setEmail(currentUser.getEmail());

        //Setting the order date
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
               month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
               day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        newOrder.setOrderDate(year + "-" + month + "-" + day);
        newOrder.setState("Await Payment");

        //Adding the movie IDs into order information
        ArrayList<Integer> addedIds = new ArrayList<>();
        MovieDB movieDB = MovieDB.getINSTANCE();
        for (Movie m : movies) {
            addedIds.add(m.getId());
            movieDB.setStock(m.getId(), m.getStock() - 1);
        }
        newOrder.setMovies(addedIds);

        long orderID = OrderDB.getINSTANCE().addOrder(newOrder);
        if (orderID == -1) { //If the order failed to be placed, restock the movies.
            for (Movie m : movies)
                movieDB.setStock(m.getId(), m.getStock() + 1);
        }
        return orderID;
    }
}
