/* <<< DO NOT EDIT THIS CODE >>>> */
/* This class simulates requests to a website which returns some weather data.
If you provide an unexpected url then it will raise a connection error */

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.Iterator;
import java.net.*;
import java.time.LocalDate;

public class FakeRequest {
    public static final String expectedURL = "http://weather.com/london/";

    // Stream weather temperature data
    Stream<String> weatherTempGenerator = Stream.generate(() -> {
        int randomTemp = ThreadLocalRandom.current().nextInt(0, 36);
        LocalDate randomDate = RandomDate.createRandomDate(2024, 2079);
        return String.format("Predicted temperature for %s: %s\u00B0C", randomDate, randomTemp);
    });

    // Stream rainfall data
    Stream<String> weatherRainfallGenerator = Stream.generate(() -> {
        int randomRainfall = ThreadLocalRandom.current().nextInt(0, 23);
        LocalDate randomDate = RandomDate.createRandomDate(2024, 2079);
        return String.format("Predicted rainfall (24h) for %s: %smm", randomDate, randomRainfall);
    });

    public String get(String url, Boolean choice) throws InterruptedException, ConnectException {
        if (url != FakeRequest.expectedURL) {
            System.out.printf("FakeRequests only simulates requests to %s\n", FakeRequest.expectedURL);
            throw new ConnectException();
        }
        if (choice) {
            Iterator<String> iter = this.weatherTempGenerator.iterator();
            var temp = iter.next();
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 1800));
            return temp;
        } else {
            Iterator<String> iter = this.weatherRainfallGenerator.iterator();
            var rainfall = iter.next();
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 1800));
            return rainfall;
        }
    }
}


class RandomDate {
    public static LocalDate createRandomDate(int startYear, int endYear) {
        int day = ThreadLocalRandom.current().nextInt(1, 29);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int year = ThreadLocalRandom.current().nextInt(startYear, endYear + 1);
        return LocalDate.of(year, month, day);
    }
}
