import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.*;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws InterruptedException
    {
        Password.generatePasswords();

        final var myLatch = new CountDownLatch(Password.LIST_SIZE);
        for (int i = 0; i < Password.plainPasswords.size(); i++) {
            final int digit = i;
            Runnable r = () -> {
                String plain  = Password.plainPasswords.get(digit);
                byte[] hashed = Password.hash(plain);
                Password.hashedPasswords.add(hashed);
                System.out.printf("Plaintext: %s --> Hashed: %s\n", plain, Hasher.toHex(hashed));
                myLatch.countDown();
            };
            Thread.ofVirtual().start(r);
        }
        myLatch.await();

        System.out.printf("\nCreated %s passwords.\n", Password.passwordsGenerated);
        System.out.printf("Hashed %s passwords.\n", Password.hashedPasswords.size());
    }
}

class Password {
    static final int LIST_SIZE                = 3000;
    static int passwordsGenerated             = 0;
    static LinkedList<String> plainPasswords  = new LinkedList<String>();
    static LinkedList<byte[]> hashedPasswords = new LinkedList<byte[]>();

    // Generate random plaintext passwords
    static void generatePasswords() {
        for (int i = 0; i < LIST_SIZE; i++) {
            plainPasswords.add(GeneratePassword.randomPassword());
            passwordsGenerated++;
        }
    }

    // Hash password
    public static byte[] hash(String password) {
        try { return Hasher.createHash(password.toCharArray()); }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.exit(99);
        }
        return null;
    }
}
