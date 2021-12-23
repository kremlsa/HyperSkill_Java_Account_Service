package account.security;

import account.exceptions.BreachedPasswordException;
import account.exceptions.PasswordLengthException;

import java.util.Arrays;

public class PasswordCheck {

    static String[] breachedPass= new String[]{"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
            "PasswordForApril", "PasswordForMay", "PasswordForJune",
            "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
            "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    public static void checkPassword(String password) {
        if (password.length() < 12) {
            throw new PasswordLengthException();
        }

        if (Arrays.asList(breachedPass).contains(password)) {
            throw new BreachedPasswordException();
        }
    }

}
