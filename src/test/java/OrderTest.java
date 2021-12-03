import order.Order;
import order.OrderDB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderTest {
    OrderDB orders = OrderDB.getINSTANCE();
    Order newOrder = new Order();

    @Before
    public void setup() {
        System.out.println("IMPORTANT");
        for (int i = 0; i < 10; i++)
            System.out.println("IF ANY OF THE TESTS FAILS RUN 'loadbkup.sh' IN src/resources/yaml BEFORE RUNNING TESTS AGAIN!");
        System.out.println();

        newOrder.setOrderID(-4);
        newOrder.setMovies(new ArrayList<>(Arrays.asList(-1, -2, -3)));
        newOrder.setEmail("ICantBelieveItIsAll@boiler.plate");
        newOrder.setOrderDate("2021-01-01");
        newOrder.setState("Shipped");
    }

    @Test
    public void test_order_loaded() {
        Order order = orders.getOrder(-1);
        Assert.assertNotNull(order);

        //Testing if the correct properties were loaded for id: -1
        //Can't get longs working for some reason with assertEquals(...)
        Assert.assertTrue(order.getMovies().get(0) == -1);
        Assert.assertEquals(1, order.getMovies().size());
        Assert.assertEquals("RealPerson@RealDomain.gov", order.getEmail());
        Assert.assertEquals("2021-01-01", order.getOrderDate());
        Assert.assertEquals("Await Payment", order.getState());

        //Double check: Print out string
        System.out.println(order);
    }

    @Test
    public void test_order_load_and_delete() {
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(-4, succ);

        //Check that the order was added with the correct properties
        Order addedOrder = orders.getOrder(-4);
        Assert.assertEquals(addedOrder.getOrderID(), newOrder.getOrderID()); //should match anyway; but just making sure
        Assert.assertArrayEquals(addedOrder.getMovies().toArray(), newOrder.getMovies().toArray());
        Assert.assertEquals(addedOrder.getEmail(), newOrder.getEmail());
        Assert.assertEquals(addedOrder.getOrderDate(), newOrder.getOrderDate());
        Assert.assertEquals(addedOrder.getState(), newOrder.getState());

        //Double-checking visually
        System.out.println(addedOrder);

        //removing the order
        boolean removeSucc = orders.removeOrder(-4);
        Assert.assertTrue(removeSucc);

        //Checking that the order was successfully removed
        Order orderDNE = orders.getOrder(-4); //Also doubles as a test to see if we can get an order that DNE
        Assert.assertNull(orderDNE);
    }

    @Test
    public void test_mod_order() {
        Order order = orders.getOrder(-2);
        Assert.assertNotNull(order);

        Order modOrder = new Order();
        modOrder.setOrderID(order.getOrderID());
        modOrder.setEmail(order.getEmail());
        modOrder.setOrderDate("2020-01-03");
        ArrayList<Integer> movies = new ArrayList<>(order.getMovies());
        movies.add(-2);
        movies.add(-1); //this shouldn't be in the final result
        modOrder.setMovies(movies);
        modOrder.setState("Cancelled"); //Should be able to cancel order as well

        boolean shouldFail = orders.modOrder(-1, modOrder);
        Assert.assertFalse(shouldFail);

        boolean shouldPass = orders.modOrder(-2, modOrder);
        Assert.assertTrue(shouldPass);

        //double-check
        Assert.assertEquals(modOrder, orders.getOrder(-2));
        System.out.println(orders.getOrder(-2));

        //Reverting things back
        boolean revert = orders.modOrder(-2, order);
        Assert.assertTrue(revert);

        //double-check
        Assert.assertEquals(order, orders.getOrder(-2));
        System.out.println("\n" + orders.getOrder(-2));
    }

    @Test
    public void test_cancel_when_fulfilled() {
        Order toMod = orders.getOrder(-3);
        Order modOrder = new Order();
        modOrder.setOrderID(-3);
        modOrder.setMovies(toMod.getMovies());
        modOrder.setEmail(toMod.getEmail());
        modOrder.setOrderDate(toMod.getOrderDate());
        modOrder.setState("Cancelled");

        boolean shouldFail = orders.modOrder(-3, modOrder);
        Assert.assertFalse(shouldFail);
    }

    @Test
    public void test_cancel_remove_all_movies() {
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(-4, succ);

        Order modOrder = new Order();
        modOrder.setOrderID(-4);
        modOrder.setEmail(newOrder.getEmail());
        modOrder.setOrderDate(newOrder.getOrderDate());
        modOrder.setMovies(new ArrayList<>());
        modOrder.setState(newOrder.getState());

        boolean shouldPass = orders.modOrder(-4, modOrder);
        Assert.assertTrue(shouldPass);

        //double check
        System.out.println(orders.getOrder(-4));

        //removing the order
        boolean removeSucc = orders.removeOrder(-4);
        Assert.assertTrue(removeSucc);

        //Checking that the order was successfully removed
        Order orderDNE = orders.getOrder(-4); //Also doubles as a test to see if we can get an order that DNE
        Assert.assertNull(orderDNE);
    }

    @Test
    public void test_add_existing_id() {
        newOrder.setOrderID(-3);
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(0, succ);

        Order bonusOrder = new Order();
        bonusOrder.setOrderID(-3);
        bonusOrder.setEmail("ChefChef@yahoo.ca");
        bonusOrder.setOrderDate(newOrder.getOrderDate());
        bonusOrder.setMovies(new ArrayList<>());
        bonusOrder.setState(newOrder.getState());
        succ = orders.addOrder(bonusOrder);
        Assert.assertEquals(1, succ);

        boolean remove = orders.removeOrder(0);
        Assert.assertTrue(remove);
        newOrder.setOrderID(-4);

        remove = orders.removeOrder(1);
        Assert.assertTrue(remove);
    }

    @Test
    public void test_cancel_order() {
        newOrder.setOrderID(-3);
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(0, succ);

        boolean res = orders.cancelOrder(0);
        Assert.assertTrue(res);
        Assert.assertEquals("Cancelled", orders.getOrder(0).getState());

        boolean remove = orders.removeOrder(0);
        Assert.assertTrue(remove);
    }
}
