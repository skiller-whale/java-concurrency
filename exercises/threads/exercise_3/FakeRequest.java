/* <<< DO NOT EDIT THIS CODE >>>> */
/* This class simulates slow requests to a website which returns Shakespeare quotes.
If you provide an unexpected url then it will raise a connection error */

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.Iterator;
import java.net.*;

public class FakeRequest {
    public static final String expectedURL = "http://shakespeare.com/quote/";
    public static final List<String> quotes = Stream.of(
        "The web of our life is of a mingled yarn, good and ill together",
        "O excellent! I love long life better than figs",
        "O gentlemen, the time of life is short!",
        "Let life be short: else shame will be too long",
        "Thy life's a miracle",
        "To be, or not to be,—that is the question:—",
        "Whether 'tis nobler in the mind to suffer",
        "The slings and arrows of outrageous fortune,",
        "Or to take arms against a sea of troubles,",
        "And by opposing end them?—To die, to sleep,—",
        "No more; and by a sleep to say we end",
        "The heart-ache, and the thousand natural shocks",
        "That flesh is heir to,—'tis a consummation",
        "Devoutly to be wish'd. To die, to sleep;—",
        "To sleep, perchance to dream:—ay, there's the rub;",
        "For in that sleep of death what dreams may come,",
        "When we have shuffled off this mortal coil,",
        "Must give us pause: there's the respect",
        "That makes calamity of so long life;",
        "For who would bear the whips and scorns of time,",
        "The oppressor's wrong, the proud man's contumely,",
        "The pangs of despis'd love, the law's delay,",
        "The insolence of office, and the spurns",
        "That patient merit of the unworthy takes,",
        "When he himself might his quietus make",
        "With a bare bodkin? who would these fardels bear,",
        "To grunt and sweat under a weary life,",
        "But that the dread of something after death,—",
        "The undiscover'd country, from whose bourn",
        "No traveller returns,—puzzles the will,",
        "And makes us rather bear those ills we have",
        "Than fly to others that we know naught of?",
        "Thus conscience does make cowards of us all;",
        "And thus the native hue of resolution",
        "Is sicklied o'er with the pale cast of thought;",
        "And enterprises of great pith and moment,",
        "With this regard, their currents turn awry,",
        "And lose the name of action.").collect(Collectors.toList());

    List<String> numberedQuotes(List<String> quotes) {
        List<String> numberedQuotes = new ArrayList<String>();
        ListIterator<String> iter = quotes.listIterator();
        while (iter.hasNext()) {
            numberedQuotes.add(String.format("Quote %s: %s", iter.nextIndex(), iter.next()));
        }
        return numberedQuotes;
    }

    // Since Java lacks a library method to infinitely generate collection elements...
    Stream<String> quoteGenerator = Stream.generate(() -> {
        List<String> numberedQuotes = numberedQuotes(FakeRequest.quotes);
        Random random = new Random();
        int randomNumber = random.nextInt(numberedQuotes.size());
        return numberedQuotes.get(randomNumber);
    });

    public String get(String url) throws InterruptedException, ConnectException {
        if (url != FakeRequest.expectedURL) {
            System.out.printf("FakeRequests only simulates requests to %s\n", FakeRequest.expectedURL);
            throw new ConnectException();
        }
        Iterator<String> iter = this.quoteGenerator.iterator();
        String quote = iter.next();
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
        return quote;
    }
}
