package user;

import movie.Movies;
import org.yaml.snakeyaml.Yaml;
import user.data.Employee;
import user.data.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DBUser {
    /**Hash map containing each user; maps their email to the User object**/
    Map<String, User> userDB = new HashMap<>();

    /**Hash map containing each user; maps their username to the User object**/
    Map<String, User> usernames = new HashMap<>();

    /**ArrayList containing each user for faster modification of DB**/
    ArrayList<User> userArray = new ArrayList<>();

    /**Singleton Instance**/
    private static final DBUser INSTANCE = new DBUser();

    /**Grabbing the singleton instance**/
    public static DBUser getINSTANCE() {return INSTANCE;}

    private DBUser() {loadYAML();}

    /**
     * Grabs information from the YAML file; storing grabbed data in a HashMap
     */
    private void loadYAML() {
        //Flushing the HashMap and ArrayList
        userDB = new HashMap<>();
        usernames = new HashMap<>();
        userArray = new ArrayList<>();

        Yaml yaml = new Yaml();

        InputStream is = null;
        try {
            is = Objects.requireNonNull(getClass().getClassLoader().getResource("yaml/users.yml")).openStream();
        } catch (IOException e) {
            System.out.println("[UserDB]: DB Loading Error\n" + e.getMessage());
        }

        Users users = yaml.loadAs(is, Users.class);
        userArray.addAll(users.getUsers());

        for (User u : users.getUsers()) {
            userDB.put(u.getEmail().toLowerCase(), u);
            usernames.put(u.getUsername(), u);
        }
    }

    /**
     * Stores data back into the YAML file
     */
    private void writeYAML() {
        //Loading the YAML file for writing
        FileWriter fw = null;
        try {
            fw = new FileWriter("src/main/resources/yaml/users.yml");
        } catch (IOException e) {
            System.out.println("[UserDB]: DB File Error\n" + e.getMessage());
        }

        Yaml yaml = new Yaml();
        Users users = new Users();
        users.setUsers(userArray);
        yaml.dump(users, fw);
    }

    /**
     * Searches the userDB for a user with the matching e-mail address
     *
     * @param key the e-mail address or username of the user to search for
     * @return the User object of the user is they exist and there's an existing type for them; otherwise, returns null
     */
    public User getUser(String key) {
        //checking for e-mail address
        if (userDB.containsKey(key.toLowerCase()))
            return userDB.get(key.toLowerCase());

        //checking for the username
        if (usernames.containsKey(key))
            return usernames.get(key);

        System.out.println("[UserDB]: User not found");
        return null;
    }

    /**
     * Get a list of every registered user.
     *
     * @return a list of every registered user.
     */
    public ArrayList<User> getUsers() {return userArray;}

    /**
     * adds a user with the appropriate data type to the userDB
     *
     * @param user the user to add to the DB
     * @return  true if user was added to the DB
     *          false if user was not added to the DB
     */
    public boolean addUser(User user) {
        if (userDB.containsKey(user.getEmail().toLowerCase()))
            return false;

        //finding if the user's username is present in the db
        if (usernames.containsKey(user.getUsername()))
            return false;

        if (userArray.contains(user))
            return false;

        userArray.add(user);
        userDB.put(user.getEmail().toLowerCase(), user);
        usernames.put(user.getUsername(), user);
        writeYAML();
        return true;
    }

    /**
     * remove the user with the associated email from the DB
     *
     * @param email the email of the user to remove
     * @return  true if the associated user was removed from the DB
     *          false if the associated user was not removed from the DB
     */
    public boolean removeUser(String email) {
        if (!userDB.containsKey(email.toLowerCase()))
            return false;

        userArray.remove(userDB.get(email.toLowerCase()));
        usernames.remove(userDB.get(email.toLowerCase()).getUsername());
        userDB.remove(email.toLowerCase());
        writeYAML();
        return true;
    }

    /**
     * "Modifies" the user with the associated email address by replacing it with a modified
     * copy of the relevant User object.
     *
     * @param email the associated e-mail address of the user to modify
     * @param user the modified copy of the same user to replace it with
     * @return  0 if the user was modified successfully
     *          1 if there exists no user with the given e-mail
     *          2 if the modified copy is not of the same data type as the original
     */
    public int modifyUser(String email, User user) {
        if (!userDB.containsKey(email.toLowerCase()))
            return 1;

        //get the data type of the object being passed and comparing it to that of the
        //user associated with the passed email
        User assocUser = userDB.get(email.toLowerCase());
        if (!(user.getClass().equals(assocUser.getClass())))
            return 2;

        if (email.equalsIgnoreCase(user.getEmail()) || user.getUsername().equals(assocUser.getUsername())) {
            userDB.put(email.toLowerCase(), user);
            usernames.put(user.getUsername(), user);
        } else {
            userDB.remove(email.toLowerCase());
            userDB.put(user.getEmail().toLowerCase(), user);
            usernames.remove(assocUser.getUsername());
            usernames.put(user.getUsername(), user);
        }
        userArray = new ArrayList<>();
        userArray.addAll(userDB.values());
        writeYAML();
        return 0;
    }

    /**
     * Checks among the list of users and finds each employee to check their ID to find the highest ID num value.
     * It takes that value and increases by one.  Since values will always be generated this way, there will never be an
     * empty vacuum of integer IDs.  Generated IDs will remain the same until they're used.
     *
     * @return an unused Employee ID
     */
    public int generateEmployeeID() {
        int newID = 0;
        ArrayList<Integer> usedIds = new ArrayList<>();
        for (User user : userArray) {
            if (user instanceof Employee)
                usedIds.add(((Employee) user).getId());
        }

        Collections.sort(usedIds);
        for (int i : usedIds) {
            if (!usedIds.contains(newID))
                break;
            newID = (i >= newID) ? i + 1 : newID;
        }

        return newID;
    }
}
