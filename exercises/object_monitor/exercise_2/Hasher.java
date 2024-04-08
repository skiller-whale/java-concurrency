/* <<< DO NOT EDIT THIS CLASS >>>
 * This is a Utility class to hash a plaintext password.
 */
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

class Hasher {
    public static byte[] createHash(char[] password)
        throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Generate salt for hashing
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[22];
        random.nextBytes(salt);

        // Hash password using PBEKeySpec
        KeySpec spec = new PBEKeySpec(password, salt, 100, 192);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    public static String toHex(byte[] bytes) {
        java.math.BigInteger bigInt = new java.math.BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bigInt);
    }
}
