package realworld.user.services.impl;

import java.util.function.Function;

/**
 * A function to scramble a password for storing in the database.
 */
public interface PasswordEncrypter extends Function<String,String> {

}
