package order;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class OrderDB {
    /**HashMap for storing orders by their IDs**/
    private Map<Long, Order> orderDB = new HashMap<>();

    /**ArrayList for faster modifications to the written DB**/
    private ArrayList<Order> orderArray = new ArrayList<>();

    /**Singleton Instance**/
    private static final OrderDB INSTANCE = new OrderDB();

    /**Grabbing the singleton instance**/
    public static OrderDB getINSTANCE() {
        return INSTANCE;
    }

    private OrderDB() {
        loadYAML();
    }

    /**
     * Grabs information from the YAML file; storing grabbed data in a HashMap
     */
    private void loadYAML() {
        //Flushing the HashMap and ArrayList
        orderDB = new HashMap<>();

        Yaml yaml = new Yaml();

        InputStream is = null;
        try {
            is = Objects.requireNonNull(getClass().getClassLoader().getResource("yaml/orders.yml")).openStream();
        } catch (IOException e) {
            System.out.println("[OrderDB]: DB Loading Error\n" + e.getMessage());
        }

        Orders orders = yaml.loadAs(is, Orders.class);
        orderArray.addAll(orders.getOrders());

        for (Order o : orders.getOrders())
            orderDB.put(o.getOrderID(), o);
    }

    /**
     * Stores data back into the YAML file
     */
    private void writeYAML() {
        //Loading the YAML file for writing
        FileWriter fw = null;
        try {
            fw = new FileWriter("src/main/resources/yaml/orders.yml");
        } catch (IOException e) {
            System.out.println("[OrderDB]: DB File Error\n" + e.getMessage());
        }

        Yaml yaml = new Yaml();
        Orders orders = new Orders();
        orders.setOrders(orderArray);
        yaml.dump(orders, fw);
    }

    /**
     * Grab an order from the DB using an ID number
     *
     * @param id the ID number to use in the search
     * @return The order with the matching ID.  Otherwise returns null.
     */
    public Order getOrder(long id) {
        if (orderDB.containsKey(id))
            return orderDB.get(id);

        System.out.println("[OrderDB]: Order with specified ID DNE.");
        return null;
    }

    /**
     * Get a list of all orders currently in the system
     *
     * @return a list of all orders in the system
     */
    public ArrayList<Order> getOrders() {return orderArray;}

    /**
     * Add an order to the DB and update the DB file
     *
     * @param order the order to add to the DB
     * @return the ID of the added order; else returns -1
     */
    public long addOrder(Order order) {
        //Check if order exists in the DB or if the orderID already exists
        if (orderDB.containsValue(order))
            return -1;

        //TODO: write up test which covers this block here
        if (orderDB.containsKey(order.getOrderID())) {
            long maxId = 0;
            ArrayList<Long> usedIDs = new ArrayList<>();
            for (Order o : orderArray)
                usedIDs.add(o.getOrderID());

            Collections.sort(usedIDs);
            for (Long l : usedIDs) {
                if (!orderDB.containsKey(maxId))
                    break;
                maxId = (l >= maxId) ? l + 1 : maxId;
            }

            order.setOrderID(maxId);
        }

        orderArray.add(order);
        orderDB.put(order.getOrderID(), order);
        writeYAML();
        return order.getOrderID();
    }

    /**
     * "Modifies" an order in the DB by effectively replacing it with a modified replica
     *
     * @param id the ID of the order to modify
     * @param order the modified replica
     * @return  true if the order was successfully modified
     *          false if the order was no modified
     */
    public boolean modOrder(long id, Order order) {
        if (!orderDB.containsKey(id) || order.getOrderID() != id)
            return false;

        //Pruning any duplicate movie IDs or cancelling the order
        if (order.getMovies() != null && order.getMovies().size() > 0) {
            ArrayList<Integer> prune = order.getMovies().stream().distinct().collect(Collectors.toCollection(ArrayList::new));
            order.setMovies(prune);
        } else {
            order.setState("Cancelled");
        }

        //Changing the order state; can only change to cancel if the order has not been fulfilled yet!
        if (orderDB.get(id).getState().equals("Fulfilled") && order.getState().equals("Cancelled"))
            return false;

        orderDB.replace(id, order);
        orderArray = new ArrayList<>();
        orderArray.addAll(orderDB.values());
        writeYAML();
        return true;
    }

    /**
     * Sets the order state to be cancelled
     *
     * @param id the ID of the order to cancel
     * @return true if the order was cancelled; false otherwise
     */
    public boolean cancelOrder(long id) {
        if (!orderDB.containsKey(id))
            return false;

        if (!orderDB.get(id).getState().equals("Fulfilled"))
            orderDB.get(id).setState("Cancelled");

        writeYAML();
        return true;
    }

    /**
     * removes the order with the associated ID from the DB.  This method is only to be used in testing
     * as to prevent the cluttering of the DB file.
     *
     * @param id the ID of the order to remove
     * @return  true if the order was removed
     *          false if the order was not removed
     */
    public boolean removeOrder(long id) {
        if (!orderDB.containsKey(id))
            return false;

        orderArray.remove(orderDB.get(id));
        orderDB.remove(id);
        writeYAML();
        return true;
    }
}
