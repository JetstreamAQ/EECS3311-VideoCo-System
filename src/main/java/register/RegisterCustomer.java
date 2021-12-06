package register;

import user.DBUser;
import user.data.Customer;

import java.util.ArrayList;

public class RegisterCustomer extends Register {
    public RegisterCustomer() {super();}

    /**
     * Registers a new customer into the userDB
     *
     * @param baseInfo An array of strings containing the basic information of a User; properties of User
     * @param additionalInfo An array of string containing information to the respective data type of the user
     * @param flag unused for User of type Customer
     * @return an integer depending on the success or fail state of user registration
     *          - 0: the user was successfully registered
     *          - 1: if the password was less than 8 characters or contains a space
     *          - 2: if the password contained no lower-case characters
     *          - 3: if the password contained no upper-case characters
     *          - 4: if the password contained no digits 0-9
     *          - 5: if the password contained no special characters (eg. !@#$%^&*()_+...)
     *          - 6: if not enough information was passed through the arguments (array size too large/small)
     *          - 7: if an invalid postal code was passed (must be a canadian postal code)
     *          - 8: if an invalid province code was passed or a non-existent province was passed or if the given street is of an incorrect format or not even a street
     *          - 9: if the user to register has their email in the DB already
     *          - 10: if the passed email "baseInfo[3]" is not an email
     */
    @Override
    public int registerUser(String[] baseInfo, String[] additionalInfo, String flag) {
        //Passed information must contain exactly everything a new user needs.
        if (baseInfo.length != 5 || additionalInfo.length != 4)
            return 6;

        Customer newCustomer = new Customer();
        newCustomer.setFName(baseInfo[0]);
        newCustomer.setLName(baseInfo[1]);
        newCustomer.setUsername(baseInfo[2]);
        if (!baseInfo[3].matches(".+@.+[.].+"))
            return 10;
        newCustomer.setEmail(baseInfo[3]);

        //Password must be complex enough
        int validateRetCode = validatePassword(baseInfo[4]);
        if (validateRetCode == 0)
            newCustomer.setPassword(baseInfo[4]);
        else
            return validateRetCode;

        //verifying address format
        if (additionalInfo[0].matches("\\d+ [a-z A-Z.]+"))
            newCustomer.setStreet(additionalInfo[0]);
        else
            return 8;

        //verifying postal code format
        if (additionalInfo[1].matches("^[A-Z]\\d[A-Z][ ]?\\d[A-Z]\\d$"))
            newCustomer.setPostalCode(additionalInfo[1]);
        else
            return 7;

        newCustomer.setCityTown(additionalInfo[2]);

        //verifying provence code is correct and exists in Canada
        if (super.provCodes.contains(additionalInfo[3]))
            newCustomer.setProvince(additionalInfo[3]);
        else
            return 8;

        newCustomer.setAmtOwed(0.0);
        newCustomer.setLoyaltyPoints(0);
        newCustomer.setCustOrders(new ArrayList<Long>());

        DBUser users = DBUser.getINSTANCE();

        if (users.addUser(newCustomer))
            return 0;
        else
            return 9;
    }
}
