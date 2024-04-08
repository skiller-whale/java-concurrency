/* <<< DO NOT EDIT THIS CLASS >>>
 * This is a Utility class to generate a random plaintext password.
 */
import java.security.SecureRandom;

class GeneratePassword {
    private static final String ACCEPTED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-!@$";
    private static final SecureRandom RANDOM   = new SecureRandom();

    public static String randomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            char randomChar = ACCEPTED_CHARS.charAt(RANDOM.nextInt(ACCEPTED_CHARS.length()));
            password.append(randomChar);
        }
        return password.toString();

    }
}
