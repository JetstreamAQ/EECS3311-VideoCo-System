package user;

import user.data.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyUserDB {
    /**
     * Remove the user associated with the given key from the database.
     *
     * @param key the username/email of the associated user in the db
     * @return true if a user associated with the key was found and removed; false otherwise
     */
    public static boolean removeUser(String key) {
        DBUser users = DBUser.getINSTANCE();
        return users.removeUser(key);
    }

    /**
     * making use of DBUser's modifyUser(), parse two passed string arrays into a new modified clone
     * of the specified user associated with the passed email.
     *
     * @param key the email of the associated user to modify
     * @param baseInfo an array containing base info shared among each user class
     * @param additionalInfo string array of the unique info between each class
     * @return  -1 - the password contains less than 8 characters
     *          -2 - the password is missing lowercase characters
     *          -3 - the password is missing uppcase characters
     *          -4 - the password is missing numerical characters
     *          -5 - the password is missing special characters
     *          0 - the user associated with the passed email was successfully modified
     *          1 - there is no user with the associated email/passed key is not an email/given email is invalid
     *          2 - the array sizes are incorrect; not enough information in baseInfo or additionalInfo
     *          3 - if the email/username is being changed and there already another user using the new email/username
     *          4 - passed province code is invalid
     *          5 - passed timezone is invalid
     *          6 - passed postal code is invalid
     *          7 - passed extension number is invalid
     */
    public static int modify(String key, String[] baseInfo, String[] additionalInfo) {
        DBUser users = DBUser.getINSTANCE();

        User toMod = users.getUser(key);
        if(toMod == null)
            return 1;

        if (!baseInfo[3].matches(".+@.+[.].+"))
            return 1;

        //Ensure that base info array is of the right size
        if (baseInfo.length != 5)
            return 2;

        //Check if the email is being changed.  If so, check if the new email exists in the DB
        if (!toMod.getEmail().equalsIgnoreCase(baseInfo[3]) && users.getUser(baseInfo[3]) != null)
            return 3;

        //Check if the username is being changed.  If so, check if the new username exists in the DB
        if (!toMod.getUsername().equals(baseInfo[2]) && users.getUser(baseInfo[2]) != null)
            return 3;

        //Checking if the password passed meets complexity requirements
        int validateRes = validatePassword(baseInfo[4]);
        if (validateRes != 0)
            return validateRes;

        //Setting up province codes for warehouse and admin
        ArrayList<String> provCodes = new ArrayList<>(); //For Warehouse Shipping/Customer
        provCodes.add("NL");
        provCodes.add("PE");
        provCodes.add("NS");
        provCodes.add("NB");
        provCodes.add("QC");
        provCodes.add("ON");
        provCodes.add("MB");
        provCodes.add("SK");
        provCodes.add("AB");
        provCodes.add("BC");
        provCodes.add("YT");
        provCodes.add("NT");
        provCodes.add("NU");

        //Compare the instance of the user to modified, make an appropriate clone and modify
        User clone;
        if (toMod instanceof Admin) {
            Admin modAdmin = new Admin();
            baseProperties(modAdmin, baseInfo);

            if (additionalInfo.length != 1)
                return 2;

            modAdmin.setId(((Admin) toMod).getId());
            if (additionalInfo[0].matches("^GMT[-+]?\\d[012:][:]?[03][0]$")) //verify timezone
                modAdmin.setTimeZone(additionalInfo[0]);
            else
                return 5;

            clone = modAdmin;
        } else if (toMod instanceof Cashier) {
            Cashier modCashier = new Cashier();
            baseProperties(modCashier, baseInfo);

            if (additionalInfo.length != 1)
                return 2;

            modCashier.setId(((Cashier) toMod).getId());
            modCashier.setLocation(additionalInfo[0]);

            clone = modCashier;
        } else if (toMod instanceof Customer) {
            Customer modCust = new Customer();
            baseProperties(modCust, baseInfo);

            if (additionalInfo.length != 3)
                return 2;

            modCust.setStreet(additionalInfo[0]);

            //verify postal code
            if (additionalInfo[1].matches("^[A-Z]\\d[A-Z][ ]?\\d[A-Z]\\d$"))
                modCust.setPostalCode(additionalInfo[1]);
            else
                return 6;

            //verify province code
            if (provCodes.contains(additionalInfo[2]))
                modCust.setProvince(additionalInfo[2]);
            else
                return 4;

            modCust.setAmtOwed(((Customer) toMod).getAmtOwed());
            modCust.setLoyaltyPoints(((Customer) toMod).getLoyaltyPoints());
            modCust.setCustOrders(((Customer) toMod).getCustOrders());

            clone = modCust;
        } else if (toMod instanceof InventoryOperator) {
            InventoryOperator modOp = new InventoryOperator();
            baseProperties(modOp, baseInfo);

            if (additionalInfo.length != 1)
                return 2;

            modOp.setId(((InventoryOperator) toMod).getId());
            if (additionalInfo[0].matches("^\\d\\d\\d\\d$"))
                modOp.setExtensionNum(additionalInfo[0]);
            else
                return 7;


            clone = modOp;
        } else if (toMod instanceof  WarehouseShippingTeam) {
            WarehouseShippingTeam modWst = new WarehouseShippingTeam();
            baseProperties(modWst, baseInfo);

            if (additionalInfo.length != 2)
                return 2;

            if (provCodes.contains(additionalInfo[0]))
                modWst.setWarehouseLocation(additionalInfo[0]);
            else
                return 4;
            modWst.setIsShipping((additionalInfo[1].toLowerCase().equals("true")));

            clone = modWst;
        } else {
            return 3;
        }

        return users.modifyUser(key, clone);
    }

    /**
     * Each user shares the same base properties
     *
     * @param user the cloned user object to set the properties of
     * @param baseInfo the properties to set the clone object to
     */
    private static void baseProperties(User user, String[] baseInfo) {
        user.setFName(baseInfo[0]);
        user.setLName(baseInfo[1]);
        user.setUsername(baseInfo[2]);
        user.setEmail(baseInfo[3]);
        user.setPassword(baseInfo[4]);
    }

    /**
     * checks if the passed password meets the required complexity of being 8 characters or more; containing
     * capital and lowercase letters; having numerical characters and containing special characters.
     *
     * @param password the password to validate
     * @return  0 - the password meets complexity standards
     *          -1 - the password contains less than 8 characters
     *          -2 - the password is missing lowercase characters
     *          -3 - the password is missing uppcase characters
     *          -4 - the password is missing numerical characters
     *          -5 - the password is missing special characters
     */
    private static int validatePassword(String password) {
        /*
         * String.matches(pattern: String) appends ^ to the start and $ to the end of the expression automatically
         * for some reason.  Have to use Pattern and Matcher to do anything Regex in Java.
         */
        Pattern lowerCase = Pattern.compile("[a-z]"),
                upperCase = Pattern.compile("[A-Z]"),
                numbers = Pattern.compile("[0-9]"),
                specChars = Pattern.compile("[\\[\\]/!@#$%^&*()_+=\\-<>,.;:'\\\"`{}~\\\\|\\?]");

        Matcher lc = lowerCase.matcher(password),
                uc = upperCase.matcher(password),
                num = numbers.matcher(password),
                sc = specChars.matcher(password);
        if (!(password.length() > 8) || password.contains(" "))
            return -1;

        if (!lc.find())
            return -2;

        if (!uc.find())
            return -3;

        if (!num.find())
            return -4;

        if (!sc.find())
            return -5;

        return 0;
    }
}
