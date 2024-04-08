/* <<< DO NOT EDIT THIS CODE >>>> */
/* This class simulates slow requests to a website which returns Shakespeare quotes.
If you provide an unexpected url then it will raise a connection error */
import java.util.concurrent.*;

public class FakeRequest {    public String get(String url) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1000, 3000));
        return "200 OK";
    }
}
