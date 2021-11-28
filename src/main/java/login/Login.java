package login;

import user.DBUser;
import user.data.User;

public class Login {
    /**
     * Grab the User corresponding to the given username/e-mail and return it only if the passed
     * password is equal to the password of the account
     *
     * @param username the username/e-mail to check against the DB with
     * @param password the password to check against the DB with
     * @return the User object corresponding with the given username/e-mail if the password matches
     */
    public static User login(String username, String password) {
        DBUser users = DBUser.getINSTANCE();

        User loggedUser = users.getUser(username);
        if (loggedUser != null && loggedUser.getPassword().equals(password))
            return loggedUser;

        return null;
    }
}
