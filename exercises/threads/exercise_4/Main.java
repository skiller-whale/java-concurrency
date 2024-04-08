import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/* DEADLOCKS
 * ---------
 *
 * The `Account` class represents bank accounts, and defines some utility
 * methods to deposit, withdraw, and transfer money between, accounts.
 *
 * In the `main` method, ten threads each transfer money randomly between two
 * different accounts. Each account has its own lock, and an account must hold
 * both its own lock, and the lock of the intended transferee, to complete a transfer.
 *
 * At the moment, this code suffers from a deadlock:
 *
 *      1. Run this program, and try to figure out why the deadlock occurs.
 *
 *      2. Try to resolve the deadlock by editing the Accounts class, and
 *      following the design patterns for deadlock prevention.
 *
 *      3. Run the program again, and make sure that it does not deadlock.
 *      You should see the main thread print "Transfers complete.", and then exit.
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Initiating bank transfers...");
        var myLatch = new CountDownLatch(10);
        final Random random = new Random();
        final List<Account> accounts = Account.generateAccounts(10);
        for (int i = 0; i < 10; i++) {
            final var digit = i;
            final Account transferor = accounts.get(digit);
            Runnable r = () -> {
                int transferee;
                boolean condition = true;
                while (condition) {
                    transferee = random.nextInt(accounts.size());
                    if (digit == transferee) {
                        continue;
                    } else {
                        transferor.transfer(accounts.get(transferee), 1000);
                        condition = false;
                    }
                }
                myLatch.countDown();
            };
            Thread.ofVirtual().start(r);
        }
        myLatch.await();
        System.out.println("Transfers complete.");
    }
}

class Account {
    int accountNumber;
    long balancePence;
    ReentrantLock lock;

    Account(int accountNumber, long balancePence) {
        this.accountNumber = accountNumber;
        this.balancePence = balancePence;
        this.lock = new ReentrantLock();
    }

    public void deposit(long amountPence) {
        balancePence += amountPence;
    }

    public void withdraw(long amountPence) {
        balancePence -= amountPence;
    }

    public void transfer(Account transferee, long amountPence) {
        this.lock.lock();
        try {
            System.out.printf("Initiated transfer from Account %s to Account %s.\n", this.accountNumber, transferee.accountNumber);
            System.out.printf("Waiting for Account %s to acquire lock for Account %s.\n\n", this.accountNumber, transferee.accountNumber);
            transferee.lock.lock();
            try {
                System.out.printf("Completing transfer...\n");
                if (balancePence >= amountPence) {
                    withdraw(amountPence);
                    transferee.deposit(amountPence);
                }
                System.out.printf("Transfer complete.\n");
            } finally { transferee.lock.unlock(); }
        } finally { this.lock.unlock(); }
    }

    // Factory method to generate accounts
    public static List<Account> generateAccounts(int number) {
        List<Account> accounts = new ArrayList<Account>(number);
        for (int i = 0; i < number; i++) {
            accounts.add(new Account(i, 1000000));
        }
        return accounts;
    }
}
