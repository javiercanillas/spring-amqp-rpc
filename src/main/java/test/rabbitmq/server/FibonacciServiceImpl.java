package test.rabbitmq.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.rabbitmq.model.FibonacciService;
import test.rabbitmq.model.StopitService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service("fibonacci.service")
public class FibonacciServiceImpl implements FibonacciService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FibonacciServiceImpl.class);
    private static final Random RND = new Random();

    private int percentageToAskForStop;
    private int delayBetweenCalculationsInMillis;

    public FibonacciServiceImpl(@Value("${server.percentageToAskForStop}") int percentageToAskForStop,
                                @Value("${server.delayBetweenCalculationsInMillis}") int delayBetweenCalculationsInMillis) {
        if (percentageToAskForStop <= 0 && percentageToAskForStop >= 100) {
            LOGGER.warn("Wrong value for percentageToAskForStop, using defaults");
            this.percentageToAskForStop = 2;
        } else {
            this.percentageToAskForStop = percentageToAskForStop;
        }

        if (delayBetweenCalculationsInMillis <= 0 && delayBetweenCalculationsInMillis >= 100) {
            LOGGER.warn("Wrong value for delayBetweenCalculationsInMillis, using defaults");
            this.delayBetweenCalculationsInMillis = 5;
        } else {
            this.delayBetweenCalculationsInMillis = delayBetweenCalculationsInMillis;
        }
    }
    @Autowired
    private StopitService stopitService;

    @Override
    public List<Long> calculateFirst(int terms) {
        boolean stopit = false;
        LOGGER.info("Called with {} terms", terms);
        if (terms <= 0) {
            return Collections.emptyList();
        }
        List<Long> series = new ArrayList<Long>(terms);
        long n = terms, t1 = 0, t2 = 1;
        for (int i = 1; i <= n; ++i)
        {
            try {
                Thread.sleep(this.delayBetweenCalculationsInMillis * i);
            } catch (InterruptedException e) {
                Thread.currentThread().isInterrupted();
                LOGGER.warn("Interrupted");
                break;
            }
            series.add(t1);

            long sum = t1 + t2;
            t1 = t2;
            t2 = sum;

            if (!stopit && RND.nextInt(100) < this.percentageToAskForStop) {
                this.stopitService.stopit();
                LOGGER.info("Asking to stop!");
                stopit = true;
            }
        }
        return series;
    }
}
