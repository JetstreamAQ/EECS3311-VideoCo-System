package register;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Register {
    protected ArrayList<String> provCodes = new ArrayList<>();

    public Register() {
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
    }

    /**
     * checks if the passed password meets the required complexity of being 8 characters or more; containing
     * capital and lowercase letters; having numerical characters and containing special characters.
     *
     * @param password the password to validate
     * @return  0 - the password meets complexity standards
     *          1 - the password contains less than 8 characters
     *          2 - the password is missing lowercase characters
     *          3 - the password is missing uppcase characters
     *          4 - the password is missing numerical characters
     *          5 - the password is missing special characters
     */
    protected int validatePassword(String password) {
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
            return 1;

        if (!lc.find())
            return 2;

        if (!uc.find())
            return 3;

        if (!num.find())
            return 4;

        if (!sc.find())
            return 5;

        return 0;
    }

    public int registerUser(String[] baseInfo, String[] additionalInfo, String flag){return 0;}
}
