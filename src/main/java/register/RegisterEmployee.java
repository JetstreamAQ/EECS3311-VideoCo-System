package register;

import user.DBUser;
import user.data.*;

public class RegisterEmployee extends Register {
    public RegisterEmployee() {super();}

    /**
     * Registers a new employee into the userDB
     *
     * @param baseInfo An array of strings containing the basic information of a User; properties of User
     * @param additionalInfo An array of string containing information to the respective data type of the user
     * @param flag dictates what type of employee is being registered
     * @return an integer depending on the success or fail state of user registration
     *          - 0: the user was successfully registered
     *          - 1: if the password was less than 8 characters or contains a space
     *          - 2: if the password contained no lower-case characters
     *          - 3: if the password contained no upper-case characters
     *          - 4: if the password contained no digits 0-9
     *          - 5: if the password contained no special characters (eg. !@#$%^&*()_+...)
     *          - 6: if the arguments passed were insufficient (no flags, information arrays too small/large, invalid info)
     *          - 7: if an invalid postal code was passed (must be a canadian postal code)
     *          - 8: if an invalid province code was passed or a non-existent province was passed.
     *          - 9: if the user to register has their email in the DB already
     *          - 10: if the passed email "baseInfo[3]" is not an email
     */
    @Override
    public int registerUser(String[] baseInfo, String[] additionalInfo, String flag) {
        //Passed information must contain exactly everything a new user needs.
        if (baseInfo.length != 5 || flag == null || flag.length() == 0)
            return 6;

        int numOfAdditionalInfo = (flag.equals("Admin") || flag.equals("Cashier") || flag.equals("Inventory Operator")) ? 1 : 2;
        if (additionalInfo.length != numOfAdditionalInfo)
            return 6;

        if (!baseInfo[3].matches(".+@.+[.].+"))
            return 10;

        //Generate a unique ID for the new employee
        DBUser users = DBUser.getINSTANCE();
        int newID = users.generateEmployeeID();

        int validateRetCode = validatePassword(baseInfo[4]);
        switch (flag) {
            case "Admin": /*ADDING AN ADMIN*/
                Admin newAdmin = new Admin();
                if (!setBaseInfo(newAdmin, baseInfo, validateRetCode, newID))
                    return validateRetCode;

                if (additionalInfo[0].matches("^GMT[-+]?\\d[012:][:]?[03][0]$"))
                    newAdmin.setTimeZone(additionalInfo[0]);
                else
                    return 6;

                if (users.addUser(newAdmin))
                    return 0;
                else
                    return 9;

            case "Cashier": /*ADDING A CASHIER*/
                Cashier newCashier = new Cashier();
                if (!setBaseInfo(newCashier, baseInfo, validateRetCode, newID))
                    return validateRetCode;

                if (additionalInfo[0].equals("In-Store Location 1") || additionalInfo[0].equals("In-Store Location 2"))
                    newCashier.setLocation(additionalInfo[0]);
                else
                    return 6;

                if (users.addUser(newCashier))
                    return 0;
                else
                    return 9;

            case "Inventory Operator": /*ADDING AN INVENTORY OPERATOR*/
                InventoryOperator newInvOp = new InventoryOperator();
                if (!setBaseInfo(newInvOp, baseInfo, validateRetCode, newID))
                    return validateRetCode;

                if (additionalInfo[0].matches("^\\d\\d\\d\\d$"))
                    newInvOp.setExtensionNum(additionalInfo[0]);
                else
                    return 6;

                if (users.addUser(newInvOp))
                    return 0;
                else
                    return 9;

            case "Warehouse Shipping": /*ADDING A WAREHOUSE SHIPPING TEAM MEMBER*/
                WarehouseShippingTeam newWst = new WarehouseShippingTeam();
                if (!setBaseInfo(newWst, baseInfo, validateRetCode, newID))
                    return validateRetCode;

                if (super.provCodes.contains(additionalInfo[0]))
                    newWst.setWarehouseLocation(additionalInfo[0]);
                else
                    return 6;

                newWst.setIsShipping(additionalInfo[1].toLowerCase().equals("true"));

                if (users.addUser(newWst))
                    return 0;
                else
                    return 9;

            default:
                return 6;
        }
    }

    /**
     * Each employee shares the same base properties.
     *
     * @param emp the employee object to set the properties of
     * @param baseInfo the array of base info to use
     * @param retCode the return code of the password check
     * @param id the generated ID of the employee
     * @return true if the base info has been set; false otherwise---usually if password is invalid
     */
    private boolean setBaseInfo(Employee emp, String[] baseInfo, int retCode, int id) {
        emp.setFName(baseInfo[0]);
        emp.setLName(baseInfo[1]);
        emp.setUsername(baseInfo[2]);
        emp.setEmail(baseInfo[3]);

        if (retCode == 0)
            emp.setPassword(baseInfo[4]);
        else
            return false;

        emp.setId(id);
        return true;
    }
}
