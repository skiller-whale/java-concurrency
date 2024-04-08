import java.util.List;

/* BANK ACCOUNT TEST
 * -----------------
 *
 * The `Account` class represents a bank account, and this `Main` class is a test
 * case for a bank transfer system.
 *
 * For each of 10000 test accounts we transfer £10 to a random other account, 1000 times.
 * Each transfer happens in its own thread, with a view to stress-testing the system in a
 * highly concurrent environment.
 *
 * The code tests only whether the bank's total assets are preserved after the transfers,
 * and at the moment that is _not_ the case, even though we use `synchronized` on the
 * Account's `transfer` method.
 *
 * Can you fix it so the transfers all work without losing the bank's assets?
 *
 */

public class Main {
    public final static int ACCOUNTS = 10000, ROUNDS = 1000, TIMEOUT = 10;
    public static void main(String[] args) throws InterruptedException {
        final var random          = new java.util.Random();
        final var accounts        = Account.generateAccounts(ACCOUNTS);
        final var finishedCounter = new java.util.concurrent.CountDownLatch(ACCOUNTS);

        System.out.println("Starting "+ACCOUNTS+" threads × "+ROUNDS+" transfers of £10");
        System.out.print("Total assets before transfers: £");
        System.out.println(Account.totalBalance(accounts)/100);

        for (int i = 0; i < ACCOUNTS; i++) {
            final int digit = i;
            final Account transferor = accounts.get(digit);
            Runnable r = () -> {
                for (int j = 0; j < ROUNDS; j++) {
                    int transferee = (digit + random.nextInt(accounts.size() - 2) + 1) % accounts.size();
                    transferor.transfer(accounts.get(transferee), 1000);
                }
                finishedCounter.countDown();
            };
            Thread.ofVirtual().start(r);
        }
        if (!finishedCounter.await(TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)) {
            System.out.println("Timed out waiting for transfers to complete - is there a deadlock?");
        } else {
            System.out.print("Total assets after transfers: £");
            System.out.println(Account.totalBalance(accounts)/100);
        }
    }
}

class Account {
    int accountNumber;
    long balancePence;

    Account(int accountNumber, long balancePence) {
        this.accountNumber = accountNumber;
        this.balancePence = balancePence;
    }

    public synchronized void transfer(Account transferee, long amountPence) {
        if (balancePence >= amountPence)
        {
            balancePence -= amountPence;
            transferee.balancePence += amountPence;
        }
    }

    // Factory method to generate accounts
    public static List<Account> generateAccounts(int number) {
        List<Account> accounts = new java.util.ArrayList<Account>(number);
        for (int i = 0; i < number; i++) {
            accounts.add(new Account(i, 1000000));
        }
        return accounts;
    }

    // Total balance of all accounts
    public static long totalBalance(List<Account> accounts) {
        return accounts.stream().mapToLong(a -> a.balancePence).sum();
    }
}
